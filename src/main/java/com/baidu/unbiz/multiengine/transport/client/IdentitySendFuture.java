package com.baidu.unbiz.multiengine.transport.client;

/**
 * 含有ID标示的SendFuture
 *
 * Created by wangchongjie on 16/4/14.
 */
public class IdentitySendFuture extends SimpleSendFutrue {

    public IdentitySendFuture() {
    }

    public IdentitySendFuture(long id) {
        this.id = id;
    }

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
