package com.baidu.unbiz.multiengine.transport.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.baidu.unbiz.multiengine.exception.MultiEngineException;

/**
 * 调用方异步发送消息时所持有的发送结果占位符实现，它实现如下接口：
 * <ul>
 * <li>SendFuture 被调用方使用</li>
 * </ul>
 *
 * @author wagnchongjie
 */
class SendFutrueImpl implements SendFuture {
    private final CountDownLatch internalWaiter = new CountDownLatch(1);

    /**
     * 结果信息
     */
    private volatile Object result;

    private volatile boolean hasInit = false;

    @Override
    public <T> T get() {
        try {
            internalWaiter.await();
        } catch (InterruptedException e) {
            throw new MultiEngineException(e);
        }
        return (T) result;
    }

    @Override
    public <T> T get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        if (internalWaiter.await(timeout, unit)) {
            return (T) result;
        } else {
            throw new TimeoutException();
        }
    }

    @Override
    public void set(Object result) {
        this.result = result;
        internalWaiter.countDown();
    }

    @Override
    public void append(Object result, AppendHandler handler, boolean finish) {
        if (!hasInit) {
            this.result = handler.init();
            hasInit = true;
        }
        handler.append(this.result, result);
        if (finish) {
            internalWaiter.countDown();
        }
    }

}
