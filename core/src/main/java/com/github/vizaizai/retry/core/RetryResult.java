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
    private RetryContext retryContext;
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
    private Object retryTask;


    public static <T> RetryResult<T> ok(Object retryTask, RetryContext retryContext, T value) {
        RetryResult<T> result = new RetryResult<>();
        result.retryTask = retryTask;
        result.retryContext = retryContext;
        result.value = value;
        return result;
    }
    public static <T> RetryResult<T> fail(Object retryTask, RetryContext retryContext, Throwable cause) {
        RetryResult<T> result = new RetryResult<>();
        result.retryTask = retryTask;
        result.retryContext = retryContext;
        result.cause = cause;
        return result;
    }

    public RetryContext getRetryContext() {
        return retryContext;
    }

    public Object getRetryTask() {
        return retryTask;
    }

    public RetryStatus getStatus() {
        return retryContext.getStatus();
    }

    public T getValue() {
        return value;
    }

    public Throwable getCause() {
        return cause;
    }


}
