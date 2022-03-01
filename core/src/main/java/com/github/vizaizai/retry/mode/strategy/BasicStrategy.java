package com.github.vizaizai.retry.mode.strategy;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 基础计算间隔策略
 * @author liaochongwei
 * @date 2020/12/9 16:06
 */
public class BasicStrategy implements Strategy {

    /**
     * 间隔时间(s)
     */
    private Integer intervalTime;

    @Override
    public LocalDateTime nextExecutionTime(Integer count) {
        if (intervalTime == null) {
            return LocalDateTime.now();
        }
        return LocalDateTime.now().plus(this.intervalTime, ChronoUnit.SECONDS);
    }

    public Integer getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Integer intervalTime) {
        this.intervalTime = intervalTime;
    }
}
