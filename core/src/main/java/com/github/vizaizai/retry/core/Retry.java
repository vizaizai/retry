package com.github.vizaizai.retry.core;

import com.github.vizaizai.retry.attempt.AttemptContext;
import com.github.vizaizai.retry.attempt.strategy.Strategy;
import com.github.vizaizai.retry.invocation.Callback;
import com.github.vizaizai.retry.invocation.InvocationOperations;
import com.github.vizaizai.retry.invocation.Processor;
import com.github.vizaizai.retry.invocation.VProcessor;
import com.github.vizaizai.retry.util.Assert;
import org.apache.commons.collections.CollectionUtils;

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
     * 注入可能需要重试的方法
     * @param processor 方法
     * @return Retry
     */
    public static <T> Retry<T> inject(Processor<T> processor) {
        Assert.notNull(processor,"processor must be not null");
        Retry<T> retry = new Retry<>();
        retry.retryHandler.setInvocationOps(InvocationOperations.of(processor));
        return retry;
    }

    public static Retry<Void> inject(VProcessor processor) {
        Assert.notNull(processor, "processor must be not null");
        Retry<Void> retry = new Retry<>();
        retry.retryHandler.setInvocationOps(InvocationOperations.of(processor));
        return retry;
    }

    /**
     * 重试预处理
     * @param preHandler
     * @return Retry
     */
    public Retry<T> preHandler(VProcessor preHandler) {
        Assert.notNull(preHandler, "preHandler must be not null");
        this.retryHandler.getInvocationOps().setPreRetryProcessor(preHandler);
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
        if (CollectionUtils.isEmpty(this.retryHandler.getRetryFor())) {
            // 默认发生RuntimeException和Error就触发重试
            this.retryHandler.setRetryFor(Arrays.asList(RuntimeException.class, Error.class));
        }
        this.retryHandler.setAttemptContext(this.attemptContext);
    }


}



