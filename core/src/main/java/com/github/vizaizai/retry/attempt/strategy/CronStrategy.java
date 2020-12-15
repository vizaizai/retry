package com.github.vizaizai.retry.attempt.strategy;

import com.github.vizaizai.retry.attempt.CronSequenceGenerator;
import com.github.vizaizai.retry.exception.RetryException;
import com.github.vizaizai.retry.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * cron表达式重试间隔策略
 * @author liaochongwei
 * @date 2020/12/9 16:06
 */
public class CronStrategy implements Strategy {

    /**
     * cron表达式
     */
    private final String expression;
    /**
     * cron生成器
     */
    private final CronSequenceGenerator cronSequenceGenerator;

    public CronStrategy(String expression) {
        Assert.notNull(expression, "The expression must be not null");
        this.expression = expression;
        cronSequenceGenerator = new CronSequenceGenerator(this.expression);
    }

    @Override
    public LocalDateTime nextExecutionTime(Integer count) {
        try {
            Date next = cronSequenceGenerator.next(new Date());
            return LocalDateTime.ofInstant(next.toInstant(), ZoneId.systemDefault());
        }catch (Exception e) {
            throw new RetryException("Cron expression parsing failed",e);
        }
    }

    public String getExpression() {
        return expression;
    }
}
