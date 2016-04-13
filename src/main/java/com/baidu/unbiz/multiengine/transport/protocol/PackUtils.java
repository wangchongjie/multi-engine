package com.baidu.unbiz.multiengine.transport.protocol;

import java.util.ArrayList;
import java.util.List;

import com.baidu.unbiz.multiengine.exception.MultiEngineException;

/**
 * Created by wangchongjie on 16/4/13.
 */
public class PackUtils {

    public static List<byte[]> buildPackData(long seqId, byte[] buf, int capacity) {
        if (capacity < PackHead.SIZE) {
            throw new MultiEngineException("packSize is too small");
        }
        List<byte[]> dataList = new ArrayList<byte[]>();

        int remainSize = countRemainSize(buf.length, capacity - PackHead.SIZE);
        int sumLength = buf.length;
        int index = 0;
        do {
            int packLen = Math.min(sumLength - index + PackHead.SIZE, capacity);
            byte[] pack = new byte[packLen];
            int bodyLen = packLen - PackHead.SIZE;
            System.arraycopy(buf, index, pack, PackHead.SIZE, bodyLen);

            PackHead head = PackHead.create(seqId);
            head.setSumLen(sumLength);
            head.setRemainLen(remainSize);
            head.setBodyLen(bodyLen);
            System.arraycopy(head.toBytes(), 0, pack, 0, PackHead.SIZE);

            index += packLen - PackHead.SIZE;
            remainSize = countRemainSize(buf.length - index, capacity - PackHead.SIZE);
            dataList.add(pack);
        } while (index < sumLength);

        return dataList;
    }

    private static int countRemainSize(int bufLength, int packSize) {
        int remainSize = bufLength - packSize;
        if (remainSize < 0) {
            remainSize = 0;
        }
        return remainSize;
    }

}
