/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.unbiz.multiengine.codec;

import com.baidu.unbiz.multiengine.exception.CodecException;

import io.netty.buffer.ByteBuf;

/**
 * ByteBuf的编解码接口
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2015-1-4 下午3:59:29
 */
public interface ByteBufCodec {

    /**
     * 将对象编码成ByteBuf
     * 
     * @param object 编码对象
     * @return byte数组
     * @throws CodecException
     */
    byte[] encode(Object object) throws CodecException;

    /**
     * 将ByteBuf解码成对象
     * 
     * @param byteBuf 字节缓冲区 @see ByteBuf
     * @param length 字节长度
     * @param clazz 对象类型
     * @return 解码对象
     * @throws CodecException
     */
    <T> T decode(ByteBuf byteBuf, int length, Class<T> clazz) throws CodecException;

}
