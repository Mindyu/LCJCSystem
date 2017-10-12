package com.tomatoLCJC.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tomatoLCJC.main.utils.CalibrationDialog;
import com.tomatoLCJC.main.utils.DialogUtils;
import com.tomatoLCJC.main.utils.FileHelper;
import com.tomatoLCJC.main.utils.InstrumentCalibration;
import com.tomatoLCJC.main.utils.Point;
import com.tomatoLCJC.main.utils.WaveletProcess;
import com.tomatoLCJC.thread.DataProcessThread;
import com.tomatoLCJC.thread.HandleStopThread;
import com.tomatoLCJC.thread.ReadDataThread;
import com.tomatoLCJC.tools.Parameter.SystemParameter;
import com.tomatoLCJC.tools.Parameter.ThreadParameter;
import com.tomatoLCJC.tools.chart.ChartService;
import com.tomatoLCJC.tools.dataBase.Bean.DeviceDetectionRecordBean;
import com.tomatoLCJC.tools.dataBase.Bean.DeviceInfoBean;
import com.tomatoLCJC.tools.dataBase.Dao.DeviceDetectionRecordDao;
import com.tomatoLCJC.tools.dataBase.Dao.DeviceInfoDao;
import com.tomatoLCJC.usbutil.SerialPortOpe;

import org.achartengine.GraphicalView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * 地磁场校准页面
 */
public class DeviceCalibrationActivity extends Activity {
    private SerialPortOpe serialPortOpe=null;//串口操作工具类

    public ChartService mService = null;   //画图工具类,控制原始曲线的小表及弹出窗口的大表
    private GraphicalView mGraphicalView;  //原始曲线图表
    private LinearLayout chartLayout;

    private ImageView back;
    private Button btnStart;
    private Button btnImport;
    private Button btnQuit;
    private TextView titleText1;
    private TextView titleText2;
    private TextView selectChaneel;
    private Spinner deviceSpinner;

