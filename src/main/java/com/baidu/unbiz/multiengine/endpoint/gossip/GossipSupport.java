package com.baidu.unbiz.multiengine.endpoint.gossip;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import com.baidu.unbiz.multiengine.endpoint.EndpointPool;
import com.baidu.unbiz.multiengine.transport.client.TaskClient;
import com.baidu.unbiz.multitask.log.AopLogFactory;

/**
 * Created by wangchongjie on 16/4/18.
 */
public class GossipSupport {

    private static final Logger LOG = AopLogFactory.getLogger(GossipSupport.class);

    protected static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor
            (new CustomizableThreadFactory("GossipScheduler"));
    protected static long heartbeatInterval = 3 * 1000;

    public void shutdownScheduler() {
        scheduler.shutdown();
    }

    public void scheduleGossip() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    doGossip();
                } catch (Exception e) {
                    LOG.error("scheduleGossip", e);
                }

            }
        }, 0, heartbeatInterval, TimeUnit.MILLISECONDS);
    }

    private void doGossip() {
        TaskClient taskClient = EndpointPool.selectEndpoint();
        taskClient.syncGossip();
    }
}
