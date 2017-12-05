package com.tomatoLCJC.tools.Parameter;
import com.tomatoLCJC.tools.dataBase.Dao.DeviceDetectionRecordDao;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 线程参数类，用于存放一些公共的数据,一些系统的参数也放在这里面
 */
public class ThreadParameter {
    //定义常量
    public final int MAX_SEGMENT = 32768;                               // 缓冲队列的长度     原为128
    public final int MAX_SEGMENT_SIZE = 32768;                          // 每次从设备读取数据的最大字符数
    public final int nMinThreshold=2000;                                // 位移传感器下阈值
    public final int nMaxThreshold=2000;                                // 位移传感器上阈值
    //定义一个单例
    private static ThreadParameter instance=null;                       // 线程参数引用
    //公共的数据
    public boolean threadFlag;                                          // 线程的开关
    public boolean ifHaveEncoder;                                       //是否有位移，决定数据的处理方式
    public int readDataIndex;                                           // 读取到缓冲数据的位置
    public byte[] ADBuffer= new byte[MAX_SEGMENT];                      // 缓冲队列，存储未处理数据    MAX_SEGMENT为128
    public boolean[] bNewSegmentData = new boolean[MAX_SEGMENT];        // 用于确定当前段数据是否为最新数据
    public List<Double> xList;                                          // 存储横坐标（位移）
    public List<ArrayList<Double>> yList;                               // 扫描图中y的值
    public List<ArrayList<Double>> detectionValue;                      // 存储磁场强度，原始曲线纵坐标
    public List<ArrayList<Double>> gradientValue;                       // 存储横向梯度曲线纵坐标
    public List<ArrayList<Double>> denoisingValue;                      // 存储原始数据的去噪数据
    public List<ArrayList<Double>> flawValue;                           //存储扫描图的曲线值
    public int shiftSatuts;                                             // 存储状态变量的 3-前进结束，4-后退结束
    public int nReadSizeWords;                                          // 每次读取字节数

    //构造函数私有化
    private ThreadParameter(){
        xList=new ArrayList<>();
        yList=new ArrayList<>();
        detectionValue=new ArrayList<>();
        gradientValue=new ArrayList<>();
        denoisingValue = new ArrayList<>();
        flawValue=new ArrayList<>();
    }

    //获取这个类的单例并初始化参数
    public static ThreadParameter getInstance() {
        if (instance == null) {
            instance = new ThreadParameter();                           // 创建线程参数对象
        }
        return instance;
    }
    /*****************************************************************
     * 方法名  :  initThreadParameter
     * 功能描述: 初始化线程参数
     ******************************************************************/
    public void initThreadParameter() {
        for(int i=0; i<MAX_SEGMENT; i++){                               // 将缓冲队列标志复位
            bNewSegmentData[i] = false;
        }
        for(int i=0;i<SystemParameter.getInstance().nChannelNumber;i++){
            yList.add(new ArrayList<Double>());
            detectionValue.add(new ArrayList<Double>());
            gradientValue.add(new ArrayList<Double>());
            denoisingValue.add(new ArrayList<Double>());
            flawValue.add(new ArrayList<Double>());
        }
        ifHaveEncoder=true;                                             //默认有位移
        threadFlag =true;
        readDataIndex = 0;                                              // 读取位置0
        shiftSatuts = 0;
        nReadSizeWords=MAX_SEGMENT_SIZE - MAX_SEGMENT_SIZE % (2 * (SystemParameter.getInstance().nChannelNumber+3));
    }

    /*****************************************************************
     * 方法名  :  initThreadParameter
     * 功能描述: 初始化ReadSizeWords,供标准磁场校准使用
     ******************************************************************/
    public void initReadSizeWords(){
//        nReadSizeWords=MAX_SEGMENT_SIZE - MAX_SEGMENT_SIZE % (5 * (SystemParameter.getInstance().nChannelNumber+2)+1);
        nReadSizeWords=MAX_SEGMENT_SIZE - MAX_SEGMENT_SIZE % (2 * (SystemParameter.getInstance().nChannelNumber+3));
    }

    /*****************************************************************
     * 方法名  :  clearXAndYList
     * 功能描述: 清空横纵坐标集合中的数据
     ******************************************************************/
    public void clearXAndYList(){
        ThreadParameter.getInstance().xList.clear();
        ThreadParameter.getInstance().yList.clear();
        ThreadParameter.getInstance().detectionValue.clear();
        ThreadParameter.getInstance().gradientValue.clear();
        ThreadParameter.getInstance().denoisingValue.clear();
        ThreadParameter.getInstance().flawValue.clear();
    }

}
