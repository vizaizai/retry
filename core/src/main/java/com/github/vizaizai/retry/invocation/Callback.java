package com.github.vizaizai.retry.invocation;

import com.github.vizaizai.retry.core.RetryResult;

import java.io.Serializable;

/**
 * 重试回调函数，T为返回值类型
 * @author liaochongwei
 * @date 2020/12/8 15:34
 */
@FunctionalInterface
public interface Callback<T> extends Serializable {

    void complete(RetryResult<T> result);
}
