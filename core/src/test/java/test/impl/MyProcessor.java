package test.impl;

import com.github.vizaizai.retry.exception.RetryException;
import com.github.vizaizai.retry.invocation.RProcessor;
import com.github.vizaizai.retry.loop.TimeLooper;

/**
 * @author liaochongwei
 * @date 2021/1/6 17:31
 */
public class MyProcessor implements RProcessor<String> {
    @Override
    public String execute() throws Throwable {
        double random = Math.random();
        TimeLooper.sleep(1);
        System.out.println(random+"----------");
        if (random > 0.4) {
            throw new RetryException("业务代码异常~");
        }
        return "random:" + random;
    }
}
