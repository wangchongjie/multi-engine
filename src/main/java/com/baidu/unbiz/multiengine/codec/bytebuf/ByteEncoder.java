package com.baidu.unbiz.multiengine.codec.bytebuf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.unbiz.multiengine.codec.ByteBufCodec;
import com.baidu.unbiz.multiengine.codec.HeadCodec;
import com.baidu.unbiz.multiengine.codec.MsgCodec;
import com.baidu.unbiz.multiengine.codec.common.MsgHeadCodec;
import com.baidu.unbiz.multiengine.codec.common.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.Signal;
import com.baidu.unbiz.multiengine.exception.CodecException;
import com.baidu.unbiz.multiengine.transport.protocol.MsgHead;
import com.baidu.unbiz.multiengine.transport.protocol.NSHead;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 二进制编码器
 */
public class ByteEncoder extends MessageToByteEncoder<Object> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Class<?> headerClass;

    /**
     * ByteBuf的编解码器
     */
    protected ByteBufCodec byteBufCodec = new MsgCodecAdaptor(new ProtostuffCodec());
    protected ByteBufCodec headBufCodec = new MsgCodecAdaptor(new MsgHeadCodec());

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

        byte[] headBytes = null;
        if (this.headerClass.equals(MsgHead.class)) {
            MsgHead header = MsgHead.create();
            header.setBodyLen(bodyBytes.length);
            headBytes = headBufCodec.encode(header);
        }

        if (this.headerClass.equals(NSHead.class)) {
            NSHead header = NSHead.factory("multi-engine");
            header.setBodyLen(bodyBytes.length);
            headBytes = headBufCodec.encode(header);
        }

        out.writeBytes(headBytes).writeBytes(bodyBytes);
    }

    public void setMessageCodec(MsgCodec msgCodec) {
        this.byteBufCodec = new MsgCodecAdaptor(msgCodec);
    }

    public void setHeadCodec(HeadCodec headBufCodec) {
        this.headBufCodec = new MsgCodecAdaptor(headBufCodec);
        this.headerClass = headBufCodec.getHeadClass();
    }
}
