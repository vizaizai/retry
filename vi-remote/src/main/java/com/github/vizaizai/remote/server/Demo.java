package com.github.vizaizai.remote.server;

import com.github.vizaizai.remote.server.netty.NettyServer;

/**
 * @author liaochongwei
 * @date 2022/2/18 16:17
 */
public class Demo {
    public static void main(String[] args) {

        Server server = new NettyServer("127.0.0.1",8080);
        server.start();

    }
}
