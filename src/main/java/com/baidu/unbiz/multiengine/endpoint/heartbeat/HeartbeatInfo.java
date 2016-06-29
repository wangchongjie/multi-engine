package com.baidu.unbiz.multiengine.endpoint.heartbeat;

/**
 * Created by wangchongjie on 16/4/18.
 */
public class HeartbeatInfo {
    public static final long DEFAULT_TIMEOUT = 5 * 1000L;

    // server timeout ms
    private long timeout = DEFAULT_TIMEOUT;

    public HeartbeatInfo() {
    }

    public HeartbeatInfo(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public static class Holder {
        public static final HeartbeatInfo instance = new HeartbeatInfo();
    }
}
