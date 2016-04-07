package com.baidu.unbiz.multiengine.codec.impl;

import com.baidu.unbiz.devlib.reflection.ReflectionUtil;
import com.baidu.unbiz.multiengine.codec.Codec;
import com.baidu.unbiz.multiengine.exception.CodecException;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * ClassName: ProtobufCodec <br/>
 * Function: protobuf序列化器，利用反射缓存<tt>method</tt>来进行调用
 */
public class ProtostuffCodec implements Codec {

    /**
     * 设置编码规则
     */
    static {
        System.setProperty("protostuff.runtime.collection_schema_on_repeated_fields", "true");
        System.setProperty("protostuff.runtime.morph_collection_interfaces", "true");
        System.setProperty("protostuff.runtime.morph_map_interfaces", "true");
    }

    /**
     * 缓冲区
     */
    private ThreadLocal<LinkedBuffer> linkedBuffer = new ThreadLocal<LinkedBuffer>() {
        @Override
        protected LinkedBuffer initialValue() {
            return LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        }
    };

    @Override
    public <T> T decode(final Class<T> clazz, byte[] data) throws CodecException {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);

        T content = ReflectionUtil.newInstance(clazz);
        ProtobufIOUtil.mergeFrom(data, content, schema);
        return content;
    }

    @Override
    public <T> byte[] encode(final Class<T> clazz, T object) throws CodecException {
        try {
            @SuppressWarnings("unchecked")
            com.dyuproject.protostuff.Schema<T> schema =
                    (com.dyuproject.protostuff.Schema<T>) RuntimeSchema.getSchema(object.getClass());
            byte[] protostuff = ProtobufIOUtil.toByteArray(object, schema, linkedBuffer.get());
            return protostuff;
        } finally {
            linkedBuffer.get().clear();
        }
    }

}
