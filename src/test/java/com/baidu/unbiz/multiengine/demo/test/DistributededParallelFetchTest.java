package com.baidu.unbiz.multiengine.demo.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.baidu.unbiz.multiengine.common.DisTaskPair;
import com.baidu.unbiz.multiengine.endpoint.EndpointPool;
import com.baidu.unbiz.multiengine.task.DistributedParallelExePool;
import com.baidu.unbiz.multiengine.endpoint.HostConf;
import com.baidu.unbiz.multiengine.transport.server.TaskServer;
import com.baidu.unbiz.multiengine.transport.server.TaskServerFactory;
import com.baidu.unbiz.multiengine.vo.DeviceRequest;
import com.baidu.unbiz.multiengine.vo.DeviceViewItem;
import com.baidu.unbiz.multiengine.vo.QueryParam;
import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.task.thread.MultiResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class DistributededParallelFetchTest {

    @Resource(name = "distributedParallelExePool")
    private DistributedParallelExePool parallelExePool;

    private TaskServer taskServer;

    /**
     * 测试分布式并行执行task
     */
    @Test
    public void testDistributedParallelRunDisTask() {
        QueryParam qp = new QueryParam();
        MultiResult ctx =
                parallelExePool.submit(
                        new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)),
                        new TaskPair("voidParamFetcher", null),
                        new DisTaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new DisTaskPair("deviceBigDataStatFetcher", DeviceRequest.build(qp)));

        List<DeviceViewItem> stat = ctx.getResult("deviceStatFetcher");
        List<DeviceViewItem> uv = ctx.getResult("deviceUvFetcher");
        List<DeviceViewItem> vstat = ctx.getResult("voidParamFetcher");
        List<DeviceViewItem> bstat = ctx.getResult("deviceBigDataStatFetcher");

        Assert.notEmpty(stat);
        Assert.notEmpty(uv);
        Assert.notEmpty(vstat);
        Assert.notEmpty(bstat);

        System.out.println(stat);
        System.out.println(uv);
        System.out.println(vstat);
        System.out.println(bstat);
    }

    /**
     * 测试分布式并行执行task
     */
    @Test
    public void testDistributedParallelRunDisTask2() {
        for (int i = 0; i < 20; i++) {
            this.testDistributedParallelRunDisTask();
        }
    }

    @Before
    public void init() {
        List<HostConf> hostConfs = new ArrayList<HostConf>();
        for (int i = 8007; i < 8012; i++) {
            HostConf hostConf = new HostConf();
            hostConf.setPort(i);
            hostConfs.add(hostConf);
            taskServer = TaskServerFactory.createTaskServer(hostConf);
            taskServer.start();
        }
        EndpointPool.init(hostConfs);
    }

    @After
    public void clean() {
        taskServer.stop();
    }

}
