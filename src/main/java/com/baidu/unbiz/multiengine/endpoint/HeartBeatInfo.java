package com.baidu.unbiz.multiengine.endpoint;

/**
 * Created by wangchongjie on 16/4/18.
 */
public class HeartBeatInfo {
    public final long DEFAULT_TIMEOUT = 3000L;

    private long timeout = DEFAULT_TIMEOUT;

    public HeartBeatInfo() {
    }

    public HeartBeatInfo(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public static class Holder {
        public static final HeartBeatInfo instance = new HeartBeatInfo();
    }
}
