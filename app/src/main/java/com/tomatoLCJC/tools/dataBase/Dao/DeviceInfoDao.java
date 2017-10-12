package com.tomatoLCJC.tools.dataBase.Dao;


import com.tomatoLCJC.tools.dataBase.Bean.DeviceInfoBean;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * 对设备信息的数据库操作类
 */
public class DeviceInfoDao {

    private static DeviceInfoDao deviceInfoDao = null;
    private DeviceInfoDao() {
        LitePal.deleteDatabase("tomato_leak_android.db");
        LitePal.getDatabase();
    }
    public static DeviceInfoDao getInstance() {
        if (deviceInfoDao == null) {
            deviceInfoDao = new DeviceInfoDao();
        }
        return deviceInfoDao;
    }

    /*****************************************************************
     * 方法名  :  insertFirstRow
     * 功能描述: 将DeviceInfoBean对象存入数据库
     *
     ******************************************************************/
    public void initSysInfoTable(){
        int rowCount = DataSupport.count(DeviceInfoBean.class);
        if (rowCount == 0) {
            for (int i=1;i<=3;i++){
                DeviceInfoBean DeviceInfoBean= new DeviceInfoBean();
                DeviceInfoBean.setDeviceID(i);
                DeviceInfoBean.setDeviceName("设备"+i);
                DeviceInfoBean.setChannelCount(4+i);
                DeviceInfoBean.setChannelWeight(1);
                DeviceInfoBean.setChannelDistance(1);
                DeviceInfoBean.setStepInterval(1);
                DeviceInfoBean.setStepDistance(0.942);
                DeviceInfoBean.setReleaseDate("20170905");
                DeviceInfoBean.setSaleDate("20170906");
                DeviceInfoBean.setHardwareVersion("1.0");
                DeviceInfoBean.setSoftwareVersion("1.0");
                DeviceInfoBean.setDeviceWidth(8.00);
                DeviceInfoBean.setDeviceLength(12.00);
                DeviceInfoBean.setDeviceTailDeadZoneLength(1.00);
                DeviceInfoBean.setDeviceHeadDeadZoneLength(1.00);
                DeviceInfoBean.setDeviceWeight(50.0);
                DeviceInfoBean.setServicePhone("430068");
                DeviceInfoBean.setNote("");
                DeviceInfoBean.save();
            }

        }
    }

    /*****************************************************************
     * 方法名  :  delete
     * 输入参数：systemID:需要删除的DeviceInfoBean对象的ID
     * 输出参数：无
     * 功能描述:  删除系统信息
     ******************************************************************/
    public void delete(int deviceID){
        DataSupport.deleteAll(DeviceInfoBean.class, "id == ?", String.valueOf(deviceID));
    }

    /*****************************************************************
     * 方法名  :  update
     * 输入参数：DeviceInfoBean 对象
     * 输出参数：无
     * 功能描述:   更新系统信息
     ******************************************************************/
    public void update(DeviceInfoBean DeviceInfoBean,long systemID){
        DeviceInfoBean.update(systemID);
    }

    /*****************************************************************
     * 方法名  :  query
     * 输入参数：systemID:需要查询的DeviceInfoBean记录的ID
     * 输出参数：DeviceInfoBean对象
     * 功能描述:  查询系统信息
     ******************************************************************/
    public DeviceInfoBean query(long systemID){
        //如果需要查询的数据量很大，建议使用事务
        List<DeviceInfoBean> DeviceInfoBeans = DataSupport
                .where("deviceID == ?", String.valueOf(systemID))
                .find(DeviceInfoBean.class);
        return DeviceInfoBeans.get(0);
    }

    /*****************************************************************
     * 方法名  :  query
     * 输出参数：DeviceInfoBean对象集合
     * 功能描述:  查询系统信息
     ******************************************************************/
    public List<DeviceInfoBean> queryAll(){
        //如果需要查询的数据量很大，建议使用事务
        return DataSupport.findAll(DeviceInfoBean.class);
    }
}
