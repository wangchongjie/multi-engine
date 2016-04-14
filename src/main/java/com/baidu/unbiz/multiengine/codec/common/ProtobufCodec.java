package com.baidu.unbiz.multiengine.codec.common;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import com.baidu.unbiz.devlib.cache.AtomicComputeCache;
import com.baidu.unbiz.multiengine.codec.MsgCodec;
import com.baidu.unbiz.multiengine.exception.CodecException;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: ProtobufCodec <br/>
 * Function: protobuf序列化器，利用反射缓存<tt>method</tt>来进行调用
 *
 */
public class ProtobufCodec implements MsgCodec {

    /**
     * Protobuf生成原生Java代码中的方法解码方法名称
     */
    private static final String METHOD_NAME_PARSEFROM = "parseFrom";

    /**
     * Protobuf生成原生Java代码中的方法编码方法名称
     */
    private static final String METHOD_NAME_TOBYTE = "toByteArray";

    /**
     * 方法缓存，用于Protobuf生成原生Java代码中的某些编解码方法。 缓存的方法包括:
     */
    private static final AtomicComputeCache<String, Method> PROTOBUF_METHOD_CACHE = new AtomicComputeCache<String, Method>();


    @Override
    public <T> T decode(final Class<T> clazz, byte[] data) throws CodecException {
        try {
            if (data == null || data.length == 0) {
                return null;
            }
            Method m = PROTOBUF_METHOD_CACHE.getComputeResult(clazz.getName() + METHOD_NAME_PARSEFROM,
                    new Callable<Method>() {
                        @Override
                        public Method call() throws Exception {
                            return clazz.getMethod(METHOD_NAME_PARSEFROM, byte[].class);
                        }
                    });
            GeneratedMessage msg = (GeneratedMessage) m.invoke(clazz, data);
            return (T) msg;
        } catch (Exception e) {
            throw new CodecException("Decode failed due to " + e.getMessage(), e);
        }
    }


    @Override
    public <T> byte[] encode(T object) throws CodecException {
        try {
            final Class<?> clazz = object.getClass();
            Method m = PROTOBUF_METHOD_CACHE.getComputeResult(clazz.getName() + METHOD_NAME_TOBYTE,
                    new Callable<Method>() {
                        @Override
                        public Method call() throws Exception {
                            return clazz.getMethod(METHOD_NAME_TOBYTE);
                        }
                    });
            byte[] data = (byte[]) m.invoke(object);
            return data;
        } catch (Exception e) {
            throw new CodecException("Encode failed due to " + e.getMessage(), e);
        }
    }

}
