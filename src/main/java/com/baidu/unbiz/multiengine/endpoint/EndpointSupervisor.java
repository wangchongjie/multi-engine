package com.baidu.unbiz.multiengine.endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
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

    private static List<TaskServer> taskServers = new ArrayList<TaskServer>();

    private static String exportPort;
    private static String serverHost;

    public static void init() {
        List<HostConf> exportHosts = HostConf.resolvePort(exportPort);
        for (HostConf hostConf : exportHosts) {
            TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
            taskServer.start();
            taskServers.add(taskServer);
        }
        List<HostConf> clientHost = HostConf.resolveHost(EndpointSupervisor.serverHost);
        EndpointPool.init(clientHost);

        scheduleHeartBeat();
    }

    public static void stop() {
        EndpointPool.stop();
        if (CollectionUtils.isEmpty(taskServers)) {
            return;
        }
        for (TaskServer taskServer : taskServers) {
            taskServer.stop();
        }
        scheduler.shutdown();
    }

    public static void scheduleHeartBeat() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    doHeartBeat();
                } catch (Exception e) {
                    LOG.error("scheduleHeartBeat", e);
                }

            }
        }, 0, heartbeatInterval, TimeUnit.MILLISECONDS);
    }

    private static void doHeartBeat() {
        List<TaskClient> pool = EndpointPool.getPool();
        for (TaskClient taskClient : pool) {
            if(taskClient.isInvalid()) {
                tryRestartEndpoint(taskClient);
            }
            boolean isAlive = taskClient.heartBeat(HeartBeatInfo.Holder.instance);
            boolean invalid = taskClient.getInvalid().getAndSet(!isAlive);
            if (isAlive == invalid) {
                LOG.info(String.format("%s change alive to:%s", taskClient.getHostConf(), isAlive));
            }
            LOG.debug(String.format("%s is alive:%s", taskClient.getHostConf(), isAlive));
        }
    }

    private static void tryRestartEndpoint(TaskClient taskClient) {
        try {
            taskClient.restart();
        } catch (Exception e){
            // do nothing
        }
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getExportPort() {
        return exportPort;
    }

    public void setExportPort(String exportPort) {
        this.exportPort = exportPort;
    }
}
