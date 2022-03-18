package com.github.vizaizai.retry.core;

import com.github.vizaizai.retry.mode.strategy.Strategy;
import com.github.vizaizai.retry.handler.*;
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
    private final RetryContext retryContext;


    private Retry() {
        retryHandler = new RetryHandler<>();
        this.retryContext = new RetryContext(3);
    }
    /**
     * 注入可能需要重试的任务
     * @param rProcessor 方法
     * @return Retry
     */
    public static <T> Retry<T> inject(RProcessor<T> rProcessor, Object ...args) {
        Assert.notNull(rProcessor,"processor must be not null");
        Retry<T> retry = new Retry<>();
        retry.retryContext.setArgs(args);
        retry.retryHandler.setRetryProcessor(RetryProcessor.of(rProcessor,retry.retryContext));
        return retry;
    }

    public static Retry<Void> inject(VProcessor vProcessor, Object ...args) {
        Assert.notNull(vProcessor, "processor must be not null");
        Retry<Void> retry = new Retry<>();
        retry.retryContext.setArgs(args);
        retry.retryHandler.setRetryProcessor(RetryProcessor.of(vProcessor, retry.retryContext));

        return retry;
    }
    /**
     * 注入重试任务
     * @param retryTask 重试任务
     * @param args 重试任务执行参数
     * @return Retry
     */
    public static <T> Retry<T> injectTask(AbstractRetryTask<T> retryTask, Object ...args) {
        Retry<T> retry = new Retry<>();
        retry.retryContext.setArgs(args);
        retry.retryHandler.setRetryProcessor(RetryProcessor.of(retryTask, retry.retryContext));
        retry.retryHandler.setAsync(retryTask.async());
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
    public Retry<T> preHandler(PreRetryHandler preHandler) {
        Assert.notNull(preHandler, "pre-handler must be not null");
        this.retryHandler.getRetryProcessor().setPreHandler(preHandler);
        return this;
    }
    /**
     * 设置最大重试次数
     * @param maxAttempts 最大重试次数
     * @return Retry
     */
    public Retry<T> max(Integer maxAttempts) {
        Assert.notNull(maxAttempts,"maxAttempts must be not null");
        this.retryContext.setMaxAttempts(maxAttempts);
        return this;
    }

    /**
     *
     * @param maxAttempts 最大重试次数
     * @param offset 已执行次数(默认为零)
     * @return Retry
     */
    public Retry<T> max(Integer maxAttempts, Integer offset) {
        Assert.notNull(maxAttempts,"maxAttempts must be not null");
        this.retryContext.setMaxAttempts(maxAttempts);
        if (offset != null && offset >=0 ) {
            this.retryContext.setAttempts(offset);
        }
        return this;
    }

    /**
     * 设置重试间隔模式
     * @param mode mode
     * @return Retry
     */
    public Retry<T> mode(Strategy mode) {
        Assert.notNull(mode,"mode must be not null");
        this.retryContext.setIntervalStrategy(mode);
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
     * @param posHandler 重试后置回调
     * @return Retry
     */
    public Retry<T> async(PostRetryHandler<T> posHandler) {
        Assert.notNull(posHandler,"posHandler must be not null");
        this.retryHandler.getRetryProcessor().setPostHandler(posHandler);
        this.retryHandler.setAsync(true);
        return this;
    }

    public T execute(){
        this.init();
        this.retryHandler.tryExecute();
        return this.retryHandler.getResult();
    }

    private void init() {
        // 重试点为空
        if (CollUtils.isEmpty(this.retryHandler.getRetryFor())) {
            // 默认发生RuntimeException和Error就触发重试
            this.retryHandler.setRetryFor(Arrays.asList(RuntimeException.class, Error.class));
        }
        this.retryHandler.setRetryContext(this.retryContext);
    }



}



