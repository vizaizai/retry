package test.impl;

import com.github.vizaizai.retry.exception.RetryException;
import com.github.vizaizai.retry.handler.VProcessor;
import com.github.vizaizai.retry.loop.TimeLooper;

/**
 * @author liaochongwei
 * @date 2021/1/6 17:31
 */
public class MyVProcessor implements VProcessor {
    @Override
    public void execute() throws Throwable {
        double random = Math.random();
        TimeLooper.sleep(1);
        if (random > 0.1) {
            throw new RetryException("发生错误啦");
        }
    }
}
