package com.github.vizaizai.retry.invocation;

import java.io.Serializable;

/**
 * 回调函数
 * @author liaochongwei
 * @date 2020/12/8 15:34
 */
@FunctionalInterface
public interface VCallback extends Serializable {

    void complete();
}
