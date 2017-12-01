package com.tomatoLCJC.usbutil;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.tomatoLCJC.tools.Parameter.ThreadParameter;

/**
 * 设备操作类
 */
public class SerialPortOpe {

    private D2xxManager ftdid2xx=null;
    private FT_Device ftDev = null;
    private Context mContext=null;

    //辅助变量
    private int DevCount = -1;//存储设备数量
    private int currentIndex = -1;//通过判断currentIndex和openIndex是否相等来判断串口是否打开
    private int openIndex = 0;    //打开设备的下标
    private boolean ifConfigured=false;//是否配置的标记

    private String TAG="SerialPortOpe";

    //单例模式
    private volatile static SerialPortOpe serialPortOpe=null;
    //私有构造函数（初始化ftdid2xx并更新设备数量）
    private SerialPortOpe(Context parentContext){
        mContext=parentContext;
        try {
            if(ftdid2xx==null)
                ftdid2xx = D2xxManager.getInstance(mContext);
        } catch (D2xxManager.D2xxException e) {
            e.printStackTrace();
        }
        //如果设置的VIDPID失败
        if(!ftdid2xx.setVIDPID(0x0403, 0xada1)){
            Toast.makeText(mContext, "设备未连接", Toast.LENGTH_SHORT).show();
//            Log.d(TAG,"setVIDPID Error");
        }
        //初始化设备数量
        DevCount=getDeviceCount();
    }

    //获取本类的实例并初始化
    public static SerialPortOpe getInstance(Context parentContext ){
        if(serialPortOpe==null){           //双重校验锁，单例模式
            synchronized (SerialPortOpe.class){
                if(serialPortOpe==null){
                    serialPortOpe=new SerialPortOpe(parentContext);
                }
            }
        }
        return  serialPortOpe;
    }
    //返回连接设备的数量，没有则返回0
    public int getDeviceCount(){
        if(ftdid2xx!=null){
            int devcount=ftdid2xx.createDeviceInfoList(mContext);
            if (devcount==0){
                Toast.makeText(mContext, "设备未连接", Toast.LENGTH_SHORT).show();
            }else if (devcount>0){
                Toast.makeText(mContext, "已连接设备数：" + devcount, Toast.LENGTH_SHORT).show();
            }
            return devcount;
        }
        return 0;
    }

    //获得设备数量，每次调用都可以更新设备数，一般只有一个设备DevCount=1
    public void createDeviceList() {
        if(ftdid2xx!=null) {
            int tempDevCount = ftdid2xx.createDeviceInfoList(mContext);
            if (tempDevCount==0){
                Toast.makeText(mContext, "设备未连接", Toast.LENGTH_LONG).show();
            }
            if (tempDevCount > 0) {
                DevCount = tempDevCount;
            } else {
                DevCount = -1;
                currentIndex = -1;
            }
        }else{
            Toast.makeText(mContext, "设备未连接", Toast.LENGTH_LONG).show();//ftd2xx is null
        }
    }

    /*连接并配置设备参数
    *  打开设备成功返回true否则返回false
    */
    public boolean initDevice() {
        if (DevCount <= 0) {
            DevCount=getDeviceCount();//更新设备数
            createDeviceList();
            return false;
        }else {
            int tmpProtNumber = openIndex + 1;
            if (currentIndex != openIndex) {
                if (ftDev == null) {
                    ftDev = ftdid2xx.openByIndex(mContext, openIndex);
                } else {
                    synchronized (ftDev) {
                        ftDev = ftdid2xx.openByIndex(mContext, openIndex);
                    }
                }
                ifConfigured = false;
            } else {
//                Toast.makeText(mContext, "Device port " + tmpProtNumber + " is already opened", Toast.LENGTH_LONG).show();
                return true;
            }
            if (ftDev == null) {
//                Toast.makeText(mContext, "open device port(" + tmpProtNumber + ") NG, ftDev == null", Toast.LENGTH_LONG).show();
                return false;
            }
            if (ftDev.isOpen()) {
                currentIndex = openIndex;
                synchronized(ftDev) {
                    ftDev.resetDevice(); // 清空设备缓存中的数据
                }
                if (ifConfigured == false) {
                    DeviceInfo deviceInfo = DeviceInfo.getDeviceInfoInstance(); //获取系统参数对象
                    setConfig(deviceInfo.BAUDRATE, deviceInfo.DATABIT, deviceInfo.STOPBIT, deviceInfo.PARITY, deviceInfo.FLOWCONTROL);//为设备设置设备参数
                    ifConfigured = true;
                }
                return true;
            } else {
//                Toast.makeText(mContext, "device port(" + tmpProtNumber + ") not open", Toast.LENGTH_LONG).show();
                return false;
            }
        }
    }


