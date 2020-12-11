package com.github.vizaizai.retry.attempt;

import com.github.vizaizai.retry.attempt.strategy.ArithmeticStrategy;
import com.github.vizaizai.retry.attempt.strategy.BasicStrategy;

import java.time.temporal.ChronoUnit;

/**
 * 间隔模式
 * @author liaochongwei
 * @date 2020/12/8 11:33
 */
public class Modes {
    private Modes() {
    }


    /**
     * 等差模式
     * @param a1 首项
     * @param d 公差
     * @param unit 单位
     * @return ArithmeticStrategy
     */
    public static ArithmeticStrategy arithmetic(Integer a1, Integer d, ChronoUnit unit) {
        ArithmeticStrategy arithmeticStrategy = new ArithmeticStrategy();
        arithmeticStrategy.setA1(a1);
        arithmeticStrategy.setD(d);
        arithmeticStrategy.setTimeUnit(unit);
        return arithmeticStrategy;
    }
    public static ArithmeticStrategy arithmetic() {
        return arithmetic(0,0,ChronoUnit.SECONDS);
    }
    public static ArithmeticStrategy arithmetic(Integer d) {
        return arithmetic(0, d, ChronoUnit.SECONDS);
    }
    public static ArithmeticStrategy arithmetic(Integer d, ChronoUnit unit) {
        return arithmetic(0, d, unit);
    }

    /**
     * 基础模式
     * @param intervalTime 间隔时间(s)
     * @return BasicStrategy
     */
    public static BasicStrategy basic(Integer intervalTime) {
        BasicStrategy basicStrategy = new BasicStrategy();
        basicStrategy.setIntervalTime(intervalTime);
        return basicStrategy;
    }
    public static BasicStrategy basic() {
        return basic(0);
    }


}
