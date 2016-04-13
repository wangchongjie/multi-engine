package com.baidu.unbiz.multiengine.codec;

import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 默认的二进制编解码器工厂
 */
public class DefaultByteCodecFactory implements ByteCodecFactory {


    private MsgCodec msgCodec;

    @Override
    public MessageToByteEncoder<Object> getEncoder() {
        ByteEncoder encoder = new ByteEncoder();
        encoder.setMessageCodec(msgCodec);
        return encoder;
    }

    @Override
    public ByteToMessageDecoder getDecoder() {
        ByteDecoder decoder = new ByteDecoder();
        decoder.setMessageCodec(msgCodec);
        return decoder;
    }

    public void setMsgCodec(MsgCodec msgCodec) {
        this.msgCodec = msgCodec;
    }

}
