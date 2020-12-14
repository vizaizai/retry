package com.github.vizaizai.retry.invocation;

import java.io.Serializable;

/**
 * @author liaochongwei
 * @date 2020/12/8 14:32
 */
@FunctionalInterface
public interface VProcessor extends Serializable {
    void execute() throws Throwable;
}
