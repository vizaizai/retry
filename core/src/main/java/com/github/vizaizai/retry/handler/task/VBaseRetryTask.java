package com.github.vizaizai.retry.handler.task;

import com.github.vizaizai.retry.core.RetryContext;
import com.github.vizaizai.retry.handler.VProcessor;

/**
 * 无有返回值的处理器重试任务
 * @author liaochongwei
 * @date 2022/3/1 16:34
 */
public class VBaseRetryTask extends AbstractProcessorRetryTask<Void> {
    /**
     * 无有返回值的处理
     */
    private VProcessor vProcessor;

    @Override
    public Void execute(RetryContext retryContext) throws Throwable{
        vProcessor.execute();
        return null;
    }

    public void setProcessor(VProcessor vProcessor) {
        this.vProcessor = vProcessor;
    }

    public Object getProcessor() {
        return vProcessor;
    }
}
