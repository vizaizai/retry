package com.github.vizaizai.retry.core;

import com.github.vizaizai.retry.attempt.AttemptContext;
import com.github.vizaizai.retry.attempt.Modes;
import com.github.vizaizai.retry.attempt.strategy.Strategy;
import com.github.vizaizai.retry.exception.RetryException;
import com.github.vizaizai.retry.invocation.Callback;
import com.github.vizaizai.retry.invocation.InvocationOperations;
import com.github.vizaizai.retry.invocation.Processor;
import com.github.vizaizai.retry.invocation.VProcessor;
import com.github.vizaizai.retry.loop.TimeLooper;
import com.github.vizaizai.retry.util.Assert;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liaochongwei
 * @date 2020/12/8 11:29
 */
public class Retry<T> {
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);
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
     * @return
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
    public Retry<T> async(Callback callback) {
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

    public static void main(String[] args) {


        for (int i = 0; i < 10000; i++) {

            Retry.inject(() -> {
                double random = Math.random();
                TimeLooper.sleep(10);
                if (random > 0.1) {
                    throw new RetryException("发生错误啦");
                }
                //return "hello" + random;
            })
                    .mode(Modes.basic(1))
                    //.mode(Modes.arithmetic(1, 1, ChronoUnit.SECONDS))
                    //.mode(Modes.geometric(1D, 2D, ChronoUnit.SECONDS))
                    .max(3)
                    .async(e-> {
                        atomicInteger.incrementAndGet();
                        System.out.println("callback--------------------:"  + e);
                        TimeLooper.sleep(100);
                    })
                    .retryFor(RetryException.class)
                    .execute();

        }

        while (true) {
            System.out.println("callback count:" + atomicInteger.get());
            TimeLooper.sleep(1000);
        }
        //Thread.sleep(1000000);

    }


    /**
     * try {
     *             //写入字节流
     *             ByteArrayOutputStream out = new ByteArrayOutputStream();
     *             ObjectOutputStream obs = new ObjectOutputStream(out);
     *             obs.writeObject(processor);
     *             obs.close();
     *             System.out.println("a");
     *             //分配内存，写入原始对象，生成新对象
     *             ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
     *             ObjectInputStream ois = new ObjectInputStream(ios);
     *             //返回生成的新对象
     *             processor = (Processor)ois.readObject();
     *             ois.close();
     *             System.out.println("b");
     *         } catch (Exception e) {
     *             e.printStackTrace();
     *             System.out.println("c");
     *         }
     */
}



