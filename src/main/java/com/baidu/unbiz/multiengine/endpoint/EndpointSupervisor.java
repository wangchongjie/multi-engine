package com.baidu.unbiz.multiengine.endpoint;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

import com.baidu.unbiz.multiengine.transport.client.TaskClient;
import com.baidu.unbiz.multiengine.transport.server.TaskServer;
import com.baidu.unbiz.multiengine.transport.server.TaskServerFactory;
import com.baidu.unbiz.multitask.log.AopLogFactory;

/**
 * Created by wangchongjie on 16/4/15.
 */
public class EndpointSupervisor extends HeartbeatSupport {
    private static final Logger LOG = AopLogFactory.getLogger(EndpointSupervisor.class);

    private static List<TaskServer> taskServers = new ArrayList<TaskServer>();

    private String exportPort;
    private String serverHost;

    public void init() {
        List<HostConf> exportHosts = HostConf.resolvePort(exportPort);
        for (HostConf hostConf : exportHosts) {
            TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
            taskServer.start();
            taskServers.add(taskServer);
        }
        List<HostConf> clientHost = HostConf.resolveHost(this.serverHost);
        EndpointPool.init(clientHost);

        scheduleHeartBeat();
    }

    public void stop() {
        EndpointPool.stop();
        if (CollectionUtils.isEmpty(taskServers)) {
            return;
        }
        for (TaskServer taskServer : taskServers) {
            taskServer.stop();
        }
        scheduler.shutdown();
    }

    protected void tryRestartEndpoint(TaskClient taskClient) {
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
