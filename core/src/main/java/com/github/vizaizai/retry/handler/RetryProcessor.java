package com.github.vizaizai.retry.handler;

import com.github.vizaizai.logging.LoggerFactory;
import com.github.vizaizai.retry.core.RetryContext;
import com.github.vizaizai.retry.core.RetryResult;
import com.github.vizaizai.retry.exception.RetryException;
import com.github.vizaizai.retry.handler.task.AbstractProcessorRetryTask;
import com.github.vizaizai.retry.handler.task.RBaseRetryTask;
import com.github.vizaizai.retry.handler.task.VBaseRetryTask;
import com.github.vizaizai.retry.util.Assert;
import org.slf4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 方法执行器
 * @author liaochongwei
 * @date 2020/12/8 14:49
 */
public class RetryProcessor<T> {
    private static final Logger log = LoggerFactory.getLogger(RetryProcessor.class);
    /**
     * 名称
     */
    private String name;
    /**
     * 重试上下文
     */
    private RetryContext retryContext;
    /**
     * 发生的异常
     */
    private Throwable cause;
    /**
     * 重试任务
     */
    private AbstractRetryTask<T> retryTask;


    public static <T> RetryProcessor<T> of(RProcessor<T> processor,RetryContext retryContext) {
        Assert.notNull(processor, "The processor must be not null");
        RetryProcessor<T> retryProcessor = new RetryProcessor<>();

        RBaseRetryTask<T> baseRetryTask = new RBaseRetryTask<>();
        baseRetryTask.setProcessor(processor);

        // 设置重试任务
        retryProcessor.retryTask = baseRetryTask;
        retryProcessor.retryContext = retryContext;

        return retryProcessor;
    }

    public static RetryProcessor<Void> of(VProcessor processor,RetryContext retryContext) {
        Assert.notNull(processor, "The processor must be not null");
        RetryProcessor<Void> retryProcessor = new RetryProcessor<>();

        VBaseRetryTask baseRetryTask = new VBaseRetryTask();
        baseRetryTask.setProcessor(processor);
        // 设置重试任务
        retryProcessor.retryTask = baseRetryTask;
        retryProcessor.retryContext = retryContext;
        return retryProcessor;
    }

    public static <T> RetryProcessor<T> of(AbstractRetryTask<T> retryTask, RetryContext retryContext) {
        Assert.notNull(retryTask, "The retryTask must be not null");
        RetryProcessor<T> retryProcessor = new RetryProcessor<>();
        retryProcessor.retryTask = retryTask;
        retryProcessor.retryContext = retryContext;
        return retryProcessor;

    }
    /**
     * 抛出异常
     */
    public void throwErr() {
        if (this.cause == null) {
            return;
        }
        if (this.cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
        }
        if (this.cause instanceof Error) {
            throw (Error)cause;
        }
        if (this.cause instanceof Exception) {
            throw new RetryException(cause);
        }
    }

    /**
     * 执行
     * @return T
     */
    public T execute(){
        T result = null;
        try {
            // 预处理
            this.preHandle();
            result = retryTask.execute(this.retryContext);
            this.cause = null;
        }catch (RuntimeException | Error ex) {
            this.cause = ex;
        }catch (Throwable e) {
            log.error("Checked-exception {}:{}", e.getClass().getName(),e.getMessage());
            this.cause = e;
        }
        return result;
    }

    /**
     * 后置处理（异步回调）
     * @param retryResult 重试结果
     */
    public void postHandle(RetryResult<T> retryResult) {
        try {
            this.retryTask.postHandle(retryResult);
        }catch (Throwable throwable) {
            log.error("Retry Post-async-handle error: {}", throwable.getMessage());
        }
    }

    private void preHandle() {
        // 重试预处理
        try {
            this.retryTask.preHandle(this.retryContext);
        }catch (Throwable throwable) {
            log.error("Retry pre-handle error: {}", throwable.getMessage());
        }
    }
    public boolean haveErr() {
        return cause != null;
    }

    public Throwable getCause() {
        return cause;
    }

    /**
     * 获取重试任务
     * @return RetryTask
     */
    public Object getRetryTask() {
       return this.retryTask;
    }


    /**
     * 设置重试预处理器
     * @param preRetryHandler 预处理
     */
    public void setPreHandler(PreRetryHandler preRetryHandler) {
        if (this.retryTask instanceof AbstractProcessorRetryTask) {
            AbstractProcessorRetryTask<T> processorRetryTask = (AbstractProcessorRetryTask<T>) this.retryTask;
            processorRetryTask.setPreHandler(preRetryHandler);
        }
    }

    /**
     * 后置异步处理
     * @param postHandler 后置处理
     */
    public void setPostHandler(PostRetryHandler<T> postHandler) {
        if (this.retryTask instanceof AbstractProcessorRetryTask) {
            AbstractProcessorRetryTask<T> processorRetryTask = (AbstractProcessorRetryTask<T>) this.retryTask;
            processorRetryTask.setPostHandler(postHandler);
        }
    }


    public String getName() {
        return name == null ? "" : name + "-";
    }

    public void setName(String name) {
        this.name = name;
    }
}
