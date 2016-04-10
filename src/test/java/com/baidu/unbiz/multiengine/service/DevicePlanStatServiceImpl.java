package com.baidu.unbiz.multiengine.service;

import java.util.ArrayList;
import java.util.List;

import com.baidu.unbiz.multiengine.exception.BusinessException;
import com.baidu.unbiz.multiengine.vo.DeviceRequest;
import com.baidu.unbiz.multiengine.vo.DeviceViewItem;
import com.baidu.unbiz.multitask.annotation.TaskBean;
import com.baidu.unbiz.multitask.annotation.TaskService;

/**
 * 该类会被并行组件监测到，并将其方法包装成可并行执行的Fetcher
 *
 */
@TaskService
public class DevicePlanStatServiceImpl implements DevicePlanStatService {

    /**
     * 并行组件会将该方法包装成一个可并行执行的Fetcher
     */
    @TaskBean("deviceStatFetcher")
    public List<DeviceViewItem> queryPlanDeviceData(DeviceRequest req) {
        this.checkParam(req);
        // Test ThreadLocal
        System.out.println(MyThreadLocal.get());
        return this.mockList1();
    }

    /**
     * 并行组件会将该方法包装成一个可并行执行的Fetcher
     */
    @TaskBean("deviceUvFetcher")
    public List<DeviceViewItem> queryPlanDeviceUvData(DeviceRequest req) {
        this.checkParam(req);
        return this.mockList2();
    }

    /**
     * 并行组件会将该方法包装成一个可并行执行的Fetcher
     */
    @TaskBean("doSthVerySlowFetcher")
    public List<DeviceViewItem> queryPlanDeviceDataWithBadNetwork(DeviceRequest req) {
        try {
            Thread.sleep(900000L);
        } catch (InterruptedException e) {
            // do nothing, just for test
        }
        return this.mockList1();
    }

    /**
     * 并行组件会将该方法包装成一个可并行执行的Fetcher
     */
    @TaskBean("doSthFailWithExceptionFetcher")
    public List<DeviceViewItem> queryPlanDeviceDataWithBusinessException(DeviceRequest req) {
        throw new BusinessException("Some business com.baidu.unbiz.multiengine.vo.exception, just for test!");
    }

    /**
     * 方法带有多个参数
     */
    @TaskBean("multiParamFetcher")
    public List<DeviceViewItem> queryPlanDeviceDataByMultiParam(String p1, int p2, int p3) {
        return this.mockList1();
    }

    /**
     * 方法带有多个参数
     */
    @TaskBean("voidParamFetcher")
    public List<DeviceViewItem> queryPlanDeviceDataByVoidParam() {
        return this.mockList2();
    }

    /**
     * 参数检验
     */
    private void checkParam(DeviceRequest req) {
        // req.getDeviceIds();
        // do sth
    }

    private List<DeviceViewItem> mockList1() {
        List<DeviceViewItem> list = new ArrayList<DeviceViewItem>();
        list.add(new DeviceViewItem());
        list.add(new DeviceViewItem());
        return list;
    }

    private List<DeviceViewItem> mockList2() {
        List<DeviceViewItem> list = new ArrayList<DeviceViewItem>();
        list.add(new DeviceViewItem());
        list.add(new DeviceViewItem());
        list.add(new DeviceViewItem());
        return list;
    }
}
