package com.github.vizaizai.retry.attempt;

import com.github.vizaizai.retry.attempt.strategy.Strategy;
import com.github.vizaizai.retry.attempt.strategy.StrategyContext;
import com.github.vizaizai.retry.exception.RetryException;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 尝试上下文
 * @author liaochongwei
 * @date 2020/12/8 17:41
 */
public class AttemptContext implements Serializable {
    /**
     * 已执行次数
     */
    private int attempts = 0;
    /**
     * 最大次数
     */
    private int maxAttempts;
    /**
     * 间隔方式
     */
    private StrategyContext intervalStrategyContext;

    public AttemptContext(int maxAttempts) {
        this.maxAttempts = maxAttempts;
        this.intervalStrategyContext = new StrategyContext(Modes.basic());
    }

    public AttemptContext(int maxAttempts, Strategy intervalStrategy) {
        this.maxAttempts = maxAttempts;
        this.intervalStrategyContext = new StrategyContext(intervalStrategy);
    }

    public boolean isMaximum() {
        return attempts >= maxAttempts;
    }

    public LocalDateTime getNextTime() {
        if (isMaximum()) {
            throw new RetryException("maximum number of retries");
        }
        attempts++;
        return intervalStrategyContext.nextExecutionTime(this.attempts);
    }

    public int getAttempts() {
        return attempts;
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
}
