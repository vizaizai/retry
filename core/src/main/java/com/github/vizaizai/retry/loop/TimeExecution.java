package com.github.vizaizai.retry.loop;

import com.github.vizaizai.retry.invocation.WaitCallback;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author liaochongwei
 * @date 2020/12/9 10:28
 */
public class TimeExecution {
    /**
     * 标识
     */
    private final String id;
    /**
     * 执行时间
     */
    private final LocalDateTime exeTime;
    /**
     * 到达通知
     */
    private final WaitCallback callback;
    /**
     * 延时时间
     */
    private long millis;


    public TimeExecution(LocalDateTime exeTime, WaitCallback callback) {
        this.exeTime = exeTime;
        this.callback = callback;
        this.id = UUID.randomUUID().toString();
    }

    public LocalDateTime getExeTime() {
        return exeTime;
    }

    public WaitCallback getCallback() {
        return callback;
    }

    public String getId() {
        return id;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }
}
