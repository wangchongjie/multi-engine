package com.baidu.unbiz.multiengine.codec;

import com.baidu.unbiz.multiengine.exception.CodecException;

/**
 * Created by wangchongjie on 16/4/5.
 */
public interface Codec {
    /**
     * 反序列化
     *
     * @param clazz
     *            反序列化后的类定义
     * @param bytes
     *            字节码
     * @return 反序列化后的对象
     * @throws CodecException
     */
     <T> T decode(Class<T> clazz, byte[] bytes) throws CodecException;

    /**
     * 序列化
     *
     * @param clazz
     *            待序列化的类定义
     * @param object
     *            待序列化的对象
     * @return 字节码
     * @throws CodecException
     */
    <T> byte[] encode(Class<T> clazz, T object) throws CodecException;
}
