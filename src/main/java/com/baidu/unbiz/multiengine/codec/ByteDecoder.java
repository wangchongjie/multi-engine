package com.baidu.unbiz.multiengine.codec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.unbiz.multiengine.codec.impl.PackHeadCodec;
import com.baidu.unbiz.multiengine.codec.impl.ProtobufCodec;
import com.baidu.unbiz.multiengine.codec.impl.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.RpcResult;
import com.baidu.unbiz.multiengine.dto.Signal;
import com.baidu.unbiz.multiengine.exception.CodecException;
import com.baidu.unbiz.multiengine.transport.protocol.PackHead;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

/**
 * 二进制解码器
 */
public class ByteDecoder extends ReplayingDecoder<Object> {

    /**
     * 日志
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Class<?> headerClass;

    /**
     * ByteBuf的编解码器
     */
    protected ByteBufCodec byteBufCodec = new MsgCodecAdaptor(new ProtostuffCodec());
    protected ByteBufCodec headBufCodec = new MsgCodecAdaptor(new PackHeadCodec());

    public ByteDecoder() {
        headerClass = PackHead.class;
        // this.setSingleDecode(true);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws CodecException {
        PackHead header = headBufCodec.decode(in, PackHead.SIZE, PackHead.class);

        int bodySize = header.getSumLen();
        Signal<?> signal = byteBufCodec.decode(in, bodySize, Signal.class);

        if (signal != null) {
            out.add(signal);
        }
    }

    public void setMessageCodec(MsgCodec messageCodec) {
        this.byteBufCodec = new MsgCodecAdaptor(messageCodec);
    }

}
