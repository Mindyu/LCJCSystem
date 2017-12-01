package com.tomatoLCJC.main;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tomatoLCJC.tools.Parameter.SystemParameter;
import com.tomatoLCJC.tools.dataBase.Bean.DeviceInfoBean;
import com.tomatoLCJC.tools.dataBase.Dao.DeviceInfoDao;

import java.util.List;

/**
 * Created by zch22 on 2017/4/19.
 * 系统参数的设置界面（只能设置通道数量和通道间距）
 */
public class SystemParameterActivity extends Activity {
    private final int  MAX_CHANNELCOUNT=15; //最大通道数量
    private final int  MIN_CHANNELCOUNT=1;  //最小通道数量
    private final int MAX_CHANNELDISTANCE=100;//最大通道间距
    private final int MIN_CHANNELDISTANCE=1;//最小通道间距
    private final double MAX_CHANNELSTEP=10;//最小通道步长
    private final double MIN_CHANNELSTEP=0.1;//最大通道步长
    private final int MAX_STEPINTERVAL = 100; //最小横向步数间隔
    private final int MIN_STEPINTERVAL = 1;//最大横向步数间隔

    private EditText channelCount;           //通道数量编辑框
    private EditText channelDistance;        //通道间距编辑框
    private EditText stepInterval;           //横向步数间隔编辑框
    private EditText channelStep;           //通道步长
    private TextView finish;                  //完成按钮
    private Spinner deviceSpinner;
    private ImageView back;

