package com.github.vizaizai.retry.handler;

import com.github.vizaizai.retry.core.RetryContext;
import com.github.vizaizai.retry.core.RetryResult;

import java.io.Serializable;

/**
 * 重试任务
 * @author liaochongwei
 * @date 2022/01/18 14:32
 */
public abstract class AbstractRetryTask<T> implements Serializable {
    /**
     * 重试预处理
     */
    public boolean preHandle(RetryContext retryContext) throws Throwable{
        return true;
    }

    /**
     * 待执行重试任务
     * @return T
     */
    public abstract T execute(RetryContext retryContext) throws Throwable;

    /**
     * 重试后置处理（异步回调），争对异步任务
     * @param result 重试结果
     */
    public void postHandle(RetryResult<T> result) {
    }

    /**
     * 是否异步执行（默认是）
     * @return boolean
     */
    public boolean async() {
        return true;
    }

}
