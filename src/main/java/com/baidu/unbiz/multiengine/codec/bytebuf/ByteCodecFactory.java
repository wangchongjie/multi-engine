/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.unbiz.multiengine.codec.bytebuf;

import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 二进制编解码器工厂
 */
public interface ByteCodecFactory {

    /**
     * 获取编码器 @see MessageToByteEncoder
     * 
     * @return 编码器
     */
    MessageToByteEncoder<Object> getEncoder();

    /**
     * 获取解码器 @see ByteToMessageDecoder
     * 
     * @return 解码器
     */
    ByteToMessageDecoder getDecoder();

}
