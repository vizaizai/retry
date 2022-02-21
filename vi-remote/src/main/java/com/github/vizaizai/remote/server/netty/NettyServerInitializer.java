package com.github.vizaizai.remote.server.netty;

import com.github.vizaizai.remote.codec.RpcDecoder;
import com.github.vizaizai.remote.codec.RpcEncoder;
import com.github.vizaizai.remote.codec.RpcRequest;
import com.github.vizaizai.remote.codec.RpcResponse;
import com.github.vizaizai.remote.serializer.Serializer;
import com.github.vizaizai.remote.serializer.kryo.KryoSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;


/**
 * Netty服务端初始化
 * @author liaochongwei
 * @date 2022/2/18 15:55
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        Serializer serializer = new KryoSerializer();
        ChannelPipeline cp = sc.pipeline();
        // 心跳检测
        cp.addLast("beat",new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
        // 处理粘包/拆包
        cp.addLast("lengthFieldBasedFrameDecoder",new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        cp.addLast("rpcDecoder",new RpcDecoder(RpcRequest.class, serializer));
        cp.addLast("rpcEncoder",new RpcEncoder(RpcResponse.class, serializer));
        cp.addLast("nettyServerHandler",new NettyServerHandler());
    }
}
