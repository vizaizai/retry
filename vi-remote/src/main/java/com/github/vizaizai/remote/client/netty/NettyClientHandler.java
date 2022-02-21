package com.github.vizaizai.remote.client.netty;

import com.github.vizaizai.logging.LoggerFactory;
import com.github.vizaizai.remote.codec.RpcResponse;
import com.github.vizaizai.remote.server.netty.NettyServerHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/*
 * netty客户端处理器
 * @author liaochongwei
 * @date 2022/2/21 10:22
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> implements Callable<RpcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
    private CountDownLatch latch = new CountDownLatch(1);


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("client cause exception: {}", cause.getMessage());
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse objectRpcResponse) throws Exception {

    }


    @Override
    public RpcResponse call() throws Exception {

        latch.await();


        return null;
    }
}
