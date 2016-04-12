package com.baidu.unbiz.multiengine.transport.protocol;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * bigpipe nshead 描述
 *
 * @author wangchongjie
 */
public final class PackHead {

    /**
     * session内顺序Id
     */
    private long seqId = 0;

    /**
     *  int (4) head后数据的总长度
     */
    private int bodyLen;

    /**
     *  int (4) head后数据的总长度
     */
    private int remainLen;


    public static final int SIZE = 16;
    private static String DEF_ENCODING = "GBK";


    private PackHead() {

    }

    private PackHead(long seqId, int bodyLen, int remainLen) {
        this.seqId = seqId;
        this.bodyLen = bodyLen;
        this.remainLen = remainLen;
    }

    public static PackHead create() {
        return new PackHead();
    }

    public static PackHead create(long seqId) {
        PackHead head = new PackHead();
        head.setSeqId(seqId);
        return head;
    }

    /**
     * write pack_head to transport
     */
    public byte[] toBytes() {
        ByteBuffer bb = ByteBuffer.allocate(SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        try {
            bb.putLong(seqId);
            bb.putInt(bodyLen);
            bb.putInt(remainLen);
        } catch (Exception e) {
            throw new RuntimeException("exception when putting bytes for nshead...", e);
        }
        return bb.array();
    }

    /**
     * 由byte数组解析成PackHead对象
     */
    public static PackHead fromBytes(byte[] headBytes) {
        PackHead head = new PackHead();
        if (headBytes.length < PackHead.SIZE) {
            throw new RuntimeException("NSHead's size should equal 16.");
        }
        ByteBuffer buffer = ByteBuffer.wrap(headBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        head.seqId = buffer.getLong();
        head.bodyLen = buffer.getInt();
        head.remainLen = buffer.getInt();
        return head;
    }

    public int getRemainLen() {
        return remainLen;
    }

    public void setRemainLen(int remainLen) {
        this.remainLen = remainLen;
    }

    public int getBodyLen() {
        return bodyLen;
    }

    public void setBodyLen(int bodyLen) {
        this.bodyLen = bodyLen;
    }

    public long getSeqId() {
        return seqId;
    }

    public void setSeqId(long seqId) {
        this.seqId = seqId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
