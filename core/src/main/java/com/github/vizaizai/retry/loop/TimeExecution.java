package com.github.vizaizai.retry.loop;

import com.github.vizaizai.retry.invocation.VCallback;

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
    private final VCallback callback;
    /**
     * 是否已执行
     */
    private boolean isDone = false;

    public TimeExecution(LocalDateTime exeTime, VCallback callback) {
        this.exeTime = exeTime;
        this.callback = callback;
        this.id = UUID.randomUUID().toString();
    }

    public LocalDateTime getExeTime() {
        return exeTime;
    }

    public VCallback getCallback() {
        return callback;
    }

    public String getId() {
        return id;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}