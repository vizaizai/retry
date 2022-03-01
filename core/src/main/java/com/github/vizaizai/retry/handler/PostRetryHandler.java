package com.github.vizaizai.retry.handler;

import com.github.vizaizai.retry.core.RetryResult;

import java.io.Serializable;

/**
 * 重试回调处理，T为返回值类型
 * @author liaochongwei
 * @date 2020/12/8 15:34
 */
@FunctionalInterface
public interface PostRetryHandler<T> extends Serializable {
    void complete(RetryResult<T> result);
}
