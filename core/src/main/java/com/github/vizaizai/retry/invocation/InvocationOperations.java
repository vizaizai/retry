package com.github.vizaizai.retry.invocation;

import com.github.vizaizai.retry.exception.RetryException;
import com.github.vizaizai.retry.util.Assert;
import org.slf4j.Logger;
import com.github.vizaizai.logging.LoggerFactory;

/**
 * 方法调用处理
 * @author liaochongwei
 * @date 2020/12/8 14:49
 */
public class InvocationOperations<T> {
    private static final Logger log = LoggerFactory.getLogger(InvocationOperations.class);
    /**
     * 带有返回值的处理
     */
    private Processor<T> processor;
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

    public static <T> InvocationOperations<T> of(Processor<T> processor) {
        Assert.notNull(processor, "The processor must be not null");
        InvocationOperations<T> operations = new InvocationOperations<>();
        operations.processor = processor;
        operations.isReturn = true;
        return operations;
    }

    public static InvocationOperations<Void> of(VProcessor processor) {
        Assert.notNull(processor, "The processor must be not null");
        InvocationOperations<Void> operations = new InvocationOperations<>();
        operations.vProcessor = processor;
        operations.isReturn = false;
        return operations;
    }

    public T execute(){
        T result = null;
        try {
            if (this.isReturn) {
                result =  this.processor.execute();
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
    public boolean haveErr() {
        return cause != null;
    }
    public Throwable getCause() {
        return cause;
    }
    public String getErrMsg() {
        return cause == null ? "" : cause.getMessage();
    }
}
