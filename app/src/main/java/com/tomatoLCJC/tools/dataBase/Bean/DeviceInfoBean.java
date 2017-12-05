package com.tomatoLCJC.tools.dataBase.Bean;

import org.litepal.crud.DataSupport;

/**
 *  系统信息
 */
public class DeviceInfoBean extends DataSupport {
    private long deviceID;                  // 设备ID
    private String deviceName;              // 设备名称
    private int channelCount;               // 通道数量
    private int channelWeight;              // 通道分量
    private int channelDistance;            // 通道间距
    private int stepInterval;               //横向梯度步数间隔
    private double stepDistance;            // 步长
    private String releaseDate;             // 出厂日期
    private String saleDate;                // 出售日期
    private String hardwareVersion;         // 硬件版本
    private String softwareVersion;         // 软件版本
    private double deviceWidth;             // 设备宽度
    private double deviceLength;            // 设备长度
    private double deviceTailDeadZoneLength;// 设备后端盲区长度
    private double deviceHeadDeadZoneLength;// 设备前端盲区长度
    private double deviceWeight;            // 设备重量
    private String servicePhone;            // 服务电话
    private String note;                    // 备注

    public long getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(long deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getChannelCount() {
        return channelCount;
    }

    public void setChannelCount(int channelCount) {
        this.channelCount = channelCount;
    }

    public int getChannelWeight() {
        return channelWeight;
    }

    public void setChannelWeight(int channelWeight) {
        this.channelWeight = channelWeight;
    }

    public int getChannelDistance() {
        return channelDistance;
    }

    public void setChannelDistance(int channelDistance) {
        this.channelDistance = channelDistance;
    }

    public int getStepInterval() {
        return stepInterval;
    }

    public void setStepInterval(int stepInterval) {
        this.stepInterval = stepInterval;
    }

    public double getStepDistance() {
        return stepDistance;
    }

    public void setStepDistance(double stepDistance) {
        this.stepDistance = stepDistance;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public double getDeviceWidth() {
        return deviceWidth;
    }

    public void setDeviceWidth(double deviceWidth) {
        this.deviceWidth = deviceWidth;
    }

    public double getDeviceLength() {
        return deviceLength;
    }

    public void setDeviceLength(double deviceLength) {
        this.deviceLength = deviceLength;
    }

    public double getDeviceTailDeadZoneLength() {
        return deviceTailDeadZoneLength;
    }

    public void setDeviceTailDeadZoneLength(double deviceTailDeadZoneLength) {
        this.deviceTailDeadZoneLength = deviceTailDeadZoneLength;
    }

    public double getDeviceHeadDeadZoneLength() {
        return deviceHeadDeadZoneLength;
    }

    public void setDeviceHeadDeadZoneLength(double deviceHeadDeadZoneLength) {
        this.deviceHeadDeadZoneLength = deviceHeadDeadZoneLength;
    }

    public double getDeviceWeight() {
        return deviceWeight;
    }

    public void setDeviceWeight(double deviceWeight) {
        this.deviceWeight = deviceWeight;
    }

    public String getServicePhone() {
        return servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
