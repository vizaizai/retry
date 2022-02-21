package com.github.vizaizai.remote.codec;

import com.github.vizaizai.logging.LoggerFactory;
import com.github.vizaizai.remote.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;

import java.util.List;

/**
 * Rpc消息解码器
 * @author liaochongwei
 * @date 2022/2/18 11:44
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private final Logger logger = LoggerFactory.getLogger(RpcDecoder.class);

    private final Class<?> clazz;
    /**
     * 序列化器
     */
    private final Serializer serializer;

    public RpcDecoder(Class<?> clazz,Serializer serializer) {
        this.serializer = serializer;
        this.clazz = clazz;
    }

    // 数据长度|数据
    // 4bytes|.....
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        // 可读取的长度小于消息头（标识数据长度）的长度
        if (in.readableBytes() < 4) {
            return;
        }
        // 标记读下标（用于重置）
        in.markReaderIndex();
        // 获取数据长度
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            // 恢复读位置
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj;
        try {
            obj = serializer.deserialize(data, clazz);
            out.add(obj);
        } catch (Exception ex) {
            logger.error("Decode error: ", ex);
        }
    }
}
