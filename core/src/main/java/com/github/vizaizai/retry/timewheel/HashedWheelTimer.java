package com.github.vizaizai.retry.timewheel;

import com.github.vizaizai.logging.LoggerFactory;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * 时间轮定时器（精度：ms）
 * @author liaochongwei
 * @date 2022/1/4 14:20
 */
public class HashedWheelTimer implements Timer{
    private static final Logger log = LoggerFactory.getLogger(HashedWheelTimer.class);
    /**
     * tick最大值
     */
    private static final int MAXIMUM_TICK = 1 << 30;
    /**
     * 时间间隔（ms）
     */
    private final long tickDuration;
    /**
     * 时间格
     */
    private final HashedWheelBucket[] wheel;
    /**
     * 掩码
     */
    private final int mask;
    /**
     * 时间轮启动时间
     */
    private final long startTime;
    /**
     * 等待进入时间轮的任务
     */
    private final Queue<HashedWheelTimeout> waitingTasks =  new LinkedBlockingQueue<>();
    /**
     * 取消的任务
     */
    private final Queue<HashedWheelTimeout> canceledTasks =  new LinkedBlockingQueue<>();
    /**
     * 时间轮转动服务
     */
    private final Worker worker;
    /**
     * 处理任务线程池
     */
    private final ExecutorService taskPool;
    /**
     * 新建时间轮定时器
     * @param tickDuration tick时间间隔（ms）
     * @param ticksPerWheel 时间轮上tick数量
     * @param corePoolSize 处理任务的核心线程数
     * @param maxPoolSize 处理任务的最大线程数
     * @param blockingQueueCapacity 阻塞队列容量（-1表示不限制）
     */
    public HashedWheelTimer(long tickDuration, int ticksPerWheel, int corePoolSize, int maxPoolSize, int blockingQueueCapacity) {
        this.tickDuration = tickDuration;
        // 初始化轮盘，大小格式化为2的N次，可以使用 & 代替取余
        int ticksNum = formatSize(ticksPerWheel);
        // 初始化时间格
        wheel = new HashedWheelBucket[ticksNum];
        for (int i = 0; i < ticksNum; i++) {
            wheel[i] = new HashedWheelBucket();
        }
        // 计算位运算需要的掩码
        mask = wheel.length - 1;

        log.info("Initializing[time-task-pool]...");
        BlockingQueue<Runnable> blockingQueue;
        if (blockingQueueCapacity == -1) {
            blockingQueue = new LinkedBlockingQueue<>();
        }else {
            blockingQueue = new ArrayBlockingQueue<>(blockingQueueCapacity);
        }
        ThreadFactory taskThreadFactory = new BasicThreadFactory.Builder().namingPattern("Time-Task-%d").build();
        taskPool =  new ThreadPoolExecutor(corePoolSize, maxPoolSize,
                100L, TimeUnit.MILLISECONDS,
                blockingQueue,
                taskThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());

        // 初始化启动时间
        startTime = System.currentTimeMillis();
        // 启动时间轮线程
        worker = new Worker();
        log.info("Starting the time wheel worker...");
        new Thread(worker, "HashedWheelTimer-Worker").start();

    }
    @Override
    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        // 执行时间
        long targetTime = System.currentTimeMillis() + unit.toMillis(delay);
        HashedWheelTimeout timeout = new HashedWheelTimeout(task, targetTime);
        // 直接运行到期、过期任务
        if (delay <= 0) {
            runTask(timeout);
            return timeout;
        }
        waitingTasks.add(timeout);
        return timeout;
    }

    @Override
    public Set<TimerTask> stop() {
        worker.stop.set(true);
        try {
            // 关闭任务线程池
            taskPool.shutdown();
            // 等待关闭
            if (!taskPool.awaitTermination(5,TimeUnit.SECONDS)) {
                taskPool.shutdownNow();
            }
        }catch (Exception e) {
            log.error("shutdown failure.",e);
        }
        return worker.getUnprocessedTasks();
    }

    /**
     * 执行任务
     * @param timeout
     */
    private void runTask(HashedWheelTimeout timeout) {
        // 提交到线程池执行
        taskPool.submit(timeout.task());
    }
    /**
     * 将大小格式化为 2的N次
     * @param cap 初始大小
     * @return 格式化后的大小，2的N次
     */
    public static int formatSize(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_TICK) ? MAXIMUM_TICK : n + 1;
    }

    /**
     * 包装TimerTask，维护预期执行时间、总圈数等数据
     */
    private final class HashedWheelTimeout implements Timeout {
        // 预期执行时间
        private final long targetTime;
        private final TimerTask timerTask;
        // 所属的时间格，用于快速删除该任务
        private HashedWheelBucket bucket;
        // 总Tick
        private long totalTicks;

        // 状态枚举值
        // 0-初始化 1-已取消 2-已过期
        private static final int ST_INIT = 0;
        private static final int ST_CANCELLED = 1;
        private static final int ST_EXPIRED = 2;

        private int status = ST_INIT;

        public HashedWheelTimeout(TimerTask timerTask, long targetTime) {
            this.targetTime = targetTime;
            this.timerTask = timerTask;
        }

        @Override
        public TimerTask task() {
            return timerTask;
        }

        @Override
        public boolean isExpired() {
            return status == ST_EXPIRED;
        }

        @Override
        public boolean cancel() {
            if (status == ST_INIT) {
                status = ST_CANCELLED;
                canceledTasks.add(this);
                return true;
            }
            return false;
        }

        @Override
        public boolean isCancelled() {
            return status == ST_CANCELLED;
        }

    }

    /**
     * 时间格（本质就是链表，维护了这个时刻可能需要执行的所有任务）
     */
    private final class HashedWheelBucket extends LinkedList<HashedWheelTimeout> {

        public void expireTimerTasks(long currentTick) {

            removeIf(timeout -> {

                // processCanceledTasks 后外部操作取消任务会导致 BUCKET 中仍存在 CANCELED 任务的情况
                if (timeout.status == HashedWheelTimeout.ST_CANCELLED) {
                    return true;
                }

                if (timeout.status != HashedWheelTimeout.ST_INIT) {
                    log.warn("[HashedWheelTimer] impossible, please fix the bug");
                    return true;
                }

                // 本轮直接调度
                if (timeout.totalTicks <= currentTick) {
                    if (timeout.totalTicks < currentTick) {
                        log.warn("[HashedWheelTimer] timeout.totalTicks < currentTick, please fix the bug");
                    }
                    try {
                        // 提交执行
                        runTask(timeout);
                    }catch (Exception ignore) {
                    } finally {
                        timeout.status = HashedWheelTimeout.ST_EXPIRED;
                    }
                    return true;
                }

                return false;
            });

        }
    }


    /**
     * 时间轮工作
     */
    private class Worker implements Runnable {
        private long tick = 0;
        private final AtomicBoolean stop = new AtomicBoolean(false);
        private final CountDownLatch latch = new CountDownLatch(1);
        @Override
        public void run() {

            while (!stop.get()) {
                // 1. 将任务从队列推入时间轮
                putToBucket();
                // 2. 处理取消的任务
                processCanceledTasks();
                // 3. 等待指针跳向下一刻
                waitForNextTick();
                // 4. 执行定时任务
                int index = (int) (tick & mask);
                HashedWheelBucket bucket = wheel[index];
                bucket.expireTimerTasks(tick);
                tick ++;
            }
            latch.countDown();
        }

        /**
         * 时间轮转动
         */
        private void waitForNextTick() {
            // 下一次调度的绝对时间
            long nextTime = startTime + (tick + 1) * tickDuration;
            long sleepTime = nextTime - System.currentTimeMillis();

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                }catch (Exception ignore) {
                }
            }
        }

        /**
         * 处理被取消的任务
         */
        private void processCanceledTasks() {
            while (true) {
                HashedWheelTimeout canceledTask = canceledTasks.poll();
                if (canceledTask == null) {
                    return;
                }
                // 从链表中删除该任务（bucket为null说明还没被正式推入时间格中，不需要处理）
                if (canceledTask.bucket != null) {
                    canceledTask.bucket.remove(canceledTask);
                }
            }
        }

        /**
         * 将队列中的任务推入时间轮中
         */
        private void putToBucket() {
            while (true) {
                HashedWheelTimeout timerTask = waitingTasks.poll();
                if (timerTask == null) {
                    return;
                }
                // 总共的偏移量
                long offset = timerTask.targetTime - startTime;
                // 总共需要走的指针步数
                timerTask.totalTicks = offset / tickDuration;
                // 取余计算 bucket index
                int index = (int) (timerTask.totalTicks & mask);
                HashedWheelBucket bucket = wheel[index];

                // TimerTask 维护 Bucket 引用，用于删除该任务
                timerTask.bucket = bucket;

                if (timerTask.status == HashedWheelTimeout.ST_INIT) {
                    bucket.add(timerTask);
                }
            }
        }

        public Set<TimerTask> getUnprocessedTasks() {
            try {
                latch.await();
            }catch (Exception ignore) {
            }

            Set<TimerTask> tasks = new HashSet<>();

            Consumer<HashedWheelTimeout> consumer = timeout -> {
                if (timeout.status == HashedWheelTimeout.ST_INIT) {
                    tasks.add(timeout.timerTask);
                }
            };

            waitingTasks.forEach(consumer);
            for (HashedWheelBucket bucket : wheel) {
                bucket.forEach(consumer);
            }
            return tasks;
        }
    }
}
