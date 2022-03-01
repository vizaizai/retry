package test.impl;

import com.github.vizaizai.retry.handler.VProcessor;

/**
 * @author liaochongwei
 * @date 2022/1/14 10:14
 */
public class Test4VProcessor implements VProcessor {
    private final long startTime = System.currentTimeMillis();
    @Override
    public void execute() throws Throwable {
        throw new RuntimeException("错误");
    }

    public long getStartTime() {
        return startTime;
    }
}