    /*设置设备参数*/
    private void setConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        if (ftDev.isOpen() == false) {
//            Log.e(TAG, "SetConfig: device not open");
            Toast.makeText(mContext, "设备未连接", Toast.LENGTH_LONG).show();
            return;
        }
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);
        ftDev.setBaudRate(baud);
        switch (dataBits) {
            case 7:
                dataBits = D2xxManager.FT_DATA_BITS_7;
                break;
            case 8:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
            default:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
        }
        switch (stopBits) {
            case 1:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
            case 2:
                stopBits = D2xxManager.FT_STOP_BITS_2;
                break;
            default:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
        }
        switch (parity) {
            case 0:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
            case 1:
                parity = D2xxManager.FT_PARITY_ODD;
                break;
            case 2:
                parity = D2xxManager.FT_PARITY_EVEN;
                break;
            case 3:
                parity = D2xxManager.FT_PARITY_MARK;
                break;
            case 4:
                parity = D2xxManager.FT_PARITY_SPACE;
                break;
            default:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
        }
        ftDev.setDataCharacteristics(dataBits, stopBits, parity);
        short flowCtrlSetting;
        switch (flowControl) {
            case 0:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
            case 1:
                flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
                break;
            case 2:
                flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
                break;
            case 3:
                flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
                break;
            default:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
        }
        ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);
//        Toast.makeText(mContext, "Config done", Toast.LENGTH_SHORT).show();
    }

    //向设备发送数据
    public void sendMessage(String sendData) {
        if(ftDev!=null) {
            if (ftDev.isOpen() == false) {
//                Log.e(TAG, "SendMessage: device not open");
                Toast.makeText(mContext, "设备未连接", Toast.LENGTH_LONG).show();
                return;
            }
            ftDev.setLatencyTimer((byte) 16);
            String writeData = sendData;
            byte[] OutData = writeData.getBytes();
            try {
                ftDev.write(OutData, writeData.length());
            }catch (Exception e){
            }
        }else{
            Toast.makeText(mContext, "设备未连接", Toast.LENGTH_LONG).show();   //ftDev is null ,please connect device
        }
    }

    /*获得返回数据,返回一个byte数组
     */
    public byte[] getMeasureData( ){
        byte[] readData = null;
        int readLength=ThreadParameter.getInstance().nReadSizeWords;
        if(ftDev!=null) {
            synchronized (ftDev) {
                if (ftDev.isOpen()) {
                    int byteCount = ftDev.getQueueStatus();
                    if (byteCount > 0) {
                        if (byteCount > readLength){       //byteCount==16320   readLength==32760
                            byteCount = readLength;
                        }
                        readData = new byte[byteCount];
                        ftDev.read(readData, byteCount);
                    }
                }
            }
        }
        return readData;
    }


    //设备变为能读，可以接收来自设备的数据
    public void enableRead() {
        if(ftDev!=null) {
            if(!ftDev.isOpen()) {
                return ;
            }
            ftDev.purge((byte) (D2xxManager.FT_PURGE_TX));
            ftDev.restartInTask();
        }else{
            Toast.makeText(mContext, "设备未连接", Toast.LENGTH_SHORT).show();   //ftDev is null!
        }
    }

    //设备变为不可读，即不能接收从设备中取数据，可用来暂停接收数据
    public void disabledRead() {
        if(ftDev!=null) {
            if(!ftDev.isOpen()) {
                return ;
            }
            //Read Disabled
            ftDev.stopInTask();
        }else{
            Toast.makeText(mContext, "设备未连接", Toast.LENGTH_SHORT).show();
        }
    }

    //停止接收数据,结束接收线程
    public void stopReciveData(){
        sendMessage("t");//停止收集数据
        disconnectFunction();//断开设备
        createDeviceList();//更新设备数
    }


    //断开连接
    private void disconnectFunction() {
        DevCount = -1;
        currentIndex = -1;
        openIndex=0;
        ifConfigured=false;
        if (ftDev != null) {
            synchronized (ftDev) {
                if (ftDev.isOpen()) {
                    ftDev.close();
                }
            }
        }
    }

    public void recoverParameter(){
        DevCount = -1;
        currentIndex = -1;
        openIndex=0;
        ifConfigured=false;
        ftDev=null;
    }

}
