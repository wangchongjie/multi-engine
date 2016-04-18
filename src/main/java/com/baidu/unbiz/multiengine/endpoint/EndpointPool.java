package com.baidu.unbiz.multiengine.endpoint;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import com.baidu.unbiz.multiengine.transport.client.TaskClient;
import com.baidu.unbiz.multiengine.transport.client.TaskClientFactory;
import com.baidu.unbiz.multitask.log.AopLogFactory;

/**
 * Created by wangchongjie on 16/4/14.
 */
public class EndpointPool {
    private static final Logger LOG = AopLogFactory.getLogger(EndpointPool.class);

    private static List<TaskClient> pool = new CopyOnWriteArrayList<TaskClient>();
    private static TaskClientFactory clientFactory = new TaskClientFactory();
    private static AtomicInteger index = new AtomicInteger();
    private static CountDownLatch hasInit = new CountDownLatch(1);
    private static AtomicBoolean initing = new AtomicBoolean(false);

    public static void init(List<HostConf> serverList) {
        if (initing.compareAndSet(false, true)) {
            doInit(serverList);
            hasInit.countDown();
        }
    }

    public static void doInit(List<HostConf> serverList) {
        if (CollectionUtils.isEmpty(serverList)) {
            LOG.error("serverList is empty");
        }
        for (HostConf hostConf : serverList) {
            TaskClient endpoint = clientFactory.createTaskClient(hostConf);
            if (endpoint.start()) {
                pool.add(endpoint);
            }
        }
    }

    public static void stop() {
        if (CollectionUtils.isEmpty(pool)) {
            return;
        }
        for (TaskClient client : pool) {
            client.stop();
        }
    }

    public static TaskClient selectEndpoint() {
        try {
            hasInit.await();
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.isTrue(pool.size() > 0);
        int idx = index.addAndGet(1);
        return pool.get((Math.abs(idx) % pool.size()));
    }

    public static List<TaskClient> getPool() {
        return pool;
    }

}
