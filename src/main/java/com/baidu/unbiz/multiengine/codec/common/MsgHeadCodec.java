package com.baidu.unbiz.multiengine.codec.common;

import com.baidu.unbiz.multiengine.codec.HeadCodec;
import com.baidu.unbiz.multiengine.exception.CodecException;
import com.baidu.unbiz.multiengine.transport.protocol.MsgHead;

/**
 * ClassName: ProtobufCodec <br/>
 * Function: protobuf序列化器，利用反射缓存<tt>method</tt>来进行调用
 */
public class MsgHeadCodec implements HeadCodec {

    @Override
    public <T> T decode(final Class<T> clazz, byte[] data) throws CodecException {
        return (T) MsgHead.fromBytes(data);
    }

    @Override
    public <T> byte[] encode(T object) throws CodecException {
        if (object instanceof MsgHead) {
            return ((MsgHead) object).toBytes();
        }
        throw new CodecException("not support non-msghead");
    }

    @Override
    public Class getHeadClass() {
        return MsgHead.class;
    }

}
