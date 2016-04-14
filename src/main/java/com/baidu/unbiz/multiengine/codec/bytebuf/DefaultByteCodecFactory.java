package com.baidu.unbiz.multiengine.codec.bytebuf;

import com.baidu.unbiz.multiengine.codec.HeadCodec;
import com.baidu.unbiz.multiengine.codec.MsgCodec;

import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 默认的二进制编解码器工厂
 */
public class DefaultByteCodecFactory implements ByteCodecFactory {


    private MsgCodec msgCodec;
    private HeadCodec headCodec;

    @Override
    public MessageToByteEncoder<Object> getEncoder() {
        ByteEncoder encoder = new ByteEncoder();
        encoder.setMessageCodec(msgCodec);
        encoder.setHeadCodec(headCodec);
        return encoder;
    }

    @Override
    public ByteToMessageDecoder getDecoder() {
        ByteDecoder decoder = new ByteDecoder();
        decoder.setMessageCodec(msgCodec);
        decoder.setHeadCodec(headCodec);
        return decoder;
    }

    public void setMsgCodec(MsgCodec msgCodec) {
        this.msgCodec = msgCodec;
    }
    public void setHeadCodec(HeadCodec headCodec) {
        this.headCodec = headCodec;
    }


}
