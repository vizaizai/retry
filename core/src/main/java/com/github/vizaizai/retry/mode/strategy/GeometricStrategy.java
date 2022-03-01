package com.github.vizaizai.retry.mode.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 等比数列重试间隔策略
 * @author liaochongwei
 * @date 2020/12/9 17:23
 */
public class GeometricStrategy implements Strategy{

    /**
     * 时间单位
     */
    private ChronoUnit timeUnit;
    /**
     * 首项
     */
    private Double a1;
    /**
     * 公比
     */
    private Double q;
    /**
     * 项数
     */
    private Integer n;
    @Override
    public LocalDateTime nextExecutionTime(Integer count) {
        n = count;
        this.check();
        // 计算第n项的值，即为需要本次重试的间隔时间
        double an = a1 *  Math.pow(q, n - 1);
        // 计算下次执行时间
        LocalDateTime exTime = LocalDateTime.now();
        // 小于0.5，四舍五入等于0
        if (an >= 0.5) {
            exTime = exTime.plus(BigDecimal.valueOf(an).setScale(0, RoundingMode.HALF_UP).intValue(), timeUnit);
        }
        return exTime;
    }

    private void check() {
        if (a1 == null) a1 = 1D;
        if (q == null) q = 1D;
        if (n == null) n = 1;
        if (timeUnit == null) timeUnit = ChronoUnit.SECONDS;
    }

    public ChronoUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(ChronoUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Double getA1() {
        return a1;
    }

    public void setA1(Double a1) {
        this.a1 = a1;
    }

    public Double getQ() {
        return q;
    }

    public void setQ(Double q) {
        this.q = q;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }
}
