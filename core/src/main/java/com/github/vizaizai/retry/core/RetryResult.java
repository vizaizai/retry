package com.github.vizaizai.retry.core;

import java.io.Serializable;

/**
 * 异步重试结果
 * @author liaochongwei
 * @date 2020/12/10 17:52
 */
public class RetryResult<T> implements Serializable {
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
    /**
     * 重试处理器
     */
    private Object retryProcessor;


    public static <T> RetryResult<T> ok(Object retryProcessor, RetryStatus status, T value) {
        RetryResult<T> result = new RetryResult<>();
        result.retryProcessor = retryProcessor;
        result.status = status;
        result.value = value;
        return result;
    }
    public static <T> RetryResult<T> fail(Object retryProcessor, RetryStatus status, Throwable cause) {
        RetryResult<T> result = new RetryResult<>();
        result.retryProcessor = retryProcessor;
        result.status = status;
        result.cause = cause;
        return result;
    }

    public Object getRetryProcessor() {
        return retryProcessor;
    }

    public void setRetryProcessor(Object retryProcessor) {
        this.retryProcessor = retryProcessor;
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
