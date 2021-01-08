package test;

import com.github.vizaizai.retry.attempt.Modes;
import com.github.vizaizai.retry.core.Retry;
import com.github.vizaizai.retry.exception.RetryException;
import test.impl.MyCallback;
import test.impl.MyProcessor;
import test.impl.MyVProcessor;

import java.time.temporal.ChronoUnit;

/**
 * @author liaochongwei
 * @date 2020/12/15 11:16
 */
public class Test2 {
    /**
     * 测试模式
     * @param args
     */
    public static void main(String[] args) {

        for (int i = 0; i < 20; i++) {
            Retry.inject(new MyProcessor())
                    //.mode(Modes.cron("13,37,58 * * * * ?")) // 不支持年份
                    //.mode(Modes.basic(1))
                    .mode(Modes.arithmetic(1, 0, ChronoUnit.SECONDS))
                    //.mode(Modes.geometric(1D, 2D, ChronoUnit.SECONDS))
                    .max(10)
                    .async(new MyCallback())
                    .retryFor(RetryException.class)
                    .execute();

        }
    }

}
