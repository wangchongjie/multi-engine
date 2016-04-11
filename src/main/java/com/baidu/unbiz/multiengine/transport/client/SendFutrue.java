package com.baidu.unbiz.multiengine.transport.client;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 异步发送消息返回的结果占位符
 * 
 * @author wangchongjie
 * 
 */
public interface SendFutrue {
    /**
     * 获取返回结果
     * 
     * @return 返回发送失败的信息，该list永远不为null
     */
    <T> T get();

    /**
     * 获取返回结果,如果消息已经发送完成或者发送失败会立刻返回，否则等待超时时间，如果超时时间内 消息发送完成或者发送失败会立刻返回,否则抛出TimeoutException异常
     * 
     * @param timeout timeout
     * @param unit unit
     * @return 返回发送失败的信息，该list永远不为null
     * @throws InterruptedException 打断异常
     * @throws TimeoutException 超时异常
     */
    <T> T get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException;

    /**
     * 设置结果数据
     *
     * @param result
     */
    void set(Object result);

    /**
     * 返回当前session id
     * 
     * @return session id
     */
    String getSessionId();
}
