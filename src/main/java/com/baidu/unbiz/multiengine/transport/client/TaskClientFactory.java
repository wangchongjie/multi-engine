package com.baidu.unbiz.multiengine.transport.client;

import com.baidu.unbiz.multiengine.transport.DefaultSessionIdProvider;
import com.baidu.unbiz.multiengine.transport.HostConf;
import com.baidu.unbiz.multiengine.transport.SessionIdProvider;

/**
 * Created by wangchongjie on 16/4/11.
 */
public class TaskClientFactory {

    private static SessionIdProvider idProvider = new DefaultSessionIdProvider("TaskClient");

    public static TaskClient createTaskClient(HostConf hostConf) {
        TaskClient taskClient = new TaskClient();
        taskClient.setHostConf(hostConf);
        taskClient.setSessionKey(idProvider.getSessionId(true));
        return taskClient;
    }
}
