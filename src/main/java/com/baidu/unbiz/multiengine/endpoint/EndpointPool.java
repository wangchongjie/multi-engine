package com.baidu.unbiz.multiengine.endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import com.baidu.unbiz.multiengine.exception.MultiEngineException;
import com.baidu.unbiz.multiengine.transport.client.TaskClient;
import com.baidu.unbiz.multiengine.transport.client.TaskClientFactory;
import com.baidu.unbiz.multiengine.transport.server.TaskServer;
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

    public static void add(List<HostConf> serverList) {
        for (HostConf hostConf : serverList) {
            TaskClient endpoint = clientFactory.createTaskClient(hostConf);

            boolean success = endpoint.start();
            endpoint.setInvalid(!success);
            pool.add(endpoint);
        }
    }

    public static List<HostConf> getTaskHostConf(){
        List<HostConf> hostConfs = new ArrayList<HostConf>();
        for(TaskClient taskServer : pool){
            hostConfs.add(taskServer.getHostConf());
        }
        return hostConfs;
    }

    private static void doInit(List<HostConf> serverList) {
        if (CollectionUtils.isEmpty(serverList)) {
            LOG.error("serverList is empty");
        }
        add(serverList);
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
        return selectEndpoint(pool.size(), true);
    }

    private static TaskClient selectEndpoint(int retry, boolean first) {
        try {
            hasInit.await();
        } catch (InterruptedException e) {
            LOG.error("select endpoint:", e);
        }

        if (retry < 0) {
            throw new MultiEngineException("select endpoint retry fail");
        }
        Assert.isTrue(pool.size() > 0);

        int idx = retry;
        if(first) {
            idx = index.addAndGet(1);
        }
        TaskClient endpoint = pool.get((Math.abs(idx) % pool.size()));
        if (endpoint.getInvalid().get()) {
            return selectEndpoint(retry - 1, false);
        }
        return endpoint;
    }

    public static List<TaskClient> getPool() {
        return pool;
    }

}
