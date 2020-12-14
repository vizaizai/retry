package com.github.vizaizai.retry.core;

import java.io.Serializable;

/**
 * 异步回调结果
 * @author liaochongwei
 * @date 2020/12/10 17:52
 */
public class CallBackResult implements Serializable {
    /**
     * 重试状态
     */
    private final RetryStatus status;
    /**
     * 异常消息
     */
    private String errMsg;

    public CallBackResult(RetryStatus status) {
        this.status = status;
    }

    public CallBackResult(RetryStatus status, String errMsg) {
        this.status = status;
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "CallBackResult{" +
                "status=" + status.name() +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }

    public RetryStatus getStatus() {
        return status;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
