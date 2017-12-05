package com.tomatoLCJC.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tomatoLCJC.main.utils.CalibrationDialog;
import com.tomatoLCJC.main.utils.InstrumentCalibration;
import com.tomatoLCJC.main.utils.Point;
import com.tomatoLCJC.main.utils.WaveletProcess;
import com.tomatoLCJC.thread.DataProcessThread;
import com.tomatoLCJC.thread.ReadDataThread;
import com.tomatoLCJC.tools.Parameter.SystemParameter;
import com.tomatoLCJC.tools.Parameter.ThreadParameter;
import com.tomatoLCJC.tools.Chart.LineChart;
import com.tomatoLCJC.tools.dataBase.Bean.DeviceDetectionRecordBean;
import com.tomatoLCJC.tools.dataBase.Dao.DeviceInfoDao;
import com.tomatoLCJC.usbutil.SerialPortOpe;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * 地磁场校准页面
 */
public class DeviceCalibrationActivity extends Activity {
    private SerialPortOpe serialPortOpe=null;       //串口操作工具类

    private LineChart lineChart;
    private LinearLayout chartLayout;

    private ImageView back;
    private Button btnStart;
    private Button btnImport;
    private Button btnQuit;
    private TextView titleText1;
    private TextView titleText2;
    private TextView selectChaneel;
    private TextView selectDevice;
    private DeviceDetectionRecordBean bean;
    private List<ArrayList<Point>> points = new ArrayList<>();
    private ProgressDialog myDialog_1;              //数据采集中的进度条

    class MyThread implements Runnable{
        @Override
        public void run() {
            if (ThreadParameter.getInstance().xList.size()==0){
                return;
            }else{
                List<ArrayList<Double>> maxValue = new ArrayList<>();
                maxValue.clear();
                maxValue.add(new ArrayList<Double>());double tempMax;
                for (int i=0;i<ThreadParameter.getInstance().xList.size();i++){      //取每个通道的最大值，
                    tempMax=ThreadParameter.getInstance().detectionValue.get(0).get(i);
                    for (int j=1;j<SystemParameter.getInstance().nChannelNumber;j++){
                        if (ThreadParameter.getInstance().detectionValue.get(j).get(i)>tempMax){
                            tempMax=ThreadParameter.getInstance().detectionValue.get(j).get(i);
                        }
                    }
                    maxValue.get(0).add(tempMax);
                }                                                                    //计算去噪值
                ThreadParameter.getInstance().denoisingValue = WaveletProcess.DenoisingCheckData(ThreadParameter.getInstance().xList.size(),1,maxValue,1);
            }                                                                        //绘制图线
            //获取校准后有效的四个Point，已排好序  从大到小
            List<Point> tempPoint =InstrumentCalibration.getMaximumValue(ThreadParameter.getInstance().denoisingValue.get(0));
            //逆序
            List<Point> tempPoint1 = InstrumentCalibration.invertedOrder(tempPoint);
            lineChart.drawCalibrationView(ThreadParameter.getInstance().xList,ThreadParameter.getInstance().denoisingValue, tempPoint1);
            points.add(null);   points.add(null);  //先初始化两个point
            if (tempPoint.size() >= 4){
                if (tempPoint.get(0).getX()>tempPoint.get(1).getX() ){               //正向的扫描  20-40-60-80
                    points.set(0,(ArrayList<Point>) tempPoint);
                    Message msg=new Message();                                       //发送消息完成重绘
                    msg.what=2;
                    handler.sendMessage(msg);
                }else if (tempPoint.get(0).getX()<tempPoint.get(1).getX()){
                    points.set(1,(ArrayList<Point>) tempPoint);
                    Message msg=new Message();                                       //发送消息完成重绘
                    msg.what=2;
                    handler.sendMessage(msg);
                }else {
                    Message msg=new Message();      //校准方向有误，请再次校准！
                    msg.what=3;
                    handler.sendMessage(msg);
                }
            }else{
                //校准有误，请再次校准
                Message msg=new Message();
                msg.what=4;
                handler.sendMessage(msg);
            }
            /* //固定校准方向
            if (tempPoint.size() >= 4){
                if (tempPoint.get(0).getX()>tempPoint.get(1).getX() && points.size()==0){    //正向的扫描
                    points.add((ArrayList<Point>) tempPoint);
                    Message msg=new Message();                                               //发送消息完成重绘
                    msg.what=2;
                    handler.sendMessage(msg);
                }else if (tempPoint.get(0).getX()<tempPoint.get(1).getX() && points.size()==1){
                    points.add((ArrayList<Point>) tempPoint);
                    Message msg=new Message();                                               //发送消息完成重绘
                    msg.what=2;
                    handler.sendMessage(msg);
                }else {
                    Message msg=new Message();                  //校准方向有误，请再次校准！
                    msg.what=3;
                    handler.sendMessage(msg);
                }
            }else{
                //校准有误，请再次校准
                Message msg=new Message();
                msg.what=4;
                handler.sendMessage(msg);
            }*/
            ThreadParameter.getInstance().clearXAndYList();         //清除数据，以便下一次校准
        }
    }

