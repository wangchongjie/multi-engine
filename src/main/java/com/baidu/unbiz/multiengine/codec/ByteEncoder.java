package com.baidu.unbiz.multiengine.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.unbiz.multiengine.codec.impl.PackHeadCodec;
import com.baidu.unbiz.multiengine.codec.impl.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.Signal;
import com.baidu.unbiz.multiengine.exception.CodecException;
import com.baidu.unbiz.multiengine.transport.protocol.PackHead;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 二进制编码器
 */
public class ByteEncoder extends MessageToByteEncoder<Object> {

    /**
     * 日志
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 是否开启debug
     */
    protected boolean isDebugEnabled;

    /**
     * ByteBuf的编解码器
     */
    protected ByteBufCodec byteBufCodec = new MsgCodecAdaptor(new ProtostuffCodec());

    protected ByteBufCodec headBufCodec = new MsgCodecAdaptor(new PackHeadCodec());

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws CodecException {
        if (msg instanceof Signal) {
            encodeSignal(ctx, (Signal<?>) msg, out);
            return;
        }
        // other languages
        if (byte[].class.isAssignableFrom(msg.getClass())) {
            out.writeBytes((byte[]) msg);
            return;
        }

        throw new CodecException("msg not being encode." + msg);
    }

    private void encodeSignal(ChannelHandlerContext ctx, Signal<?> msg, ByteBuf out) throws CodecException {
        byte[] bodyBytes = byteBufCodec.encode(msg);

        PackHead header = PackHead.create();
        header.setSumLen(bodyBytes.length);
        header.setBodyLen(bodyBytes.length);
        byte[] headBytes = headBufCodec.encode(header);

        out.writeBytes(headBytes).writeBytes(bodyBytes);
    }


    public void setMessageCodec(MsgCodec msgCodec) {
        this.byteBufCodec = new MsgCodecAdaptor(msgCodec);
    }
}
