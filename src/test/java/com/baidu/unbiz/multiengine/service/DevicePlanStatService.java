package com.baidu.unbiz.multiengine.service;

import java.util.List;

import com.baidu.unbiz.multiengine.vo.DeviceRequest;
import com.baidu.unbiz.multiengine.vo.DeviceViewItem;

/**
 * 获取设备数据接口
 *
 * @author wangchongjie
 * @fileName DevicePlanStatService.java
 * @since 2015-7-3 下午3:50:56
 */
public interface DevicePlanStatService {

    /**
     * 查询设备维度基础数据
     */
    List<DeviceViewItem> queryPlanDeviceData(DeviceRequest req);

    /**
     * 查询设备维度uv数据
     */
    List<DeviceViewItem> queryPlanDeviceUvData(DeviceRequest req);

    /**
     * 该接口模拟网络异常慢的情况
     */
    List<DeviceViewItem> queryPlanDeviceDataWithBadNetwork(DeviceRequest req);

    /**
     * 该接口模拟发生一些业务异常
     */
    List<DeviceViewItem> queryPlanDeviceDataWithBusinessException(DeviceRequest req);

    /**
     * 多参数查询设备维度基础数据
     */
    List<DeviceViewItem> queryPlanDeviceDataByMultiParam(String p1, int p2, int p3);

    /**
     * 无参数查询
     */
    List<DeviceViewItem> queryPlanDeviceDataByVoidParam();
}