     //更新图表
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                myDialog_1.setProgress(0);
                myDialog_1.dismiss();
                //数据完成获取
                serialPortOpe.stopReciveData();                       //停止接收数据，关闭设备
                ThreadParameter.getInstance().threadFlag = false;     //停止线程
                //绘制图线线程
                new Thread(new MyThread()).start();
            }else if (msg.what==0){
                myDialog_1.setProgress(ThreadParameter.getInstance().xList.size()%100);
            }else if(msg.what==2){
                lineChart.invalidate();
                if (points.get(0)!=null){
                    bean = new DeviceDetectionRecordBean();
                    bean.setDefectPercent1(20);
                    bean.setDefectPercent1_x(points.get(0).get(3).getX());
                    bean.setDefectPercent1(40);
                    bean.setDefectPercent2_x(points.get(0).get(2).getX());
                    bean.setDefectPercent1(60);
                    bean.setDefectPercent3_x(points.get(0).get(1).getX());
                    bean.setDefectPercent1(80);
                    bean.setDefectPercent4_x(points.get(0).get(0).getX());
                    //完成第一次正向扫描，接下来进行第二次的反向扫描
                    titleText1.setVisibility(View.GONE);
                    titleText2.setVisibility(View.VISIBLE);
//                    deviceSpinner.setEnabled(false);
                    selectChaneel.setEnabled(false);
                    if (points.get(1)!=null){
                        //完成两种方向的扫描，弹框显示基础数据，以完成校准
                        Toast.makeText(DeviceCalibrationActivity.this,"完成两种方向的扫描",Toast.LENGTH_SHORT).show();
                        //设置校准信息（初始化）
                        setDeviceDetectionRecordBean();
                        //显示弹出框校准信息的修改及确认，确认之后直接保存
                        CalibrationDialog dialog = new CalibrationDialog(DeviceCalibrationActivity.this,bean);
                        dialog.show();
                    }
                }
            }else if (msg.what==3){
                Toast.makeText(DeviceCalibrationActivity.this,"校准方向有误，请再次校准！",Toast.LENGTH_SHORT).show();
            }else if (msg.what==4){
                //校准有误，请再次校准
                Toast.makeText(DeviceCalibrationActivity.this,"校准有误，请再次校准！",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //锁定竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.device_calibration);
        initView();
        initEvent();
    }

    public void initView(){
        back=(ImageView)findViewById(R.id.return_key);
        btnStart=(Button)findViewById(R.id.btnStart);
        btnImport=(Button)findViewById(R.id.btnImport);
        btnQuit=(Button)findViewById(R.id.btnQuit);
        titleText1= (TextView) findViewById(R.id.title_text_1);
        titleText2= (TextView) findViewById(R.id.title_text_2);
        selectChaneel=(TextView)findViewById(R.id.selectChaneel);
        selectDevice= (TextView) findViewById(R.id.deviceText);
        selectChaneel.setText(""+SystemParameter.getInstance().nChannelNumber);    //设置设备的通道数
        String deviceName = DeviceInfoDao.getInstance().query(SystemParameter.getInstance().deviceID).getDeviceName();
        selectDevice.setText(deviceName);
        chartLayout=(LinearLayout)findViewById(R.id.chart);
        serialPortOpe=SerialPortOpe.getInstance(getApplicationContext());
        lineChart = new LineChart(getApplicationContext());
        chartLayout.removeAllViews();
        chartLayout.addView(lineChart, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void initEvent(){
        //开始校准
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果初始化并打开设备成功，则返回true,否则返回false
                serialPortOpe.createDeviceList();
                boolean flag = serialPortOpe.initDevice();
                if (flag) {
                    if (myDialog_1 == null) {
                        myDialog_1 = new ProgressDialog(DeviceCalibrationActivity.this);
                        myDialog_1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                        myDialog_1.setTitle("请稍后");
                        myDialog_1.setMessage("\n  数据采集中...");
                        myDialog_1.setMax(100);
                        myDialog_1.setCancelable(false);
                        myDialog_1.setProgressNumberFormat(null);      //不显示进度条右侧的百分比    12/100
                        myDialog_1.setProgressPercentFormat(null);     //不显示进度条左侧的百分比    12%
                    }
                    myDialog_1.setProgress(0);
                    if (!(DeviceCalibrationActivity.this).isFinishing()){
                        myDialog_1.show();
                    }

                    lineChart.cleanChart();
                    chartLayout.removeAllViews();
                    chartLayout.addView(lineChart, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    /**********线程参数得清空（当数据保存后）***********/
                    ThreadParameter.getInstance().clearXAndYList();
                    ThreadParameter.getInstance().initThreadParameter();    //初始化参数
                    ThreadParameter.getInstance().ifHaveEncoder = true;     //以有位移的方式处理数据
                    ThreadParameter.getInstance().threadFlag = true;        //打开线程开关
                    ReadDataThread readDataThread = new ReadDataThread(getApplicationContext());
                    ReadDataThread.isPause = false;
                    new Thread(readDataThread).start();                     //启动读数据线程
                    DataProcessThread dataProcessThread = new DataProcessThread(handler, 1);
                    new Thread(dataProcessThread).start();                  //启动数据处理线程
                }
            }
        });

        //从历史数据中导入校准记录
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceCalibrationActivity.this,CalibrationDataImportActivity.class);
                startActivity(intent);
            }
        });

        //直接退出
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialPortOpe.stopReciveData();                             //停止接收数据，关闭设备
                ThreadParameter.getInstance().threadFlag=false;             //停止线程
                ThreadParameter.getInstance().clearXAndYList();
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnQuit.callOnClick();
            }
        });

    }

    //设置校准信息（初始化）
    public void setDeviceDetectionRecordBean(){           //points.get(0) 正向扫描   points.get(1)反向扫描
        double value = (points.get(0).get(3).getY()+points.get(1).get(3).getY())/2;
        bean.setDefectPercent1_value(value);
        value = (points.get(0).get(2).getY()+points.get(1).get(2).getY())/2;
        bean.setDefectPercent2_value(value);
        value = (points.get(0).get(1).getY()+points.get(1).get(1).getY())/2;
        bean.setDefectPercent3_value(value);
        value = (points.get(0).get(0).getY()+points.get(1).get(0).getY())/2;
        bean.setDefectPercent4_value(value);
        bean.setSteelThickness(8);          //厚度
        bean.setUpliftValue(3);             //提离值
        bean.setSteelTexture("");           //材质
    }
}
