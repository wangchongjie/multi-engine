package com.baidu.unbiz.multiengine.vo;

import java.util.List;

public class DeviceRequest {

    private List<Integer> deviceIds;

    public List<Integer> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<Integer> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public static DeviceRequest build(QueryParam target) {
        DeviceRequest req = new DeviceRequest();
//        req.copyProperties(target);
        return req;
    }
}
