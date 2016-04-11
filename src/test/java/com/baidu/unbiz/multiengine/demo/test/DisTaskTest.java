package com.baidu.unbiz.multiengine.demo.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.baidu.unbiz.multiengine.dto.RpcResult;
import com.baidu.unbiz.multiengine.dto.TaskCommand;
import com.baidu.unbiz.multiengine.transport.HostConf;
import com.baidu.unbiz.multiengine.transport.TaskClient;
import com.baidu.unbiz.multiengine.transport.TaskClientFactory;
import com.baidu.unbiz.multiengine.transport.TaskServer;
import com.baidu.unbiz.multiengine.transport.TaskServerFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class DisTaskTest {

    @Test
    public void testRunDisTask() {

        HostConf hostConf = new HostConf();

        TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
        taskServer.start();
        dumySleep(500);

        TaskClient taskClient = TaskClientFactory.createTaskClient(hostConf);
        taskClient.start();
        dumySleep(500);

        TaskCommand command = new TaskCommand();
        command.setTaskBean("deviceStatFetcher");
        command.setParams(null);
        RpcResult result = taskClient.makeCall(command);

        Assert.notNull(result);
        System.out.println(result.getResult());
    }

    private void dumySleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

}
