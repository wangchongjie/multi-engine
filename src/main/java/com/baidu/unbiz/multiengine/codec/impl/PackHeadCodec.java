package com.baidu.unbiz.multiengine.codec.impl;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import com.baidu.unbiz.devlib.cache.AtomicComputeCache;
import com.baidu.unbiz.multiengine.codec.MsgCodec;
import com.baidu.unbiz.multiengine.exception.CodecException;
import com.baidu.unbiz.multiengine.transport.protocol.PackHead;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: ProtobufCodec <br/>
 * Function: protobuf序列化器，利用反射缓存<tt>method</tt>来进行调用
 *
 */
public class PackHeadCodec implements MsgCodec {

    @Override
    public <T> T decode(final Class<T> clazz, byte[] data) throws CodecException {
        return (T) PackHead.fromBytes(data);
    }

    @Override
    public <T> byte[] encode(T object) throws CodecException {
        if (object instanceof PackHead) {
            return ((PackHead) object).toBytes();
        }
        throw new CodecException("not support non-packhead");
    }

}
