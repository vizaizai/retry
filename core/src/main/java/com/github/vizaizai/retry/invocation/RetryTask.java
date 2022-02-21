package com.github.vizaizai.retry.invocation;

import com.github.vizaizai.retry.core.RetryResult;

import java.io.Serializable;

/**
 * 重试任务
 * @author liaochongwei
 * @date 2022/01/18 14:32
 */
public abstract class RetryTask<T> implements Serializable {
    /**
     * 重试参数
     */
    private Object[] args;

    /**
     * 重试与处理
     */
    public void preHandle(){
    }

    /**
     * 待执行重试任务
     * @return T
     */
    public abstract T execute();

    /**
     * 重试后置异步处理
     * @param result 重试结果
     */
    public void postAsyncHandle(RetryResult<T> result) {

    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
