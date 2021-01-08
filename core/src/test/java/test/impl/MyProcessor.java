package test.impl;

import com.github.vizaizai.retry.exception.RetryException;
import com.github.vizaizai.retry.invocation.Processor;
import com.github.vizaizai.retry.loop.TimeLooper;

/**
 * @author liaochongwei
 * @date 2021/1/6 17:31
 */
public class MyProcessor implements Processor<String> {
    @Override
    public String execute() throws Throwable {
        double random = Math.random();
        TimeLooper.sleep(1);
        if (random > 0.1) {
            throw new RetryException("业务代码异常~");
        }
        return "random:" + random;
    }
}
