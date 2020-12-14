package com.github.vizaizai.retry.loop;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

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

    static {
        ThreadFactory callbackThreadFactory = new BasicThreadFactory.Builder().namingPattern("loop-callback-thread-%d").build();
        callbackExecutorService =  new ThreadPoolExecutor(0, 50,
                                                            100L, TimeUnit.MILLISECONDS,
                                                            new LinkedBlockingQueue<>(50),
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
                callbackExecutorService.execute(()-> this.timeExecution.getCallback().complete());
            }catch (Exception e) {
                this.timeExecution.getCallback().complete();
            }
        }

        public TimeExecution getTimeExecution() {
            return timeExecution;
        }
    }
}
