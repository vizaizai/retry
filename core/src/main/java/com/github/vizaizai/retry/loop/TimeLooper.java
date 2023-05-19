package com.github.vizaizai.retry.loop;

import com.github.vizaizai.logging.LoggerFactory;
import com.github.vizaizai.retry.handler.WaitCallback;
import com.github.vizaizai.retry.timewheel.HashedWheelTimer;
import com.github.vizaizai.retry.timewheel.TimerTask;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * 时间循环
 * @author liaochongwei
 * @date 2020/12/9 9:31
 */
public class TimeLooper {
    private static final Logger log = LoggerFactory.getLogger(TimeLooper.class);
    /**
     * 时间轮，100ms走一格
     */
    private static final HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(100,4096, Runtime.getRuntime().availableProcessors(), 200, -1);
    private TimeLooper() {
    }

    public static void wait(LocalDateTime exeTime) {
        // 延时时间，毫秒
        long delayTime = LocalDateTime.now().until(exeTime, ChronoUnit.MILLIS);
        // 小于毫秒则忽略
        if (delayTime <= 0) {
            return;
        }
        sleep(delayTime);
    }

    /**
     * 异步等待
     * @param exeTime 执行时间
     * @param callback 回调
     */
    @Deprecated
    public static void asyncWait(LocalDateTime exeTime, WaitCallback callback) {
        // 延时时间，毫秒
        long delayTime = LocalDateTime.now().until(exeTime, ChronoUnit.MILLIS);
        // 同步判断一次
        if (delayTime < 10) {
            callback.complete();
            return;
        }
        TimeExecution execution = new TimeExecution(exeTime, callback);
        DelayTaskHelper.doAsyncDelay(delayTime, execution);
    }

    /**
     *
     * @param exeTime 执行时间
     * @param timerTask 时间任务
     */
    public static void asyncWaitV2(LocalDateTime exeTime, TimerTask timerTask) {
        // 延时时间，毫秒
        long delayTime = LocalDateTime.now().until(exeTime, ChronoUnit.MILLIS);
        // 推到时间轮等待回调
        hashedWheelTimer.newTimeout(timerTask, delayTime, TimeUnit.MILLISECONDS);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        }catch (Exception e) {
            log.error("Thread sleep exception：{}", e.getMessage());
        }
    }
}
