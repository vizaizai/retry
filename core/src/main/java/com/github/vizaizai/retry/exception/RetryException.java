package com.github.vizaizai.retry.exception;

/**
 * @author liaochongwei
 * @date 2020/7/30 15:05
 */
public class RetryException extends RuntimeException{
    public RetryException(String message) {
        super(message);
    }
    public RetryException(String message, Throwable cause) {
        super(message, cause);
    }
    public RetryException(Throwable cause) {
        super(cause);
    }
}
