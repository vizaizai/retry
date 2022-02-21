package com.github.vizaizai.remote.common;

/**
 * 服务节点
 * @author liaochongwei
 * @date 2022/2/21 11:06
 */
public class ServiceNode {
    /**
     * 主机地址
     */
    private String host;
    /**
     * 端口号
     */
    private int port;
    /**
     * 服务名称
     */
    private String serviceName;

    public ServiceNode(String host, int port, String serviceName) {
        this.host = host;
        this.port = port;
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
