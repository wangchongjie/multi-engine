package com.baidu.unbiz.multiengine.endpoint;

import java.util.List;

import com.baidu.unbiz.multiengine.transport.server.TaskServer;
import com.baidu.unbiz.multiengine.transport.server.TaskServerFactory;

/**
 * Created by wangchongjie on 16/4/15.
 */
public class EndpointConsole {

    private String serverHost;
    private String clientHost;
    private TaskServer taskServer;

    public void init() {
        List<HostConf> sHostConfs = HostConf.resolveHost(serverHost);
        for (HostConf hostConf : sHostConfs) {
            taskServer = TaskServerFactory.createTaskServer(hostConf);
            taskServer.start();
        }
        List<HostConf> cHostConfs = HostConf.resolveHost(clientHost);
        EndpointPool.init(cHostConfs);
    }

    public void stop() {
        EndpointPool.stop();
        if (taskServer == null) {
            return;
        }
        taskServer.stop();
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
