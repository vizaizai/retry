package com.github.vizaizai.retry.invocation;

import com.github.vizaizai.retry.core.CallBackResult;

import java.io.Serializable;

/**
 * 回调函数
 * @author liaochongwei
 * @date 2020/12/8 15:34
 */
@FunctionalInterface
public interface Callback extends Serializable {

    void complete(CallBackResult result);
}
