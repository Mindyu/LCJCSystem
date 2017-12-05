package com.tomatoLCJC.thread;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.tomatoLCJC.tools.Parameter.SystemParameter;
import com.tomatoLCJC.tools.Parameter.ThreadParameter;
import com.tomatoLCJC.usbutil.SerialPortOpe;

/**
 * 将原始数据读取到缓冲队列中
 */
public class ReadDataThread implements Runnable {
    ThreadParameter threadParameter;
    SerialPortOpe serialPortOpe;
    final static int CURRENT_MAX_CHANEEL = 9;                           //当前设备最大通道数量：1+2+6 （1：标志位， 2：唯一判断位， 6：有效通道数量）
    public static boolean isPause = false;                              //是否暂停

    public ReadDataThread(Context parentContext) {
        threadParameter = ThreadParameter.getInstance();                //获取线程参数类的对象
        serialPortOpe = SerialPortOpe.getInstance(parentContext);       //获取设备操作类的对象
    }

    @Override
    public void run() {
        int index = 0;
        int size = SystemParameter.getInstance().nChannelNumber + 2;    //通道数加位移判断标记
        while (threadParameter.threadFlag) {
            if (!isPause) {      //如果未暂停
                byte[] data = serialPortOpe.getMeasureData();           //读取长度最大为ThreadParameter.getInstance().nReadSizeWords的一个byte[]
                if (data == null || data.length == 0) {
                    continue;
                }
                for (int i = 0; i < data.length - CURRENT_MAX_CHANEEL * 2 - 1; i++) {
                    while (threadParameter.bNewSegmentData[(index + 2 * size) % threadParameter.MAX_SEGMENT]) {  //如果下一段已存并且未被处理
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (data[i] == 0x08 && data[i + 1] == 0x5A && data[i + CURRENT_MAX_CHANEEL * 2] == 0x08 && data[i + CURRENT_MAX_CHANEEL * 2 + 1] == 0x5A) {
                        for (int j = 1; j <= size; j++) {
                            threadParameter.ADBuffer[index] = data[i + j * 2];
                            threadParameter.bNewSegmentData[index++] = true;           //对应的标志改为true
                            threadParameter.ADBuffer[index] = data[i + j * 2 + 1];
                            threadParameter.bNewSegmentData[index++] = true;           //对应的标志改为true
                            index %= threadParameter.MAX_SEGMENT;
                        }
                        i += (CURRENT_MAX_CHANEEL * 2 - 1);     //如果满足 i跳到下一个标记   需要减1，循环里面有i++
                    }
                }
            }
        }
    }
}
