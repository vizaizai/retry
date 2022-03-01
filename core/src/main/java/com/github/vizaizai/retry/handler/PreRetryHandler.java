package com.github.vizaizai.retry.handler;

import com.github.vizaizai.retry.core.RetryContext;

import java.io.Serializable;

/**
 * 重试预处理
 * @author liaochongwei
 * @date 2022/2/8 14:32
 */
@FunctionalInterface
public interface PreRetryHandler extends Serializable {

    void execute(RetryContext retryContext) throws Throwable;
}
