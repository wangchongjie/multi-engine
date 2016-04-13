package com.baidu.unbiz.multiengine.codec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multiengine.exception.CodecException;
import com.baidu.unbiz.multiengine.utils.BufferUtils;

import io.netty.buffer.ByteBuf;

/**
 * 消息编解码器接口的适配器
 */
public class MsgCodecAdaptor implements ByteBufCodec {

    private static final Log LOG = LogFactory.getLog(MsgCodecAdaptor.class);

    /**
     * 消息编解码器
     */
    private MsgCodec messageCodec;

    public MsgCodecAdaptor(MsgCodec messageCodec) {
        this.messageCodec = messageCodec;
    }

    @Override
    public byte[] encode(Object object) throws CodecException {
        try {
            return messageCodec.encode(object);
        } catch (Exception e) {
            LOG.error("encode error", e);
            throw new CodecException(e);
        }
    }

    @Override
    public <T> T decode(ByteBuf byteBuf, int length, Class<T> clazz) throws CodecException {
        try {
            return messageCodec.decode(clazz, BufferUtils.bufToBytes(byteBuf, length));
        } catch (Exception e) {
            LOG.error("decode error,clazz=" + clazz, e);
            throw new CodecException(e);
        }

    }
}
