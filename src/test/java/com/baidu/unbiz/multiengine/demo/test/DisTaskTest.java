package com.baidu.unbiz.multiengine.demo.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.baidu.unbiz.multiengine.transport.TaskClient;
import com.baidu.unbiz.multiengine.transport.TaskServer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class DisTaskTest {

    @Test
    public void testDisTask() {
        Thread serverThread = new Thread() {
            public void run() {
                try {
                    TaskServer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread clientThread = new Thread() {
            public void run() {
                dumySleep(1000);
                try {
                    TaskClient.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        serverThread.start();
        clientThread.start();

        dumySleep(2000);
        Object result = TaskClient.getResult();
        Assert.notNull(result);
        System.out.println(result);
    }

    private void dumySleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

}
