package com.github.vizaizai.retry.loop;

import com.github.vizaizai.retry.util.Utils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 延迟任务工具
 * @author liaochongwei
 * @date 2020/12/9 10:04
 */
public class DelayTaskHelper {

    private DelayTaskHelper() {
    }
    /**
     * 回调函数线程池
     */
    private static final ExecutorService callbackExecutorService;
    /**
     * 延迟线程池
     */
    private static final ScheduledExecutorService delayExecutorService;
    /**
     * 回调函数数量
     */
    private static final AtomicInteger callbackAmount = new AtomicInteger(0);
    /**
     * 线程池最大长度
     */
    private static final Integer MAXIMUM_POOL_SIZE = 50;
    /**
     * 阻塞队列容量
     */
    private static final Integer BLOCKING_QUEUE_CAPACITY = 50;
    static {
        ThreadFactory callbackThreadFactory = new BasicThreadFactory.Builder().namingPattern("loop-callback-thread-%d").build();
        callbackExecutorService =  new ThreadPoolExecutor(0, MAXIMUM_POOL_SIZE,
                                                            100L, TimeUnit.MILLISECONDS,
                                                            new LinkedBlockingQueue<>(BLOCKING_QUEUE_CAPACITY),
                                                            callbackThreadFactory);

        ThreadFactory delayThreadFactory = new BasicThreadFactory.Builder().namingPattern("loop-thread-%d").build();
        delayExecutorService = Executors.newScheduledThreadPool(2, delayThreadFactory);
    }

    /**
     * 异步延迟
     * @param millis 时间(毫秒)
     * @param execution 执行器
     */
    public static void doAsyncDelay(long millis, TimeExecution execution) {
        execution.setMillis(millis);
        delayExecutorService.schedule(new DelayTask(execution), millis, TimeUnit.MILLISECONDS);
    }

     static class DelayTask implements Runnable {

        private final TimeExecution timeExecution;

        public DelayTask(TimeExecution timeExecution) {
            this.timeExecution = timeExecution;
        }

        @Override
        public void run() {
            try {
                synchronized (DelayTask.class) {
                    // 待回调数小于线程最大数加延迟队列容量时，可以执行新任务
                    if (callbackAmount.get() < MAXIMUM_POOL_SIZE + BLOCKING_QUEUE_CAPACITY) {
                        callbackExecutorService.execute(()-> {
                            this.timeExecution.getCallback().complete();
                            callbackAmount.decrementAndGet();
                        });
                        callbackAmount.incrementAndGet();
                        return;

                    }
                }
                // 重新进入等待队列
                double factor =  Utils.getRandom(20,5) * 1.0 / 10;
                doAsyncDelay((long) (this.timeExecution.getMillis() * factor), this.timeExecution);

            }catch (Exception e) {
                this.timeExecution.getCallback().complete();
            }
        }

        public TimeExecution getTimeExecution() {
            return timeExecution;
        }
    }
}
