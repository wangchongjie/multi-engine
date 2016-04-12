package com.baidu.unbiz.multiengine.transport.protocol;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * bigpipe nshead 描述
 *
 * @author wangchongjie
 */
public final class NSHead {
    public static final int SIZE = 36;

    private static final int VERSION = 2;
    private static final int PROVIDER_LEN = 16;
    private static final long MAGIC_NUM = 0x012FAE3A;

    private static String DEF_ENCODING = "GBK";

    private int id = 0;
    private int version = 0;

    /**
     * unsigned int (4) 由 apache 产生的 logid，贯穿一次请求的所有网络交互。 要求前端产生的 logid 在短时间内 (例如 10 秒内) 在所有前端服务器范围内都不会重复出现 目的是用时间和
     * log_id 能够确定唯一一次 kr 会话
     */
    private long logId = 0;

    /**
     * char (16) 请求包为客户端标识，命名方式：产品名-模块名，比如 "sf-web\0",
     * "im-ext\0",”fc-web\0”，凤巢客户端一定要填上”fc-web\0”，否则得到的res中的竞价客户数是shifen的竞价客户数
     */
    private String provider;

    /**
     * unsigned int (4) 特殊标识：常数 0xfb709394，标识一个包的起始
     */
    private long magicNum = MAGIC_NUM;

    private long reserved;

    /**
     * unsigned int (4) head后数据的总长度
     */
    private long bodyLen;

    private NSHead() {

    }

    private NSHead(String provider) {
        this(0, VERSION, (long) 0, provider, MAGIC_NUM, 0x00, 0L);
    }

    private NSHead(int id, int version, long logId, String provider, long magicNum, int reserved, long bodyLen) {
        this.id = id;
        this.version = version;
        this.logId = logId;
        this.provider = provider;
        this.magicNum = magicNum;
        this.reserved = reserved;
        this.bodyLen = bodyLen;
    }

    public static NSHead factory(String provider) {
        return new NSHead(provider);
    }

    /**
     * write ns_head to transport
     */
    public byte[] toBytes() {
        ByteBuffer bb = ByteBuffer.allocate(SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        try {
            bb.putShort((short) id);
            bb.putShort((short) version);
            bb.putInt((int) logId);
            byte[] prvd = provider.getBytes(DEF_ENCODING);
            byte[] pb = new byte[PROVIDER_LEN];
            System.arraycopy(prvd, 0, pb, 0, prvd.length);
            bb.put(pb);
            bb.putInt((int) magicNum);
            bb.putInt((int) reserved);
            bb.putInt((int) bodyLen);
        } catch (Exception e) {
            throw new RuntimeException("exception when putting bytes for nshead...", e);
        }

        return bb.array();
    }

    /**
     * 由byte数组解析成NsHead对象
     *
     * @param headBytes 36-bytes
     *
     * @return 2012-9-21
     */
    public static NSHead fromBytes(byte[] headBytes) {
        NSHead head = new NSHead();
        if (headBytes.length < NSHead.SIZE) {
            throw new RuntimeException("NSHead's size should equal 16.");
        }
        ByteBuffer buffer = ByteBuffer.wrap(headBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        head.id = buffer.getShort();
        head.version = buffer.getShort();
        head.logId = buffer.getInt();

        byte[] hb = new byte[PROVIDER_LEN];
        buffer.get(hb, 0, PROVIDER_LEN);
        try {
            head.setProvider(new String(hb, DEF_ENCODING));
        } catch (UnsupportedEncodingException e) {
            // ignore
            throw new RuntimeException(e);
        }
        head.magicNum = buffer.getInt();
        head.reserved = buffer.getInt();
        head.bodyLen = buffer.getInt();

        return head;
    }

    @Override
    public String toString() {
        return "[ version: " + version + ", id:" + id + " logId:" + logId + " bodyLen:" + bodyLen + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String getProvider(String charset) {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public long getMagicNum() {
        return magicNum;
    }

    public void setMagicNum(long magicNum) {
        this.magicNum = magicNum;
    }

    public long getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public long getBodyLen() {
        return bodyLen;
    }

    public void setBodyLen(int bodyLen) {
        this.bodyLen = bodyLen;
    }
}
