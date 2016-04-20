package com.baidu.unbiz.multiengine.endpoint.heartbeat;

import java.util.List;
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
public abstract class HeartbeatSupport {

    private static final Logger LOG = AopLogFactory.getLogger(HeartbeatSupport.class);

    protected static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor
            (new CustomizableThreadFactory("EndpointSupervisorScheduler"));
    protected static long heartbeatInterval = 10 * 1000;

    public void shutdownScheduler() {
        scheduler.shutdown();
    }

    public void scheduleHeartbeat() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    doHeartbeat();
                } catch (Exception e) {
                    LOG.error("scheduleHeartbeat", e);
                }

            }
        }, 1000, heartbeatInterval, TimeUnit.MILLISECONDS);
    }

    private void doHeartbeat() {
        List<TaskClient> pool = EndpointPool.getPool();
        for (TaskClient taskClient : pool) {
            if(taskClient.isInvalid()) {
                tryRestartEndpoint(taskClient);
            }
            boolean isAlive = taskClient.heartBeat(HeartbeatInfo.Holder.instance);
            boolean invalid = taskClient.getInvalid().getAndSet(!isAlive);
            if (isAlive == invalid) {
                LOG.info(String.format("%s change alive to:%s", taskClient.getHostConf(), isAlive));
            }
            LOG.debug(String.format("%s is alive:%s", taskClient.getHostConf(), isAlive));
        }
    }

    protected void tryRestartEndpoint(TaskClient taskClient) {
    }
}
