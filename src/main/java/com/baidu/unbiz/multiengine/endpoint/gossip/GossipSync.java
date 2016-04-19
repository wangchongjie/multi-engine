package com.baidu.unbiz.multiengine.endpoint.gossip;

/**
 * Created by wangchongjie on 16/4/18.
 */
public class GossipSync {

    private long version;

    public GossipSync() {
        version = System.currentTimeMillis();
    }
}
