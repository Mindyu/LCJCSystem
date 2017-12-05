package com.tomatoLCJC.tools.Parameter;

import com.tomatoLCJC.tools.dataBase.Bean.DeviceInfoBean;
import com.tomatoLCJC.tools.dataBase.Dao.DeviceDetectionRecordDao;
import com.tomatoLCJC.tools.dataBase.Dao.DeviceInfoDao;

/**
 * Created by zch22 on 2017/5/16.
 *  系统参数
 */
public class SystemParameter {

    public long deviceID=1;                                           // 当前设备的ID
    public int nChannelNumber;                                        // 采样通道数
    public int channelWeight;                                         // 通道分量，默认值为1
    public int nChannelDistance;                                      // 通道间距
    public double disSensorStepLen;                                   // 位移传感器步长(单位：mm)
    public int nStepInterval;                                         // 横向梯度间隔

    private static  SystemParameter systemParameter=null;
    // 向相应表中插入初始数据，并初始化系统参数
    private SystemParameter(){
        DeviceDetectionRecordDao.getInstance().initChannelDetectionRecordTable();   // 初始化通道校准记录表
        DeviceInfoDao.getInstance().initSysInfoTable();                             // 初始化系统信息表
        DeviceInfoBean sysBean= DeviceInfoDao.getInstance().query(deviceID);        // 从数据库取出系统信息
        nChannelDistance=sysBean.getChannelDistance();
        channelWeight=sysBean.getChannelWeight();
        disSensorStepLen=sysBean.getStepDistance();
        nChannelNumber =sysBean.getChannelCount();
        nStepInterval = sysBean.getStepInterval();
    }

    public static SystemParameter getInstance(){
        if(systemParameter==null){
            systemParameter=new SystemParameter();
        }
        return systemParameter;
    }

    public void updateSystemParameter(DeviceInfoBean deviceBean){
        nChannelDistance=deviceBean.getChannelDistance();
        channelWeight=deviceBean.getChannelWeight();
        disSensorStepLen=deviceBean.getStepDistance();
        nChannelNumber =deviceBean.getChannelCount();
        nStepInterval = deviceBean.getStepInterval();
    }

    // 选定设备之后需要更新系统参数
    public void updateSystemParameter(){
        DeviceInfoBean sysBean= DeviceInfoDao.getInstance().query(deviceID);    // 从数据库取出系统信息
        nChannelDistance=sysBean.getChannelDistance();
        channelWeight=sysBean.getChannelWeight();
        disSensorStepLen=sysBean.getStepDistance();
        nChannelNumber =sysBean.getChannelCount();
        nStepInterval = sysBean.getStepInterval();
    }

    public long getDeviceID() {
        return deviceID;
    }
}
