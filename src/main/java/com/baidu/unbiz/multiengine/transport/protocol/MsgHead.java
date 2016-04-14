package com.baidu.unbiz.multiengine.transport.protocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * bigpipe nshead 描述
 *
 * @author wangchongjie
 */
public final class MsgHead {

    /**
     * session内顺序Id
     */
    private long seqId;

    /**
     *  int (4) 当前包的数据长度
     */
    private int bodyLen;

    public static final int SIZE = 12;

    private static String DEF_ENCODING = "GBK";


    private MsgHead() {

    }

    private MsgHead(long seqId, int bodyLen) {
        this.seqId = seqId;
        this.bodyLen = bodyLen;
    }

    public static MsgHead create() {
        return new MsgHead();
    }

    public static MsgHead create(long seqId) {
        MsgHead head = new MsgHead();
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
        } catch (Exception e) {
            throw new RuntimeException("exception when putting bytes for nshead...", e);
        }
        return bb.array();
    }

    /**
     * 由byte数组解析成PackHead对象
     */
    public static MsgHead fromBytes(byte[] headBytes) {
        MsgHead head = new MsgHead();
        if (headBytes.length < MsgHead.SIZE) {
            throw new RuntimeException("NSHead's size should equal 16.");
        }
        ByteBuffer buffer = ByteBuffer.wrap(headBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        head.seqId = buffer.getLong();
        head.bodyLen = buffer.getInt();
        return head;
    }

    public long getSeqId() {
        return seqId;
    }

    public void setSeqId(long seqId) {
        this.seqId = seqId;
    }

    public int getBodyLen() {
        return bodyLen;
    }

    public void setBodyLen(int bodyLen) {
        this.bodyLen = bodyLen;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
