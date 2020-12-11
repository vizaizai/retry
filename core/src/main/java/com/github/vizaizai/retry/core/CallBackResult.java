package com.github.vizaizai.retry.core;

import java.io.Serializable;

/**
 * 异步回调结果
 * @author liaochongwei
 * @date 2020/12/10 17:52
 */
public class CallBackResult implements Serializable {
    /**
     * 是否成功
     */
    private final boolean ok;
    /**
     * 异常消息
     */
    private String errMsg;

    public CallBackResult(boolean ok) {
        this.ok = ok;
    }

    public CallBackResult(boolean ok, String errMsg) {
        this.ok = ok;
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "CallBackResult{" +
                "ok=" + ok +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }
}
