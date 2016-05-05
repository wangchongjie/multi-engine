package com.baidu.unbiz.multiengine.transport.client;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wangchongjie on 16/5/5.
 */
public class TaskClientStatus {

    private AtomicBoolean invalid = new AtomicBoolean(false);

    public AtomicBoolean getInvalid() {
        return invalid;
    }

    public void setInvalid(AtomicBoolean invalid) {
        this.invalid = invalid;
    }
}
