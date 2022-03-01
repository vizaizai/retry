package com.github.vizaizai.retry.mode.strategy;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 等差数列重试间隔策略
 * @author liaochongwei
 * @date 2020/12/9 16:06
 */
public class ArithmeticStrategy implements Strategy {

    /**
     * 时间单位
     */
    private ChronoUnit timeUnit;
    /**
     * 首项
     */
    private Integer a1;
    /**
     * 公差
     */
    private Integer d;
    /**
     * 项数
     */
    private Integer n;


    @Override
    public LocalDateTime nextExecutionTime(Integer count) {
        n = count;
        this.check();
        // 计算第n项的值，即为需要本次重试的间隔时间
        int an = a1 + (n - 1) * d;
        // 计算下次执行时间
        LocalDateTime exTime = LocalDateTime.now();
        if (an > 0) {
            exTime = exTime.plus(an, timeUnit);
        }
        return exTime;
    }

    private void check() {
        if (a1 == null) a1 = 0;
        if (d == null) d = 0;
        if (n == null) n = 1;
        if (timeUnit == null) timeUnit = ChronoUnit.SECONDS;
    }

    public ChronoUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(ChronoUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Integer getA1() {
        return a1;
    }

    public void setA1(Integer a1) {
        this.a1 = a1;
    }

    public Integer getD() {
        return d;
    }

    public void setD(Integer d) {
        this.d = d;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }
}
