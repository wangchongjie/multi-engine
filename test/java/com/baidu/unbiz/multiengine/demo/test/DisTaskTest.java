package com.baidu.unbiz.multiengine.demo.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.unbiz.multiengine.transport.TaskServer;
import com.baidu.unbiz.multiengine.transport.TaskServerHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class DisTaskTest {

//    @Resource
//    TaskServerHandler handler;

    /**
     * 正常并行查询测试
     */
    @Test
    public void testDisTask() {
        try {
            TaskServer.main(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
