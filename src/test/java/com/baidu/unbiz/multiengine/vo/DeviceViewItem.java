package com.baidu.unbiz.multiengine.vo;

/**
 * 分设备维度模型
 *
 * @author wangchongjie
 * @fileName DeviceViewItem.java
 * @since 2015-7-3 上午10:52:25
 */
public class DeviceViewItem {

    private static final long serialVersionUID = 6132709579470894604L;

    private int planId;

    private String planName;

    private Integer deviceId;

    private String deviceName ;

    // getter and setter
    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }


}
