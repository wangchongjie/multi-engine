package com.baidu.unbiz.multiengine.codec.common;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import com.baidu.unbiz.multiengine.codec.MsgCodec;
import com.baidu.unbiz.multiengine.exception.CodecException;

/**
 * JSON格式的消息编解码
 */
public class JsonCodec implements MsgCodec {
    /**
     * 对象匹配映射
     */
    private final ObjectMapper mapper;

    public JsonCodec() {
        mapper = new ObjectMapper();
        // ignoring unknown properties makes us more robust to changes in the schema
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // This will allow including type information all non-final types. This allows correct
        // serialization/deserialization of generic collections, for example List<MyType>.
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    }

    @Override
    public byte[] encode(Object object) throws CodecException {
        try {
            return mapper.writeValueAsBytes(object);
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

    @Override
    public <T> T decode(Class<T> clazz, byte[] bytes) throws CodecException {
        try {
            return mapper.readValue(bytes, clazz);
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

}
