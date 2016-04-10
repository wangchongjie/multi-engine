package com.baidu.unbiz.multiengine.demo.test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.constants.DefaultThreadPoolConfig;
import com.baidu.unbiz.multitask.task.ParallelExePool;
import com.baidu.unbiz.multitask.task.thread.MultiResult;
import com.baidu.unbiz.multitask.task.thread.TaskManager;
import com.baidu.unbiz.multiengine.vo.DeviceRequest;
import com.baidu.unbiz.multiengine.vo.DeviceViewItem;
import com.baidu.unbiz.multiengine.vo.QueryParam;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class ThreadPoolConfigRefreshTest {

    @Resource(name = "simpleParallelExePool")
    private ParallelExePool parallelExePool;

    @Before
    public void init() {
        TaskManager.refreshConfig(new DefaultThreadPoolConfig() {
            public int maxTaskNum() {
                return MAX_TASK_NUM + 10;
            }
        });
    }

    /**
     * 正常并行查询测试
     */
    @Test
    public void testInitThreadPoolConfig() {
        QueryParam qp = new QueryParam();
        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp));

        MultiResult ctx =
                parallelExePool.submit(
                        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)));

        List<DeviceViewItem> stat = ctx.getResult("deviceStatFetcher");
        List<DeviceViewItem> uv = ctx.getResult("deviceUvFetcher");

        Assert.notEmpty(stat);
        Assert.notEmpty(uv);
        System.out.println(stat);
        System.out.println(uv);
    }
}
