package com.github.vizaizai.retry.invocation;

import com.github.vizaizai.logging.LoggerFactory;
import com.github.vizaizai.retry.exception.RetryException;
import com.github.vizaizai.retry.util.Assert;
import org.slf4j.Logger;

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
     * 带有返回值的处理
     */
    private RProcessor<T> rProcessor;
    /**
     * 无返回值处理
     */
    private VProcessor vProcessor;
    /**
     * 是否有返回值
     */
    private boolean isReturn;
    /**
     * 发生的异常
     */
    private Throwable cause;
    /**
     * 重试前处理
     */
    private VProcessor preRetryProcessor;


    public static <T> RetryProcessor<T> of(RProcessor<T> rProcessor) {
        Assert.notNull(rProcessor, "The processor must be not null");
        RetryProcessor<T> retryProcessor = new RetryProcessor<>();
        retryProcessor.rProcessor = rProcessor;
        retryProcessor.isReturn = true;
        return retryProcessor;
    }

    public static RetryProcessor<Void> of(VProcessor processor) {
        Assert.notNull(processor, "The processor must be not null");
        RetryProcessor<Void> retryProcessor = new RetryProcessor<>();
        retryProcessor.vProcessor = processor;
        retryProcessor.isReturn = false;
        return retryProcessor;
    }

    public static <T> RetryProcessor<T> of(RetryTask<T> retryTask) {
        Assert.notNull(retryTask, "The retryTask must be not null");
        RetryProcessor<T> operations = new RetryProcessor<>();



        return operations;
    }


    public T executeForRetry(){
        this.preHandle();
        return this.execute();
    }
    public T execute(){
        T result = null;
        try {

            if (this.isReturn) {
                result =  this.rProcessor.execute();
            }else {
                this.vProcessor.execute();
            }
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

    private void preHandle() {
        // 重试预处理
        try {
            // 存在预处理，且发生触发过重试
            if(this.preRetryProcessor != null && this.cause != null) {
                this.preRetryProcessor.execute();
            }
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
     * 获取重试业务处理器
     * @return Processor
     */
    public Object getRealProcessor() {
        if (isReturn) {
            return rProcessor;
        }
        return vProcessor;
    }


    public RProcessor<T> getRProcessor() {
        return rProcessor;
    }

    public VProcessor getVProcessor() {
        return vProcessor;
    }
    public void setPreRetryProcessor(VProcessor preRetryProcessor) {
        this.preRetryProcessor = preRetryProcessor;
    }

    public String getName() {
        return name == null ? "" : name + "-";
    }

    public void setName(String name) {
        this.name = name;
    }
}
