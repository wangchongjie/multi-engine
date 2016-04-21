package com.baidu.unbiz.multiengine.demo.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.baidu.unbiz.multiengine.common.DisTaskPair;
import com.baidu.unbiz.multiengine.vo.DeviceRequest;
import com.baidu.unbiz.multiengine.vo.DeviceViewItem;
import com.baidu.unbiz.multiengine.vo.QueryParam;
import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.forkjoin.ForkJoin;
import com.baidu.unbiz.multitask.task.ParallelExePool;
import com.baidu.unbiz.multitask.task.thread.MultiResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class DistributededParallelFetchTest {

    @Resource(name = "distributedParallelExePool")
    private ParallelExePool parallelExePool;

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
     * 分布式ForkJoin并行查询测试
     */
    @Test
    public void testDistributeParallelForkJoinFetch() {
        DisTaskPair taskPair = new DisTaskPair("deviceStatFetcher", DeviceRequest.build(new QueryParam()));

        ForkJoin<DeviceRequest, List<DeviceViewItem>> forkJoin = new ForkJoin<DeviceRequest, List<DeviceViewItem>>() {

            public List<DeviceRequest> fork(DeviceRequest deviceRequest) {
                List<DeviceRequest> reqs = new ArrayList<DeviceRequest>();
                reqs.add(deviceRequest);
                reqs.add(deviceRequest);
                reqs.add(deviceRequest);
                return reqs;
            }

            public List<DeviceViewItem> join(List<List<DeviceViewItem>> lists) {
                List<DeviceViewItem> result = new ArrayList<DeviceViewItem>();
                if (CollectionUtils.isEmpty(lists)) {
                    return result;
                }
                for (List<DeviceViewItem> res : lists) {
                    result.addAll(res);
                }
                return result;
            }
        };

        List<DeviceViewItem> result = parallelExePool.submit(taskPair, forkJoin);

        Assert.notEmpty(result);
        System.out.println(result);
    }

    /**
     * 测试分布式并行执行task
     */
    @Test
    public void testConcurrentDistributedParallelRunDisTask() {
        final int loopCnt = 100;
        final CountDownLatch latch = new CountDownLatch(loopCnt);

        for (int i = 0; i < loopCnt; i++) {
            new Thread() {
                @Override
                public void run() {
                    testDistributedParallelRunDisTask();
                    latch.countDown();
                }
            }.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
