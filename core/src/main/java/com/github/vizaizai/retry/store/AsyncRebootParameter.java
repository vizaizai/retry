package com.github.vizaizai.retry.store;

import com.github.vizaizai.retry.attempt.strategy.Strategy;
import com.github.vizaizai.retry.invocation.Callback;
import com.github.vizaizai.retry.invocation.RProcessor;
import com.github.vizaizai.retry.invocation.VProcessor;

import java.io.Serializable;
import java.util.List;

/**
 * 异步重启参数
 * @author liaochongwei
 * @date 2021/1/7 14:36
 */
public class AsyncRebootParameter implements Serializable {

    private RProcessor<?> rProcessor;

    private VProcessor vProcessor;

    private Integer maxAttempts;

    private Strategy mode;

    private List<Class<? extends Throwable>> retryFor;

    private Callback<?> callback;

    private String classKey;

    public RProcessor<?> getProcessor() {
        return rProcessor;
    }

    public void setRProcessor(RProcessor<?> rProcessor) {
        this.rProcessor = rProcessor;
    }

    public VProcessor getVProcessor() {
        return vProcessor;
    }

    public void setVProcessor(VProcessor vProcessor) {
        this.vProcessor = vProcessor;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Strategy getMode() {
        return mode;
    }

    public void setMode(Strategy mode) {
        this.mode = mode;
    }

    public List<Class<? extends Throwable>> getRetryFor() {
        return retryFor;
    }

    public void setRetryFor(List<Class<? extends Throwable>> retryFor) {
        this.retryFor = retryFor;
    }

    public Callback<?> getCallback() {
        return callback;
    }

    public void setCallback(Callback<?> callback) {
        this.callback = callback;
    }

    public String getClassKey() {
        return classKey;
    }

    public void setClassKey(String classKey) {
        this.classKey = classKey;
    }
}
