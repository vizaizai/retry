package com.github.vizaizai.remote.codec;

import com.github.vizaizai.logging.LoggerFactory;
import com.github.vizaizai.remote.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;

/**
 * RPC Encoder
 * @author liaochongwei
 * @date 2022/2/18 11:44
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RpcEncoder.class);
    private final Class<?> clazz;
    private final Serializer serializer;

    public RpcEncoder(Class<?> clazz, Serializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (clazz.isInstance(in)) {
            try {
                byte[] data = serializer.serialize(in);
                out.writeInt(data.length);
                out.writeBytes(data);
            } catch (Exception ex) {
                logger.error("Encode error: ", ex);
            }
        }
    }
}
