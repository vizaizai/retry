package com.github.vizaizai.retry.handler.task;

import com.github.vizaizai.retry.core.RetryContext;
import com.github.vizaizai.retry.core.RetryResult;
import com.github.vizaizai.retry.handler.PostRetryHandler;
import com.github.vizaizai.retry.handler.RProcessor;
import com.github.vizaizai.retry.handler.VProcessor;

/**
 * 有返回值的处理器重试任务
 * @author liaochongwei
 * @date 2022/3/1 16:34
 */
public class RBaseRetryTask<T> extends AbstractProcessorRetryTask<T> {
    /**
     * 带有返回值的处理
     */
    private RProcessor<T> rProcessor;
    @Override
    public T execute(RetryContext retryContext) throws Throwable{
        return rProcessor.execute();
    }

    public void setProcessor(RProcessor<T> rProcessor) {
        this.rProcessor = rProcessor;
    }
    @Override
    public Object getProcessor() {
        return rProcessor;
    }
}
