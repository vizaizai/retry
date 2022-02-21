package com.github.vizaizai.remote.codec;

import java.io.Serializable;

/**
 * RPC Request
 * @author liaochongwei
 * @date 2022/2/18 11:35
 */
public class RpcRequest implements Serializable {
    /**
     * 请求id
     */
    private String requestId;
    /**
     * 业务码
     */
    private String bizCode;
    /**
     * 请求参数
     */
    private Object param;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }
}
