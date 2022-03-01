package com.github.vizaizai.retry.core;

import com.github.vizaizai.retry.mode.Modes;
import com.github.vizaizai.retry.mode.strategy.Strategy;
import com.github.vizaizai.retry.mode.strategy.StrategyContext;
import com.github.vizaizai.retry.exception.RetryException;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 重试上下文
 * @author liaochongwei
 * @date 2020/12/8 17:41
 */
public class RetryContext implements Serializable {
    /**
     * 状态
     */
    private RetryStatus status  = RetryStatus.NO_RETRY;
    /**
     * 已执行次数
     */
    private int attempts = 0;
    /**
     * 最大次数
     */
    private int maxAttempts;
    /**
     * 重试参数
     */
    private Object[] args;
    /**
     * 间隔方式
     */
    private StrategyContext intervalStrategyContext;

    public RetryContext(int maxAttempts) {
        this.maxAttempts = maxAttempts;
        this.intervalStrategyContext = new StrategyContext(Modes.basic());
    }

    public RetryContext(int maxAttempts, Strategy intervalStrategy) {
        this.maxAttempts = maxAttempts;
        this.intervalStrategyContext = new StrategyContext(intervalStrategy);
    }

    public boolean isMaximum() {
        return attempts >= maxAttempts;
    }

    /**
     * 获取下一次重试时间
     * @return LocalDateTime
     */
    public LocalDateTime getNextRetryTime() {
        if (isMaximum()) {
            throw new RetryException("maximum number of retries");
        }
        attempts++;
        return intervalStrategyContext.nextExecutionTime(this.attempts);
    }

    public int getAttempts() {
        return attempts;
    }

    public void setStatus(RetryStatus status) {
        this.status = status;
    }

    public RetryStatus getStatus() {
        return status;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public StrategyContext getIntervalStrategyContext() {
        return intervalStrategyContext;
    }

    public void setIntervalStrategy(Strategy strategy) {
        this.intervalStrategyContext = new StrategyContext(strategy);
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
