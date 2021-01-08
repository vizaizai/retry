package com.github.vizaizai.retry.core;

import java.io.Serializable;

/**
 * 异步回调结果
 * @author liaochongwei
 * @date 2020/12/10 17:52
 */
public class CallBackResult<T> implements Serializable {
    /**
     * 重试状态
     */
    private RetryStatus status;
    /**
     * 返回结果
     */
    private T value;
    /**
     * 异常
     */
    private Throwable cause;


    public static <T> CallBackResult<T> ok(RetryStatus status, T value) {
        CallBackResult<T> result = new CallBackResult<>();
        result.status = status;
        result.value = value;
        return result;
    }
    public static <T> CallBackResult<T> fail(RetryStatus status, Throwable cause) {
        CallBackResult<T> result = new CallBackResult<>();
        result.status = status;
        result.cause = cause;
        return result;
    }

    public RetryStatus getStatus() {
        return status;
    }

    public void setStatus(RetryStatus status) {
        this.status = status;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
}
