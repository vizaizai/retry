package com.github.vizaizai.remote.codec;

import java.io.Serializable;

/**
 * RPC Response
 * @author liaochongwei
 * @date 2022/2/18 11:40
 */
public class RpcResponse implements Serializable {
    /**
     * 请求id
     */
    private String requestId;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 错误消息
     */
    private String msg;
    /**
     * 返回结果
     */
    private Object result;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
