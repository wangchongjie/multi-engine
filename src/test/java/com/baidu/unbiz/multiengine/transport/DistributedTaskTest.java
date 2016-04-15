package com.baidu.unbiz.multiengine.transport;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.baidu.unbiz.multiengine.endpoint.HostConf;
import com.baidu.unbiz.multiengine.task.TaskCommand;
import com.baidu.unbiz.multiengine.transport.client.SendFuture;
import com.baidu.unbiz.multiengine.transport.client.TaskClient;
import com.baidu.unbiz.multiengine.transport.client.TaskClientFactory;
import com.baidu.unbiz.multiengine.transport.server.TaskServer;
import com.baidu.unbiz.multiengine.transport.server.TaskServerFactory;
import com.baidu.unbiz.multiengine.vo.DeviceViewItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class DistributedTaskTest {

    @Test
    public void testRunDisTask() {
        HostConf hostConf = new HostConf();

        TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
        taskServer.start();

        TaskClient taskClient = TaskClientFactory.createTaskClient(hostConf);
        taskClient.start();

        TaskCommand command = new TaskCommand();
        command.setTaskBean("deviceStatFetcher");
        command.setParams(null);
        List<DeviceViewItem> result = taskClient.call(command);

        Assert.notNull(result);
        System.out.println(result);

        taskClient.stop();
        taskServer.stop();
    }

    @Test
    public void testAsynRunDisTask() {
        HostConf hostConf = new HostConf();

        TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
        taskServer.start();

        TaskClient taskClient = TaskClientFactory.createTaskClient(hostConf);
        taskClient.start();

        TaskCommand command = new TaskCommand();
        command.setTaskBean("deviceStatFetcher");
        command.setParams(null);
        SendFuture sendFuture = taskClient.asynCall(command);
        List<DeviceViewItem> result = sendFuture.get();

        Assert.notNull(result);
        System.out.println(result);

        taskClient.stop();
        taskServer.stop();
    }

    @Test
    public void testRunDisTaskByBigData() {

        HostConf hostConf = new HostConf();

        TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
        taskServer.start();

        TaskClient taskClient = TaskClientFactory.createTaskClient(hostConf);
        taskClient.start();

        TaskCommand command = new TaskCommand();
        command.setTaskBean("deviceBigDataStatFetcher");
        command.setParams(null);
        List<DeviceViewItem> result = taskClient.call(command);

        Assert.notNull(result);
        System.out.println(result);

        taskClient.stop();
        taskServer.stop();
    }


    @Test
    public void testRunDisTasksByBigResult() {
        HostConf hostConf = new HostConf();

        TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
        taskServer.start();

        final TaskClient taskClient = TaskClientFactory.createTaskClient(hostConf);
        taskClient.start();

        for (int i = 0; i < 10; i++) {
            TaskCommand command = new TaskCommand();
            command.setTaskBean("deviceBigDataStatFetcher");
            command.setParams(null);
            List<DeviceViewItem> result = taskClient.call(command);

            Assert.notNull(result);
            System.out.println(result);
        }

        taskClient.stop();
        taskServer.stop();
    }

    @Test
    public void testConcurrentRunDisTask() {
        HostConf hostConf = new HostConf();

        TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
        taskServer.start();

        final TaskClient taskClient = TaskClientFactory.createTaskClient(hostConf);
        taskClient.start();

        final CountDownLatch latch = new CountDownLatch(50);

        for (int i = 0; i < 50; i++) {
            new Thread() {
                @Override
                public void run() {
                    TaskCommand command = new TaskCommand();
                    command.setTaskBean("deviceBigDataStatFetcher");
                    command.setParams(null);
                    Object result = taskClient.call(command);

                    Assert.notNull(result);
                    System.out.println(result);
                    latch.countDown();
                }
            }.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        taskClient.stop();
        taskServer.stop();
    }

    @Test
    public void testConcurrentRunDisTaskByBigResult() {
        HostConf hostConf = new HostConf();

        TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
        taskServer.start();

        final TaskClient taskClient = TaskClientFactory.createTaskClient(hostConf);
        taskClient.start();

        final CountDownLatch latch = new CountDownLatch(50);

        for (int i = 0; i < 50; i++) {
            new Thread() {
                @Override
                public void run() {
                    TaskCommand command = new TaskCommand();
                    command.setTaskBean("deviceBigDataStatFetcher");
                    command.setParams(null);
                    Object result = taskClient.call(command);

                    Assert.notNull(result);
                    System.out.println(result);
                    latch.countDown();
                }
            }.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        taskClient.stop();
        taskServer.stop();
    }

    @Test
    public void testRunDisTaskByMultiClient() {
        HostConf hostConf = new HostConf();

        TaskServer taskServer = TaskServerFactory.createTaskServer(hostConf);
        taskServer.start();

        TaskClient taskClient1 = TaskClientFactory.createTaskClient(hostConf);
        taskClient1.start();
        TaskClient taskClient2 = TaskClientFactory.createTaskClient(hostConf);
        taskClient2.start();

        TaskCommand command = new TaskCommand();
        command.setTaskBean("deviceStatFetcher");
        command.setParams(null);

        Object result1 = taskClient1.call(command);
        Object result2 = taskClient2.call(command);

        for (int i = 0; i < 500; i++) {
            result1 = taskClient1.call(command);
            result2 = taskClient2.call(command);
            Assert.isTrue(result1.toString().equals(result2.toString()));
        }

        Assert.notNull(result1);
        Assert.notNull(result2);
        System.out.println(result1);
        System.out.println(result2);

        taskClient1.stop();
        taskClient2.stop();
        taskServer.stop();
    }

    private void dumySleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

}
