package com.baidu.unbiz.multiengine.transport.client;

import com.baidu.unbiz.multiengine.transport.HostConf;

/**
 * Created by wangchongjie on 16/4/11.
 */
public class TaskClientFactory {

    public static TaskClient createTaskClient(HostConf hostConf) {
        TaskClient taskClient = new TaskClient();
        taskClient.setHostConf(hostConf);
        return taskClient;
    }
}
