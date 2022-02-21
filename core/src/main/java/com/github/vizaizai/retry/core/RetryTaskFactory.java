package com.github.vizaizai.retry.core;

import org.springframework.beans.BeansException;

/**
 * 重试任务工厂
 * @author liaochongwei
 * @date 2022/1/19 11:31
 */
public interface RetryTaskFactory {

    /**
     * 获取任务对象实例
     * @param clazz task class
     * @param <T> T
     * @return T task
     */
    <T> T getTask(Class<T> clazz);
}
