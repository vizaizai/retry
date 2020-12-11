package com.github.vizaizai.retry.attempt.strategy;

import java.time.LocalDateTime;

/**
 * cron表达式重试间隔策略
 * @author liaochongwei
 * @date 2020/12/9 16:06
 */
public class CronStrategy implements Strategy {

    @Override
    public LocalDateTime nextExecutionTime(Integer count) {

        return null;
    }




}
