package com.baidu.unbiz.multiengine.transport.client;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Task提交客户端实例状态
 *
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
