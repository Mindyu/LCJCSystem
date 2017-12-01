package com.tomatoLCJC.usbutil;

/**
 *  设备信息类，用于在串口工具类中初始化设备
 */
public class DeviceInfo {
    //初始化设备的参数
    public final int BAUDRATE=921600;//波特率
    public final byte STOPBIT=1; //停止位
    public final byte DATABIT=8; //数据位
    public final byte PARITY=0;  //是否进行奇偶校验，0代表否
    public final byte FLOWCONTROL=0; //流控制
    //设备信息的单例
    private static DeviceInfo deviceInfo =null;
    private DeviceInfo(){}
    public static DeviceInfo getDeviceInfoInstance(){
        if(deviceInfo ==null){
            deviceInfo =new DeviceInfo();
        }
        return deviceInfo;
    }
}
