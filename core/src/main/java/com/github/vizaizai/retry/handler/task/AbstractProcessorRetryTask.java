package com.github.vizaizai.retry.handler.task;

import com.github.vizaizai.retry.core.RetryContext;
import com.github.vizaizai.retry.core.RetryResult;
import com.github.vizaizai.retry.handler.PostRetryHandler;
import com.github.vizaizai.retry.handler.AbstractRetryTask;
import com.github.vizaizai.retry.handler.PreRetryHandler;

/**
 * 处理器重试任务
 * @author liaochongwei
 * @date 2022/3/1 16:34
 */
public abstract class AbstractProcessorRetryTask<T> extends AbstractRetryTask<T> {
    /**
     * 重试预处理
     */
    protected PreRetryHandler preHandler;
    /**
     * 重试异步处理
     */
    protected PostRetryHandler<T> postHandler;

    @Override
    public void preHandle(RetryContext retryContext) throws Throwable {
        if (this.preHandler != null) {
            this.preHandler.execute(retryContext);
        }
    }

    @Override
    public void postHandle(RetryResult<T> result) {
        if (this.postHandler != null) {
            this.postHandler.complete(result);
        }
    }

    public abstract Object getProcessor();

    public PreRetryHandler getPreHandler() {
        return preHandler;
    }

    public void setPreHandler(PreRetryHandler preHandler) {
        this.preHandler = preHandler;
    }

    public PostRetryHandler<T> getPostHandler() {
        return postHandler;
    }

    public void setPostHandler(PostRetryHandler<T> postHandler) {
        this.postHandler = postHandler;
    }
}
