package com.baidu.unbiz.multiengine.endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;

import com.baidu.unbiz.multiengine.exception.MultiEngineException;
import com.baidu.unbiz.multiengine.transport.client.TaskClient;
import com.baidu.unbiz.multiengine.transport.client.TaskClientFactory;

/**
 * Created by wangchongjie on 16/4/14.
 */
public class EndpointPool {

    private static List<TaskClient> pool = new ArrayList<TaskClient>();
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
            throw new MultiEngineException("serverList is empty");
        }
        for (HostConf hostConf : serverList) {
            TaskClient taskClient = clientFactory.createTaskClient(hostConf);
            taskClient.start();
            pool.add(taskClient);
        }
    }

    public static TaskClient selectEndpoint() {
        try {
            hasInit.await();
        } catch (InterruptedException e) {
            // do nothing
        }
        int idx = index.addAndGet(1);
        return pool.get((Math.abs(idx) % pool.size()));
    }

}
