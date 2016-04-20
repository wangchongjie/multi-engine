package com.baidu.unbiz.multiengine.transport.client;

import com.baidu.unbiz.multiengine.endpoint.HostConf;
import com.baidu.unbiz.multiengine.transport.DefaultSessionIdProvider;
import com.baidu.unbiz.multiengine.transport.SequenceIdGen;
import com.baidu.unbiz.multiengine.transport.SessionIdProvider;

/**
 * Created by wangchongjie on 16/4/11.
 */
public class TaskClientFactory {

    private static SessionIdProvider idProvider = new DefaultSessionIdProvider("TaskClient");

    public static TaskClient createTaskClient(HostConf hostConf) {
        TaskClient taskClient = new TaskClient(hostConf);
        taskClient.setIdGen(new SequenceIdGen());
        String sessionKey = idProvider.getSessionId(true);
        taskClient.setSessionKey(sessionKey);
        TaskClientContext.sessionClientMap.putIfAbsent(sessionKey, taskClient);
        return taskClient;
    }
}
