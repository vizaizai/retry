package com.github.vizaizai.retry.exception;

/**
 * @author liaochongwei
 * @date 2020/7/30 15:05
 */
public class PreRetryHandleException extends RuntimeException{
    public PreRetryHandleException(String message) {
        super(message);
    }
    public PreRetryHandleException(String message, Throwable cause) {
        super(message, cause);
    }
    public PreRetryHandleException(Throwable cause) {
        super(cause);
    }
}
