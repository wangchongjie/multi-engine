package com.baidu.unbiz.multiengine.utils;

import io.netty.buffer.ByteBuf;

/**
 * Netty ByteBuf的工具类
 */
public abstract class BufferUtils {

    /**
     * 将ByteBuf转化成byte数组
     * 
     * @param byteBuf Netty的ByteBuf @see ByteBuf
     * @return byte数组
     */
    public static byte[] bufToBytes(ByteBuf byteBuf) {
        if (byteBuf == null) {
            return null;
        }

        int length = byteBuf.readableBytes();
        byte[] array = new byte[length];
        byteBuf.readBytes(array);

        return array;
    }

    /**
     * 将ByteBuf转化成byte数组
     *
     * @param byteBuf Netty的ByteBuf @see ByteBuf
     * @param length 长度
     * @return byte数组
     */
    public static byte[] bufToBytes(ByteBuf byteBuf, int length) {
        if (byteBuf == null) {
            return null;
        }

        byte[] array = new byte[length];
        byteBuf.readBytes(array);

        return array;
    }

}
