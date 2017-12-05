package com.tomatoLCJC.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tomatoLCJC.main.utils.NumberHelper;
import com.tomatoLCJC.tools.Parameter.SystemParameter;
import com.tomatoLCJC.tools.Parameter.ThreadParameter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据处理线程：
 * 从缓冲队列中取出原始数据进行处理并将处理后的数据保存
 */
public class DataProcessThread implements Runnable {
    private ThreadParameter threadParameter;        //线程参数对象
    private SystemParameter systemParameter;        //系统参数对象
    private Handler detectionHandler;               //地磁场校准的Handler
    private int shiftStatus;                        //移动状态，0-静止，1-开始前进，2-开始后退，3-前进结束，4-后退结束
    private double preVoltShiftData;                //第1个位移传感器的上一个值，用于判断位移状态
    List<List<Double>> nTempList;                   //暂时存储一步之内多个通道的多个数据
    private int count;                              //计数器
    private int stepCount;                          //步数
    private Handler stopHandler;
//    private boolean isStop = false;
    private Thread hasData;
    private int size = 0;
    private int ADVANCEFLAG = 3;

    //是否有位移数据
    class MyReprintThread implements Runnable {
        @Override
        public void run() {
            int flag;
            while (!ReadDataThread.isPause) {          //如果是暂停则等待1ms进入下一次循环
                if (ThreadParameter.getInstance().xList.size() != size) {   //长度不等，有位移数据
                    size = ThreadParameter.getInstance().xList.size();
                    Message message = new Message();
                    message.what = 0;
                    if (stopHandler != null) {
                        stopHandler.sendMessage(message);                   //发送消息重绘进度条
                    } else {
                        detectionHandler.sendMessage(message);
                    }
                    try {                       //等待33ms
                        Thread.sleep(33);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {                        //330ms未获取到数据
                    flag = 10;
                    while (flag > 0) {          //当获取到数据时跳出循环。
                        if (ThreadParameter.getInstance().xList.size() != size) break;
                        try {
                            Thread.sleep(33);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        flag--;
                    }
                    if (flag == 0 && !ReadDataThread.isPause) {     //330ms未获取到数据
                        Message message = new Message();
                        message.what = 1;
                        if (stopHandler != null) {
                            stopHandler.sendMessage(message);       //发送消息结束重绘曲线图
                        } else {
                            detectionHandler.sendMessage(message);
                        }
                    }
                }
            }
            hasData=null;
        }
    }

    //设备校准的线程初始化方式
    public DataProcessThread(Handler handler, int flag) {               //flag值为1时校准
        threadParameter = ThreadParameter.getInstance();
        systemParameter = SystemParameter.getInstance();
        if (flag == 1) {
            this.detectionHandler = handler;
        } else {
            stopHandler = handler;
        }
        shiftStatus = 0;
        preVoltShiftData = 0;
        count = 0;
        stepCount = 0;
//        isStop = false;
        nTempList = new ArrayList<>();
        for (int i = 0; i < systemParameter.nChannelNumber; i++) {      //对tempList进行初始化
            nTempList.add(new ArrayList<Double>());
        }
    }

    /*每次有多组数据，对于每一组数据，其开始两个通道的数据用来判断设备是前进还是后退，判断出来后，为shiftStatus赋相应值
     *比如如果 shiftStatus 一直等于1就说明设备一直处在前进过程中，当shiftStatus突变为3的时候说明前进结束，
     *在这个过程中每个通道会有多个数据，而我们要对每个通道的多个数据取均值，方法为（先排序，取中间值）然后将每个通道对应的均值数据存入缓冲池
     *shiftStatus由1变为3的过程称为一步，横坐标是在上一条数据的横坐标上加上步长*/
    @Override
    public void run() {
        while (threadParameter.threadFlag) {
            if (!ReadDataThread.isPause) {                             //如果是暂停则等待1ms进入下一次循环
                int index = threadParameter.readDataIndex;             //当前从缓冲队列中取出数据对应的下标值
                //FileHelper.writeString(String.valueOf(index)+" ");
                int size = systemParameter.nChannelNumber + 2;
                if (!threadParameter.bNewSegmentData[(index + 2 * size) % threadParameter.MAX_SEGMENT]) {       //如果当前size组数据不是最新的
                    continue;
                }
                int[] data = new int[size];                             //从缓冲队列中取出原始数据
                for (int i = 0; i < size; i++) {
                    data[i] = NumberHelper.bytesToInt(threadParameter.ADBuffer, index);
                    threadParameter.bNewSegmentData[index++] = false;   //数据取出之后将标志复原
                    threadParameter.bNewSegmentData[index++] = false;   //数据取出之后将标志复原
                    index %= threadParameter.MAX_SEGMENT;
                }
                threadParameter.readDataIndex = index;
                if (data == null || data.length != size) {              //再次判断防止data为null
                    continue;
                }
                if (threadParameter.ifHaveEncoder) {
                    dataAnalyzeWithEncoder(data);                       //有位移的处理方法
                }
            }
        }
    }

    DecimalFormat df = new DecimalFormat("#0.0000");

    //有位移的处理方法
    private void dataAnalyzeWithEncoder(int[] aGroupChannelData) {
        double firstSensorValue = (aGroupChannelData[0] * 5000.0 / 32768);      //第一个位移传感器的值     *(5000.0 / 32768)
        double secondSensorValue = (aGroupChannelData[1] * 5000.0 / 32768);     //第二个位移传感器的值
        //FileHelper.writeString(String.valueOf(firstSensorValue)+" "+String.valueOf(secondSensorValue)+"\n");
        if (preVoltShiftData > threadParameter.nMaxThreshold && firstSensorValue < threadParameter.nMinThreshold && secondSensorValue > threadParameter.nMaxThreshold) {
            shiftStatus = 1;    // 开始前进(第1个传感器从高电平变为低电平，而此时第2个传感器处于高电平)
        } else if (preVoltShiftData > threadParameter.nMaxThreshold && firstSensorValue < threadParameter.nMinThreshold && secondSensorValue < threadParameter.nMinThreshold) {
            shiftStatus = 2;    // 开始后退(第1个传感器从高电平变为低电平，而此时第2个传感器处于低电平)
        } else if (preVoltShiftData < threadParameter.nMinThreshold && firstSensorValue > threadParameter.nMaxThreshold && secondSensorValue < threadParameter.nMinThreshold && shiftStatus == 1) {
            shiftStatus = 3;    // 前进结束(//第1个传感器从低电平变为高电平，而此时第2个传感器处于低电平)
        } else if (preVoltShiftData < threadParameter.nMinThreshold && firstSensorValue > threadParameter.nMaxThreshold && secondSensorValue > threadParameter.nMaxThreshold && shiftStatus == 2) {
            shiftStatus = 4;    // 后退结束(第1个传感器从低电平变为高电平，而此时第2个传感器处于高电平)
        }
        preVoltShiftData = firstSensorValue;                                     //保留第1个位移传感器的值
        //FileHelper.writeString(" ShiftStatus "+String.valueOf(shiftStatus)+" \n");
        if (shiftStatus != 0) { // 如果不是静止状态
            if (count < 100 && (shiftStatus == 1 || shiftStatus == 2)) {         //最多取100数据做平均
                for (int i = 0; i < systemParameter.nChannelNumber; i++) {
                    nTempList.get(i).add(Double.valueOf(aGroupChannelData[i + 2]));
                }
                count++;
            } else if (shiftStatus == 3 || shiftStatus == 4) {
                if (shiftStatus == 3 && stepCount == 0) {                        //根据第一步的方向判断正方向
                    ADVANCEFLAG = 3;
                } else if (shiftStatus == 4 && stepCount == 0) {
                    ADVANCEFLAG = 4;
                }
                if (shiftStatus == ADVANCEFLAG) {                                //移动一格，显示一个数据，曲线向前走一格
                    for (int i = 0; i < systemParameter.nChannelNumber; i++) {
                        List<Double> tempList = nTempList.get(i);                //第i个通道的处于前进或后退过程中的多个数据（最多取100个）
                        Double[] array = tempList.toArray(new Double[tempList.size()]);
                        if (array.length > 0) {
                            Arrays.sort(array);                         //升序排序
                            double x = array[tempList.size() / 2];      //取中间数
                            threadParameter.detectionValue.get(i).add(Double.valueOf(df.format(x * 5000.0 / 32768))); //保存第i个通道的纵坐标
                        }
                        if (detectionHandler == null) {
                            int size = threadParameter.detectionValue.get(i).size();
                            if (size == 1) {
                                threadParameter.gradientValue.get(i).add(0.0);
                            } else {
                                if (size <= systemParameter.nStepInterval) {      //nStepInterval横向梯度间隔
                                    double y = Math.abs(threadParameter.detectionValue.get(i).get(size - 1) - threadParameter.detectionValue.get(i).get(size - 2));
                                    threadParameter.gradientValue.get(i).add(Double.valueOf(df.format(y / systemParameter.disSensorStepLen / systemParameter.nStepInterval)));
                                } else {
                                    double y = Math.abs(threadParameter.detectionValue.get(i).get(size - 1) - threadParameter.detectionValue.get(i).get(size - 1 - systemParameter.nStepInterval));
                                    threadParameter.gradientValue.get(i).add(Double.valueOf(df.format(y / systemParameter.disSensorStepLen / systemParameter.nStepInterval)));
                                }
                            }
                            threadParameter.yList.get(i).add(Double.valueOf((i + 1) * systemParameter.nChannelDistance));
                        }
                        nTempList.get(i).clear();               //清空此通道相应的临时数据缓存
                    }
                    stepCount++;            //步数加1
                    threadParameter.xList.add(stepCount * SystemParameter.getInstance().disSensorStepLen / 1000);
                    count = 0;
                } else {                    //后退一格，擦除一个数据，曲线向后走一格
                    int size = threadParameter.xList.size();
                    if (size > 0) {
                        for (int i = 0; i < systemParameter.nChannelNumber; i++) {
                            threadParameter.detectionValue.get(i).remove(size - 1);
                            if (detectionHandler == null) {
                                threadParameter.yList.get(i).remove(size - 1);
                                threadParameter.gradientValue.get(i).remove(size - 1);
                            }
                            nTempList.get(i).clear();               //清空此通道相应的临时数据缓存
                        }
                        threadParameter.xList.remove(size - 1);
                        stepCount--;                    //步数减1
                    }
                    count = 0;
                }
                if (hasData == null&&!ReadDataThread.isPause) {
                    hasData = new Thread(new MyReprintThread());
                    hasData.start();
                }
                shiftStatus = 0;                        //将状态置初始值
            }
        }
    }

}
