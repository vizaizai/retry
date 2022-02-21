package com.github.vizaizai.remote.server.netty;

import com.github.vizaizai.logging.LoggerFactory;
import com.github.vizaizai.remote.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author liaochongwei
 * @date 2022/2/18 15:33
 */
public class NettyServer implements Server {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private static ExecutorService executor = Executors.newFixedThreadPool(1);
    /**
     * 地址
     */
    private String host;
    /**
     * 绑定端口
     */
    private int port;

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // 一个线程来接收连接
                EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                // 处理器个数*2
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                            .childHandler(new NettyServerInitializer())
                            .option(ChannelOption.SO_BACKLOG, 128)
                            .childOption(ChannelOption.SO_KEEPALIVE, true);

                    ChannelFuture future = bootstrap.bind(host, port).sync();
                    logger.info("Server started on port {}", port);
                    future.channel().closeFuture().sync();
                }catch (Exception e) {
                    logger.error("Server startup failure: ", e);
                }finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }


        });
    }
    @Override
    public void stop() {
        try {
            // 关闭任务线程池
            executor.shutdown();
            // 等待关闭
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }catch (Exception e) {
            logger.error("shutdownfailure.",e);
        }
    }
}
