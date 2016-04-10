package com.baidu.unbiz.multiengine.demo.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
                    TaskServer.main(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread clientThread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    TaskClient.main(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        serverThread.start();
        clientThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Object result = TaskClient.getResult();
        System.out.println(result);
    }


}
