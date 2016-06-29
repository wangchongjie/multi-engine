package com.baidu.unbiz.multiengine.demo.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.unbiz.multiengine.endpoint.supervisor.DefaultEndpointSupervisor;
import com.baidu.unbiz.multiengine.utils.TestUtils;

/**
 * Created by wangchongjie on 16/4/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test2.xml")
public class TestMultiProcessServer3 {

    private DefaultEndpointSupervisor supervisor = new DefaultEndpointSupervisor();

    @Before
    public void init() {
        supervisor.setExportPort("8803");
        supervisor.setServerHost("127.0.0.1:8801;127.0.0.1:8803");
        supervisor.init();
    }

    @After
    public void clean() {
        supervisor.stop();
    }

    /**
     * 测试分布式并行执行task
     */
    @Test
    public void runServer() {
        TestUtils.dumySleep(TestUtils.VERY_LONG_TIME);
    }

}
