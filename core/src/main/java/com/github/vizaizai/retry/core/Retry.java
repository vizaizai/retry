package com.github.vizaizai.retry.core;

import com.github.vizaizai.retry.attempt.AttemptContext;
import com.github.vizaizai.retry.attempt.strategy.Strategy;
import com.github.vizaizai.retry.invocation.*;
import com.github.vizaizai.retry.invocation.spring.BeanHelper;
import com.github.vizaizai.retry.util.Assert;
import com.github.vizaizai.retry.util.CollUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author liaochongwei
 * @date 2020/12/8 11:29
 */
public class Retry<T> {

    /**
     * 重试环境
     */
    private final RetryHandler<T> retryHandler;
    /**
     * 尝试环境
     */
    private final AttemptContext attemptContext;


    private Retry() {
        retryHandler = new RetryHandler<>();
        this.attemptContext = new AttemptContext(3);
    }
    /**
     * 注入可能需要重试的任务
     * @param rProcessor 方法
     * @return Retry
     */
    public static <T> Retry<T> inject(RProcessor<T> rProcessor) {
        Assert.notNull(rProcessor,"processor must be not null");
        Retry<T> retry = new Retry<>();
        retry.retryHandler.setRetryProcessor(RetryProcessor.of(rProcessor));
        return retry;
    }

    public static Retry<Void> inject(VProcessor vProcessor) {
        Assert.notNull(vProcessor, "processor must be not null");
        Retry<Void> retry = new Retry<>();
        retry.retryHandler.setRetryProcessor(RetryProcessor.of(vProcessor));
        return retry;
    }

    /**
     * 设置重试任务Bean
     * @param clazz BeanClass
     * @param args 重试任务执行参数
     * @return Retry
     */
    public static <T> Retry<T> withBean(Class<? extends RetryTask<T>> clazz, Object ...args) {
        Assert.notNull(clazz, "bean clazz must be not null");
        Retry<T> retry = new Retry<>();
        RetryTask<T> retryTask = BeanHelper.getBean(clazz);

        retryTask.preHandle();

        //retry.retryHandler.setRetryProcessor(RetryProcessor.of());

        return retry;
    }

    /**
     * 重试任务名称
     * @param name 名称
     * @return Retry
     */
    public Retry<T> name(String name) {
        this.retryHandler.getRetryProcessor().setName(name);
        return this;
    }
    /**
     * 重试预处理
     * @param preHandler 预处理逻辑
     * @return Retry
     */
    public Retry<T> preHandler(VProcessor preHandler) {
        Assert.notNull(preHandler, "pre-handler must be not null");
        this.retryHandler.getRetryProcessor().setPreRetryProcessor(preHandler);
        return this;
    }
    /**
     * 设置最大重试次数
     * @param maxAttempts 最大重试次数
     * @return Retry
     */
    public Retry<T> max(Integer maxAttempts) {
        Assert.notNull(maxAttempts,"mode must be not null");
        this.attemptContext.setMaxAttempts(maxAttempts);
        return this;
    }
    /**
     * 设置重试间隔模式
     * @param mode mode
     * @return Retry
     */
    public Retry<T> mode(Strategy mode) {
        Assert.notNull(mode,"mode must be not null");
        this.attemptContext.setIntervalStrategy(mode);
        return this;
    }
    /**
     * 重试条件
     * @param retryFor 异常
     * @return Retry
     */
    public Retry<T> retryFor(List<Class<? extends Throwable>> retryFor) {
        Assert.notEmpty(retryFor,"retryFor must be not null");
        this.retryHandler.setRetryFor(retryFor);
        return this;
    }
    public Retry<T> retryFor(Class<? extends Throwable> retryFor) {
        Assert.notNull(retryFor,"retryFor must be not null");
        this.retryHandler.setRetryFor(Collections.singletonList(retryFor));
        return this;
    }

    /**
     * 设置异步
     * @param callback 回调函数
     * @return Retry
     */
    public Retry<T> async(Callback<T> callback) {
        Assert.notNull(callback,"callback must be not null");
        this.retryHandler.setCallback(callback);
        this.retryHandler.setAsync(true);
        return this;
    }

    public T execute(){
        this.init();
        this.retryHandler.tryInvocation();
        return this.retryHandler.getResult();
    }

    private void init() {
        // 重试点为空
        if (CollUtils.isEmpty(this.retryHandler.getRetryFor())) {
            // 默认发生RuntimeException和Error就触发重试
            this.retryHandler.setRetryFor(Arrays.asList(RuntimeException.class, Error.class));
        }
        this.retryHandler.setAttemptContext(this.attemptContext);
    }


}



