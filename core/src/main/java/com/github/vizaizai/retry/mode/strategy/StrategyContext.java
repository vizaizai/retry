package com.github.vizaizai.retry.mode.strategy;

import java.time.LocalDateTime;

/**
 * 上下文
 * @author liaochongwei
 * @date 2020/12/8 15:58
 */
public class StrategyContext {
    /**
     * 间隔模式策略
     */
    private final Strategy strategy;

    public StrategyContext(Strategy strategy) {
        this.strategy = strategy;
    }

    public LocalDateTime nextExecutionTime(Integer count) {
        return strategy.nextExecutionTime(count);
    }

    public Strategy getStrategy() {
        return strategy;
    }
}
