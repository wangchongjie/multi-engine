package com.baidu.unbiz.multiengine.endpoint;

/**
 * Created by wangchongjie on 16/4/18.
 */
public class HeartbeatInfo2 {
    public final long DEFAULT_TIMEOUT = 5 * 1000L;

    private long timeout = DEFAULT_TIMEOUT;

    public HeartbeatInfo2() {
    }

    public HeartbeatInfo2(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public static class Holder {
        public static final HeartbeatInfo2 instance = new HeartbeatInfo2();
    }
}
