package com.github.vizaizai.retry.timewheel;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author liaochongwei
 * @date 2022/1/4 14:02
 */
public interface Timer {

    /**
     * 调度定时任务
     */
    Timeout newTimeout(TimerTask task, long delay, TimeUnit unit);

    /**
     * 停止所有调度任务
     */
    Set<TimerTask> stop();
}
