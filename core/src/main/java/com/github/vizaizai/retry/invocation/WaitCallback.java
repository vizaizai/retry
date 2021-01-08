package com.github.vizaizai.retry.invocation;

import java.io.Serializable;

/**
 * 等待回调函数
 * @author liaochongwei
 * @date 2020/12/8 15:34
 */
@FunctionalInterface
public interface WaitCallback extends Serializable {

    void complete();
}
