package com.baidu.unbiz.multiengine.transport;

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
