package com.baidu.unbiz.multiengine.transport;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.baidu.unbiz.multiengine.dto.RpcResult;
import com.baidu.unbiz.multiengine.dto.TaskCommand;
import com.baidu.unbiz.multiengine.transport.client.TaskClient;
import com.baidu.unbiz.multiengine.transport.client.TaskClientFactory;
import com.baidu.unbiz.multiengine.transport.server.TaskServer;
import com.baidu.unbiz.multiengine.transport.server.TaskServerFactory;

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

    @Test
    public void testRunDisTaskByBigData() {

        HostConf hostConf = new HostConf();

        TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
        taskServer.start();
        dumySleep(500);

        TaskClient taskClient = TaskClientFactory.createTaskClient(hostConf);
        taskClient.start();
        dumySleep(500);

        TaskCommand command = new TaskCommand();
        command.setTaskBean("deviceBigDataStatFetcher");
        command.setParams(null);
        RpcResult result = taskClient.makeCall(command);

        Assert.notNull(result);
        System.out.println(result.getResult());
    }

    @Test
    public void testRunDisTaskByMultiClient() {

        HostConf hostConf = new HostConf();

        TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
        taskServer.start();
        dumySleep(500);

        TaskClient taskClient1 = TaskClientFactory.createTaskClient(hostConf);
        taskClient1.start();
        TaskClient taskClient2 = TaskClientFactory.createTaskClient(hostConf);
        taskClient2.start();
        dumySleep(500);

        TaskCommand command = new TaskCommand();
        command.setTaskBean("deviceStatFetcher");
        command.setParams(null);

        RpcResult result1 = taskClient1.makeCall(command);
        RpcResult result2 = taskClient2.makeCall(command);

        for (int i = 0; i < 500; i++) {
            result1 = taskClient1.makeCall(command);
            result2 = taskClient2.makeCall(command);
            Assert.isTrue(result1.toString().equals(result2.toString()));
        }

        Assert.notNull(result1);
        Assert.notNull(result2);
        System.out.println(result1.getResult());
        System.out.println(result2.getResult());
    }

    private void dumySleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

}
