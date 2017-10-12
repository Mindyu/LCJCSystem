package com.tomatoLCJC.thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tomatoLCJC.main.utils.WaveletProcess;
import com.tomatoLCJC.tools.Parameter.SystemParameter;
import com.tomatoLCJC.tools.Parameter.ThreadParameter;
import com.tomatoLCJC.tools.dataBase.Bean.HistoryDataRecordBean;
import com.tomatoLCJC.tools.dataBase.Dao.HistoryDataRecordDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zch22 on 2017/5/1.
 */
//功能：点击停止按钮后或中途意外拔出设备的处理线程
public class HandleStopThread extends Thread {
    Handler handler;
    public HandleStopThread(Handler refreshHistoryData){
        this.handler=refreshHistoryData;
    }
    @Override
    public void run(){
        ThreadParameter threadParameter = ThreadParameter.getInstance();
        if( threadParameter.xList.size()>0) {
            //将数据保存到数据库
            HistoryDataRecordBean dataRecordBean = new HistoryDataRecordBean();
            Date date = new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSS");
            String title = sdf.format(date);
            dataRecordBean.setTitle(title);//设置标题(过滤时通过标题)
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
            String detectionTime = sdf1.format(date);
            dataRecordBean.setDetectionTime(detectionTime);//设置检测时间
            dataRecordBean.setChannelCount(SystemParameter.getInstance().nChannelNumber);//设置通道数
            dataRecordBean.setChannelWeight(SystemParameter.getInstance().channelWeight);//设置通道分量
            dataRecordBean.setChannelDistance(SystemParameter.getInstance().nChannelDistance);//设置通道间距
            dataRecordBean.setStepDistance(SystemParameter.getInstance().disSensorStepLen);//设置步长
            dataRecordBean.setStepInterval(SystemParameter.getInstance().nStepInterval);//设置横向梯度步数间隔
            //缺少最大缺陷值最大缺陷值
            if (ThreadParameter.getInstance().denoisingValue.get(0).size()!=ThreadParameter.getInstance().detectionValue.get(0).size()){
                ThreadParameter.getInstance().denoisingValue = WaveletProcess.DenoisingCheckData(ThreadParameter.getInstance().detectionValue.get(0).size(),SystemParameter.getInstance().nChannelNumber,ThreadParameter.getInstance().detectionValue,1);
            }
            //保存通道记录信息和数据, 数据记录、xList、yList、检测值、去噪值、缺陷值
            HistoryDataRecordDao.getInstance().insert(dataRecordBean, threadParameter.xList,threadParameter.yList,
                    threadParameter.detectionValue,threadParameter.denoisingValue,threadParameter.flawValue);
            ThreadParameter.getInstance().clearXAndYList();
            //获取刷新历史数据listview的时间
            String date_num = sdf.format(date);
            String year = date_num.substring(0,4);
            String month = date_num.substring(4,6);
            String day = date_num.substring(6,8);
            Message msg=new Message();
            Bundle bundle=new Bundle();
            bundle.putString("year",year);
            bundle.putString("month",month);
            bundle.putString("day",day);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

    }
}