    private List<DeviceInfoBean> deviceInfoBeens;
    private DeviceDetectionRecordBean bean;
    private boolean isStart=false;
    private List<ArrayList<Point>> points = new ArrayList<>();
    private ProgressDialog myDialog;    //数据采集中的进度条


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
            mService.drawDetectionChart(ThreadParameter.getInstance().xList,ThreadParameter.getInstance().denoisingValue);
            //获取校准的多个最大点，已排好序从大到小
            List<Point> tempPoint =InstrumentCalibration.getMaximumValue(ThreadParameter.getInstance().denoisingValue.get(0));
             //尝试打印，验证大小关系
            for (int i=0;i<tempPoint.size();i++){
                Log.d("point: ",tempPoint.get(i).toString());
            }
            if (tempPoint.size() >= 4){
                if (tempPoint.get(0).getX()>tempPoint.get(1).getX() && points.size()==0){   //正向的扫描
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
                    Message msg=new Message();    //校准方向有误，请再次校准！
                    msg.what=3;
                    handler.sendMessage(msg);
                }
            }else{
                //校准有误，请再次校准
                Message msg=new Message();
                msg.what=4;
                handler.sendMessage(msg);
            }
            ThreadParameter.getInstance().clearXAndYList();    //清除数据，以便下一次校准
        }
    }

     //更新图表
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                myDialog.dismiss();
                isStart=false;
                //数据完成获取
                serialPortOpe.stopReciveData();    //停止接收数据，关闭设备
                ThreadParameter.getInstance().threadFlag = false;     //停止线程
                //绘制图线线程
                new Thread(new MyThread()).start();
            }else if (msg.what==0){
                myDialog.setProgress(ThreadParameter.getInstance().xList.size()%100);
            }else if(msg.what==2){
                mService.refreshChart();
                if (points.size()==1){
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
                    deviceSpinner.setEnabled(false);
                    selectChaneel.setEnabled(false);
                }else if (points.size()==2){
                    //完成两种方向的扫描，弹框显示基础数据，以完成校准
                    Toast.makeText(DeviceCalibrationActivity.this,"完成两种方向的扫描",Toast.LENGTH_SHORT).show();
                    //设置校准信息（初始化）
                    setDeviceDetectionRecordBean();
                    //显示弹出框校准信息的修改及确认，确认之后直接保存
                    CalibrationDialog dialog = new CalibrationDialog(DeviceCalibrationActivity.this,bean);
                    dialog.show();
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
        btnStart=(Button)findViewById(R.id.btnStart);
        btnImport=(Button)findViewById(R.id.btnImport);
        btnQuit=(Button)findViewById(R.id.btnQuit);
        titleText1= (TextView) findViewById(R.id.title_text_1);
        titleText2= (TextView) findViewById(R.id.title_text_2);
        selectChaneel=(TextView)findViewById(R.id.selectChaneel);
//        imageView=(ImageView)findViewById(R.id.img_select_2);
        chartLayout=(LinearLayout)findViewById(R.id.chart);
        back=(ImageView)findViewById(R.id.return_key);
        serialPortOpe=SerialPortOpe.getInstance(getApplicationContext());
        mService = new ChartService(DeviceCalibrationActivity.this,1,3);    //SystemParameter.getInstance().nChannelNumber
        mService.setXYMultipleSeriesDataset("");
        mService.setXYMultipleSeriesRenderer();
        mGraphicalView= mService.getGraphicalView();     //设备校准的图表
        chartLayout.removeAllViews();
        chartLayout.addView(mGraphicalView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        deviceInfoBeens= DeviceInfoDao.getInstance().queryAll();
        String [] data = new String[deviceInfoBeens.size()];
        int i=0;
        for (DeviceInfoBean deviceInfoBeen :deviceInfoBeens){
            data[i++]=deviceInfoBeen.getDeviceName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,data);
        //设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceSpinner = (Spinner)findViewById(R.id.deviceSpinner);
        deviceSpinner.setAdapter(adapter);
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
                    if (myDialog == null) {
                        myDialog = new ProgressDialog(DeviceCalibrationActivity.this);
                        myDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                        myDialog.setTitle("请稍后");
                        myDialog.setMessage("\n  数据采集中...");
                        myDialog.setMax(100);
                        myDialog.setProgress(0);
                        myDialog.setCancelable(false);
                        myDialog.setProgressNumberFormat(null);      //不显示进度条右侧的百分比    12/100
                        myDialog.setProgressPercentFormat(null);     //不显示进度条左侧的百分比    12%
                    } else {
                        myDialog.setProgress(0);
                    }
                    myDialog.show();
                    isStart = true;

                    mService.clearDetectionChart();
                    mService = new ChartService(DeviceCalibrationActivity.this, 1, 3);      //Integer.valueOf(selectChaneel.getText().toString().trim())
                    mService.setXYMultipleSeriesDataset("");
                    mService.setXYMultipleSeriesRenderer();
                    mGraphicalView = mService.getGraphicalView();    //地磁场校准的图表
                    chartLayout.removeAllViews();
                    chartLayout.addView(mGraphicalView, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    /**********线程参数得清空（当数据保存后）***********/
                    ThreadParameter.getInstance().clearXAndYList();
                    ThreadParameter.getInstance().initThreadParameter();//初始化参数
                    ThreadParameter.getInstance().ifHaveEncoder = true;//以有位移的方式处理数据
                    ThreadParameter.getInstance().threadFlag = true;   //打开线程开关
                    ReadDataThread readDataThread = new ReadDataThread(getApplicationContext());
                    new Thread(readDataThread).start();            //启动读数据线程
                    DataProcessThread dataProcessThread = new DataProcessThread(handler, mService);
                    new Thread(dataProcessThread).start();         //启动数据处理线程
                }
            }
        });

        //从历史数据中导入校准记录
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnQuit.callOnClick();
            }
        });

        //直接退出
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStart=false;
                if (!mService.isFrist){
                    mService.isFrist=true;
                }
                serialPortOpe.stopReciveData();    //停止接收数据，关闭设备
                ThreadParameter.getInstance().threadFlag=false;     //停止线程
                ThreadParameter.getInstance().clearXAndYList();
                finish();
            }
        });

        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SystemParameter.getInstance().deviceID=deviceInfoBeens.get(position).getDeviceID();
                SystemParameter.getInstance().updateSystemParameter();                          //选择设备之后会接着更新系统信息
                selectChaneel.setText("  "+deviceInfoBeens.get(position).getChannelCount());    //设置设备的通道数
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
