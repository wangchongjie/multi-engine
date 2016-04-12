package com.baidu.unbiz.multiengine.transport;

import java.util.concurrent.atomic.AtomicLong;

/**
 * LogIdGen的缺省实现,在内存中从1原子性的累加,线程安全,但jvm重启后会从1重新累加
 * 
 * @author wangchongjie
 * 
 */
public class SequenceIdGen {
    private AtomicLong counter = new AtomicLong(0);

    public long genId() {
        long v = counter.incrementAndGet();
        if (v < 0) {
            v &= Long.MAX_VALUE;
        }
        if (v == 0) {
            return genId();
        }
        return v;
    }

}
