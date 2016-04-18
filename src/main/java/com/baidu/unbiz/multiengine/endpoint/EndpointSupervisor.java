package com.baidu.unbiz.multiengine.endpoint;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import com.baidu.unbiz.multiengine.transport.client.TaskClient;
import com.baidu.unbiz.multiengine.transport.server.TaskServer;
import com.baidu.unbiz.multiengine.transport.server.TaskServerFactory;
import com.baidu.unbiz.multitask.log.AopLogFactory;

/**
 * Created by wangchongjie on 16/4/15.
 */
public class EndpointSupervisor {
    private static final Logger LOG = AopLogFactory.getLogger(EndpointSupervisor.class);

    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor
            (new CustomizableThreadFactory("EndpointSupervisorScheduler"));
    private static long heartbeatInterval = 3 * 1000;

    private static TaskServer taskServer;
    private static String serverHost;
    private static String clientHost;

    public static void init() {
        List<HostConf> serverHost = HostConf.resolveHost(EndpointSupervisor.serverHost);
        for (HostConf hostConf : serverHost) {
            taskServer = TaskServerFactory.createTaskServer(hostConf);
            taskServer.start();
        }
        List<HostConf> clientHost = HostConf.resolveHost(EndpointSupervisor.clientHost);
        EndpointPool.init(clientHost);

        scheduleHeartBeat();
    }

    public static void stop() {
        EndpointPool.stop();
        if (taskServer == null) {
            return;
        }
        taskServer.stop();
        scheduler.shutdown();
    }

    public static void scheduleHeartBeat() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try{
                    heartBeat();
                } catch (Exception e) {
                    LOG.error("scheduleHeartBeat", e);
                }

            }
        }, 0, heartbeatInterval, TimeUnit.MILLISECONDS);
    }

    private static void heartBeat() {
        List<TaskClient> pool = EndpointPool.getPool();
        for (TaskClient taskClient : pool) {
            boolean isAlive = taskClient.heartBeat(new HeartBeatInfo(3000));
            LOG.debug(String.format("%s isAlive:%s", taskClient.getHostConf(), isAlive));
        }
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getClientHost() {
        return clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }
}
