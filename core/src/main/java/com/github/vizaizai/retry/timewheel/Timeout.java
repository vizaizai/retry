package com.github.vizaizai.retry.timewheel;

/**
 * @author liaochongwei
 * @date 2022/1/4 14:10
 */
public interface Timeout {
    TimerTask task();

    boolean isExpired();

    boolean isCancelled();

    boolean cancel();
}