    private List<DeviceInfoBean> deviceInfoBeens;
    private int channelCountTemp;           //存储通道数量的中间变量
    private int channelDistanceTemp;       //存储通道间距的中间变量
    private double channelStepTemp;        //存储通道步长的中间变量
    private int stepIntervalTemp;           //存储横向步数间隔的中间变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置通知栏样式
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.system_parameter);
        initView();                         //初始化组件，为EditText设置初始值
        initEvent();                        //为组件添加事件监听器
        initDates();
    }
    //初始化组件
    public void initView(){
        channelCount=(EditText)findViewById(R.id.channelCount);
        channelDistance=(EditText)findViewById(R.id.channelDistance);
        channelStep=(EditText)findViewById(R.id.channelStep) ;
        stepInterval=(EditText)findViewById(R.id.stepInterval);
        finish=(TextView)findViewById(R.id.btn_finish);
        back=(ImageView)findViewById(R.id.return_key);
        deviceInfoBeens= DeviceInfoDao.getInstance().queryAll();
        String [] data = new String[deviceInfoBeens.size()];
        int i=0,currentDevice=0;
        for (DeviceInfoBean deviceInfoBeen :deviceInfoBeens){
            if (SystemParameter.getInstance().deviceID==deviceInfoBeen.getDeviceID()){
                currentDevice=i;
            }
            data[i++]=deviceInfoBeen.getDeviceName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,data);
        //设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceSpinner = (Spinner)findViewById(R.id.deviceSpinner);
        deviceSpinner.setAdapter(adapter);
        deviceSpinner.setSelection(currentDevice);
    }

    public void initDates(){
        channelCountTemp= SystemParameter.getInstance().nChannelNumber;     //从系统参数对象获得通道数，系统参数是从数据库中取的
        channelDistanceTemp=SystemParameter.getInstance().nChannelDistance;//从系统参数对象获得通道间距，系统参数是从数据库中取的
        channelStepTemp=SystemParameter.getInstance().disSensorStepLen;     //从系统参数对象获得通道步长，系统参数是从数据库中取的
        stepIntervalTemp=SystemParameter.getInstance().nStepInterval;        //从系统参数对象获得横向步数间隔，系统参数是从数据库中取的
        channelCount.setText(""+channelCountTemp);                           //设置通道数文本框的内容
        channelDistance.setText(""+channelDistanceTemp);                   //设置通道间距文本框的内容
        channelStep.setText(""+channelStepTemp);                             //设置通道步长文本框的内容
        stepInterval.setText(""+stepIntervalTemp);                           //设置横向步数间隔文本框的内容
//        channelCount.setSelection(channelCount.getText().toString().length());//设置光标位置
    }

    //为组件添加事件
    public void initEvent(){

        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SystemParameter.getInstance().deviceID=deviceInfoBeens.get(position).getDeviceID();
                SystemParameter.getInstance().updateSystemParameter();                          //选择设备之后会接着更新系统信息
                initDates();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //为通道数量文本框添加事件监听器
        channelCount.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count){
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                int markVal = 0;
                s.toString();
                try {
                    markVal =Integer.parseInt(s.toString());
                } catch(NumberFormatException e) {
                    markVal = -1;
                }
                if(markVal > MAX_CHANNELCOUNT) {
                    Toast.makeText(getBaseContext(),"通道数量最大为"+MAX_CHANNELCOUNT, Toast.LENGTH_SHORT).show();
                    channelCount.setText(String.valueOf(MAX_CHANNELCOUNT));
                }
                if(markVal==-1)
                    Toast.makeText(getBaseContext(),"通道数不能小于"+MIN_CHANNELCOUNT, Toast.LENGTH_SHORT).show();
                if(markVal==0)
                    channelCount.setText(String.valueOf(MIN_CHANNELCOUNT));
                channelCount.setSelection(channelCount.getText().toString().length());//设置光标位置在最后
                return;
            }
        });

        //为通道间距文本框添加事件监听器
        channelDistance.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count){
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                int markVal = 0;
                try {
                    markVal =Integer.parseInt(s.toString());
                } catch(NumberFormatException e) {
                    markVal = -1;
                }
                if(markVal > MAX_CHANNELDISTANCE) {
                    Toast.makeText(getBaseContext(),"通道间距过最大为"+MAX_CHANNELDISTANCE, Toast.LENGTH_SHORT).show();
                    channelDistance.setText(String.valueOf(MAX_CHANNELDISTANCE));
                }
                if(markVal==-1)
                    Toast.makeText(getBaseContext(),"通道间距不能小于"+MIN_CHANNELDISTANCE, Toast.LENGTH_SHORT).show();
                if(markVal==0)
                    channelDistance.setText(String.valueOf(MIN_CHANNELDISTANCE));
                channelDistance.setSelection(channelDistance.getText().toString().length());//设置光标位置在最后
                return;
            }
        });

        channelStep.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count){
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                double markVal = 0;
                try {
                    markVal = Double.valueOf(s.toString());
                } catch(NumberFormatException e) {
                    markVal = -1;
                }
                if(markVal > MAX_CHANNELSTEP) {
                    Toast.makeText(getBaseContext(),"通道步长最大为"+MAX_CHANNELSTEP, Toast.LENGTH_SHORT).show();
                    channelStep.setText(String.valueOf(MAX_CHANNELSTEP));
                }
                if(markVal==-1)
                    Toast.makeText(getBaseContext(),"通道步长不能小于"+MIN_CHANNELSTEP, Toast.LENGTH_SHORT).show();
                if(markVal==0)
                    channelStep.setText(String.valueOf(MIN_CHANNELSTEP));
                channelStep.setSelection(channelStep.getText().toString().length());//设置光标位置在最后
                return;
            }
        });

        stepInterval.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count){
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                int markVal = 0;
                try {
                    markVal =Integer.parseInt(s.toString());
                } catch(NumberFormatException e) {
                    markVal = -1;
                }
                if(markVal > MAX_STEPINTERVAL) {
                    Toast.makeText(getBaseContext(),"横向间隔最大为"+MAX_STEPINTERVAL, Toast.LENGTH_SHORT).show();
                    stepInterval.setText(String.valueOf(MAX_STEPINTERVAL));
                }
                if(markVal==-1)
                    Toast.makeText(getBaseContext(),"横向间隔不能小于"+MIN_STEPINTERVAL, Toast.LENGTH_SHORT).show();
                if(markVal==0)
                    stepInterval.setText(String.valueOf(MIN_STEPINTERVAL));
                stepInterval.setSelection(stepInterval.getText().toString().length());//设置光标位置在最后
                return;
            }
        });

        //为完成按钮添加点击事件
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String channelNum=channelCount.getText().toString();                 //获取输入的通道数量
                String channelDis=channelDistance.getText().toString();             //获取输入的通道间距
                String channelstep=channelStep.getText().toString();                //获取输入的通道步长
                String stepInter = stepInterval.getText().toString();               //获取输入的步数间隔
                if(!"".equals(channelNum)&&!"".equals(channelDis)&&!"".equals(channelstep)&&!"".equals(stepInter)){    //如果输入框不空
                    int channel_num=Integer.parseInt(channelNum);
                    int channel_Dis=Integer.parseInt(channelDis);
                    double channel_Step=Double.parseDouble(channelstep);
                    int step_Interval =Integer.parseInt(stepInter);
                    SystemParameter.getInstance().nChannelNumber=channel_num;      //更新系统参数对象的通道数量
                    SystemParameter.getInstance().nChannelDistance=channel_Dis;     //更新系统参数对象的通道间距
                    SystemParameter.getInstance().disSensorStepLen=channel_Step;    //更新系统参数对象的通道步长
                    SystemParameter.getInstance().nStepInterval = step_Interval;       //更新系统参数中的步数间隔
                    DeviceInfoBean deviceInfoBean = new DeviceInfoBean();
                    deviceInfoBean.setChannelCount(channel_num);
                    deviceInfoBean.setChannelDistance(channel_Dis);
                    deviceInfoBean.setStepDistance(channel_Step);
                    deviceInfoBean.setStepInterval(step_Interval);
                    DeviceInfoDao.getInstance().update(deviceInfoBean,SystemParameter.getInstance().deviceID);     //保存参数到数据库
                }
                finish();              //finish掉当前Activity,返回上一个Activity
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
