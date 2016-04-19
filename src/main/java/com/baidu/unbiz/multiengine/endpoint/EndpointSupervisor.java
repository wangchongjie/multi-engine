package com.baidu.unbiz.multiengine.endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

import com.baidu.unbiz.multiengine.endpoint.gossip.GossipSupport;
import com.baidu.unbiz.multiengine.endpoint.heartbeat.HeartbeatSupport;
import com.baidu.unbiz.multiengine.transport.client.TaskClient;
import com.baidu.unbiz.multiengine.transport.server.TaskServer;
import com.baidu.unbiz.multiengine.transport.server.TaskServerFactory;
import com.baidu.unbiz.multitask.log.AopLogFactory;

/**
 * Created by wangchongjie on 16/4/15.
 */
public class EndpointSupervisor {
    private static final Logger LOG = AopLogFactory.getLogger(EndpointSupervisor.class);

    private static List<TaskServer> taskServers;
    private GossipSupport gossipSupport;
    private HeartbeatSupport heartbeatSupport;

    private String exportPort;
    private String serverHost;

    public EndpointSupervisor() {
        taskServers = new CopyOnWriteArrayList<TaskServer>();
        gossipSupport = new GossipSupport();
        heartbeatSupport = new HeartbeatSupport() {
            @Override
            public void tryRestartEndpoint(TaskClient taskClient) {
                doTryRestartEndpoint(taskClient);
            }
        };
    }

    public static List<HostConf> getTaskHostConf(){
        List<HostConf> hostConfs = new ArrayList<HostConf>();
        for(TaskServer taskServer : taskServers){
            hostConfs.add(taskServer.getHostConf());
        }
        hostConfs.addAll(EndpointPool.getTaskHostConf());
        return hostConfs;
    }

    public static List<HostConf> mergeTaskServer(List<HostConf> otherHost){
        List<HostConf> hostConfs = getTaskHostConf();
        otherHost.removeAll(hostConfs);
        EndpointPool.add(otherHost);
        return hostConfs;
    }

    public void init() {
        List<HostConf> exportHosts = HostConf.resolvePort(exportPort);
        for (HostConf hostConf : exportHosts) {
            TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
            taskServer.start();
            taskServers.add(taskServer);
        }
        List<HostConf> clientHost = HostConf.resolveHost(this.serverHost);
        EndpointPool.init(clientHost);

        heartbeatSupport.scheduleHeartBeat();
        gossipSupport.scheduleGossip();
    }

    public void stop() {
        EndpointPool.stop();
        if (CollectionUtils.isEmpty(taskServers)) {
            return;
        }
        for (TaskServer taskServer : taskServers) {
            taskServer.stop();
        }

        heartbeatSupport.shutdownScheduler();
        gossipSupport.shutdownScheduler();
    }

    protected void doTryRestartEndpoint(TaskClient taskClient) {
        try {
            taskClient.restart();
        } catch (Exception e) {
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
