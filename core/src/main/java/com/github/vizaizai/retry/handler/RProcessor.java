package com.github.vizaizai.retry.handler;

import java.io.Serializable;

/**
 * 带返回值处理器,T为返回值类型
 * @author liaochongwei
 * @date 2020/12/8 14:32
 */
@FunctionalInterface
public interface RProcessor<T> extends Serializable {

    T execute() throws Throwable;
}
