package com.github.vizaizai.retry.exception;

/**
 * @author liaochongwei
 * @date 2020/7/30 15:05
 */
public class StoreException extends RuntimeException{
    public StoreException(String message) {
        super(message);
    }
    public StoreException(String message, Throwable cause) {
        super(message, cause);
    }
    public StoreException(Throwable cause) {
        super(cause);
    }
}
