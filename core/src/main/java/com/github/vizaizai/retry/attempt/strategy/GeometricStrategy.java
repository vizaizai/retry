package com.github.vizaizai.retry.attempt.strategy;

import java.time.LocalDateTime;

/**
 * 等比数列重试间隔策略
 * @author liaochongwei
 * @date 2020/12/9 17:23
 */
public class GeometricStrategy implements Strategy{
    @Override
    public LocalDateTime nextExecutionTime(Integer count) {
        return null;
    }
}
