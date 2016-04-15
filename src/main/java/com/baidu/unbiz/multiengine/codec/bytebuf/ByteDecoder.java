package com.baidu.unbiz.multiengine.codec.bytebuf;

import java.util.List;

import org.slf4j.Logger;

import com.baidu.unbiz.multiengine.codec.ByteBufCodec;
import com.baidu.unbiz.multiengine.codec.HeadCodec;
import com.baidu.unbiz.multiengine.codec.MsgCodec;
import com.baidu.unbiz.multiengine.codec.common.MsgHeadCodec;
import com.baidu.unbiz.multiengine.codec.common.ProtostuffCodec;
import com.baidu.unbiz.multiengine.exception.CodecException;
import com.baidu.unbiz.multiengine.transport.dto.Signal;
import com.baidu.unbiz.multiengine.transport.protocol.MsgHead;
import com.baidu.unbiz.multiengine.transport.protocol.NSHead;
import com.baidu.unbiz.multitask.log.AopLogFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

/**
 * 二进制解码器
 */
public class ByteDecoder extends ReplayingDecoder<Object> {

    protected final Logger LOG = AopLogFactory.getLogger(this.getClass());

    private Class<?> headerClass;

    /**
     * ByteBuf的编解码器
     */
    protected ByteBufCodec byteBufCodec = new MsgCodecAdaptor(new ProtostuffCodec());
    protected ByteBufCodec headBufCodec = new MsgCodecAdaptor(new MsgHeadCodec());

    public ByteDecoder() {
        headerClass = MsgHead.class;
        // this.setSingleDecode(true);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws CodecException {
        int bodySize = 0;

        if (this.headerClass.equals(MsgHead.class)) {
            MsgHead header = headBufCodec.decode(in, MsgHead.SIZE, MsgHead.class);
            bodySize = header.getBodyLen();
        }
        if (this.headerClass.equals(NSHead.class)) {
            NSHead header = headBufCodec.decode(in, NSHead.SIZE, NSHead.class);
            bodySize = (int) header.getBodyLen();
        }

        Signal<?> signal = byteBufCodec.decode(in, bodySize, Signal.class);
        if (signal != null) {
            out.add(signal);
        }
    }

    public void setMessageCodec(MsgCodec messageCodec) {
        this.byteBufCodec = new MsgCodecAdaptor(messageCodec);
    }

    public void setHeadCodec(HeadCodec headBufCodec) {
        this.headerClass = headBufCodec.getHeadClass();
        this.headBufCodec = new MsgCodecAdaptor(headBufCodec);
    }
}
