package com.baidu.unbiz.multiengine.endpoint;

/**
 * Created by wangchongjie on 16/4/18.
 */
public class HeartBeatInfo {

    private long timeout;

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
}
