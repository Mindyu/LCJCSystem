package com.tomatoLCJC.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tomatoLCJC.main.utils.Point;
import com.tomatoLCJC.tools.Chart.LineChart;
import com.tomatoLCJC.tools.dataBase.Bean.DeviceDetectionRecordBean;
import com.tomatoLCJC.tools.dataBase.Dao.DeviceDetectionRecordDao;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CalibrationDataSelectedActivity extends Activity{

    private ImageView finish;
    private TextView tvTime;
    private TextView tvDeviceName;
    private TextView tvThickness;
    private TextView tvLiftUpValue;
    private TextView tvMaterial;
    private TextView tvProbesNum;
    private TextView tvDefectPercent_1;
    private TextView tvDefectPercent_2;
    private TextView tvDefectPercent_3;
    private TextView tvDefectPercent_4;
    private TextView tvFlawValue1;
    private TextView tvFlawValue2;
    private TextView tvFlawValue3;
    private TextView tvFlawValue4;
    private Button selectBtn;
    private Button cancelBtn;

    private List<DeviceDetectionRecordBean> calibrationDataList = DeviceDetectionRecordDao.getInstance().queryAllByID();
    private int id;

//    private ChartService mChartService = null;
//    private GraphicalView mGraphicalView;  //原始曲线图表
    private LineChart lineChart;
    private LinearLayout mChartLayout;

    private Point P1,P2,P3,P4;   //四个极大值点

    private List<Double> xList;
    private List<ArrayList<Double>> yList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //锁定竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_calibration_data_selected);

        //接受CalibrationDataImportActivity传送的Id值
        Bundle bundle = this.getIntent().getExtras();
        id = bundle.getInt("position");
//        Log.d("Caliselect",""+calibrationDataList.get(id).getId());

        initView();
        initEvent();
        setTable();       //填充表格数据
        drawChart();      //绘制曲线数据
    }

    public void initView(){
        finish = (ImageView)findViewById(R.id.calibration_data_select_return_key);
        tvTime = (TextView)findViewById(R.id.select_time);
        tvDeviceName = (TextView)findViewById(R.id.select_deviceName);
        tvThickness = (TextView)findViewById(R.id.select_thickness_value);
        tvLiftUpValue = (TextView)findViewById(R.id.select_uplift_value);
        tvMaterial = (TextView)findViewById(R.id.select_material_value);
        tvProbesNum = (TextView)findViewById(R.id.select_probesNum);
        tvDefectPercent_1 = (TextView)findViewById(R.id.defect_percent_1);
        tvDefectPercent_2 = (TextView)findViewById(R.id.defect_percent_2);
        tvDefectPercent_3 = (TextView)findViewById(R.id.defect_percent_3);
        tvDefectPercent_4 = (TextView)findViewById(R.id.defect_percent_4);
        tvFlawValue1 = (TextView)findViewById(R.id.select_flaw_value_1);
        tvFlawValue2 = (TextView)findViewById(R.id.select_flaw_value_2);
        tvFlawValue3 = (TextView)findViewById(R.id.select_flaw_value_3);
        tvFlawValue4 = (TextView)findViewById(R.id.select_flaw_value_4);
        mChartLayout = (LinearLayout) findViewById(R.id.calibration_device_chart);
        selectBtn = (Button)findViewById(R.id.device_select_button);
        cancelBtn = (Button)findViewById(R.id.device_cancel_button);

        xList = new LinkedList<Double>();
        yList = new LinkedList<ArrayList<Double>>();

//        mChartService = new ChartService(CalibrationDataSelectedActivity.this,2,3);
//        mChartService.setXYMultipleSeriesDataset("");
//        mChartService.setXYMultipleSeriesRenderer();
//        mGraphicalView= mChartService.getGraphicalView();     //设备校准的图表
        lineChart = new LineChart(CalibrationDataSelectedActivity.this);
        mChartLayout.removeAllViews();
        mChartLayout.addView(lineChart, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void initEvent(){
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selectBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DeviceDetectionRecordDao.getInstance().importDetectionData(calibrationDataList.get(id).getId());  //导入选中ID的校准数据
                //跳转到主页面
                Intent intent = new Intent(CalibrationDataSelectedActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish.callOnClick();
            }
        });
    }

    public void drawChart(){
        setXYList();
        List<Point> tempPoint = new ArrayList<>();  // InstrumentCalibration.getMaximumValue(tmpArrY);
        tempPoint.add(P1);tempPoint.add(P2);tempPoint.add(P3);tempPoint.add(P4);
//        mChartService.drawDetectionChart(xList,yList,tempPoint);
        lineChart.drawCalibrationView(xList,yList,tempPoint);
    }

    public void setTable(){
        String str = calibrationDataList.get(id).getDetectionTime();
        String time,year = str.substring(0,4),month= str.substring(4,6),day= str.substring(6,8);
        time = year + "-" + month + "-" + day;
        tvTime.setText(time);
        tvDeviceName.setText(""+calibrationDataList.get(id).getDeviceName());
        tvThickness.setText(""+calibrationDataList.get(id).getSteelThickness());
        tvLiftUpValue.setText(""+calibrationDataList.get(id).getUpliftValue());
        tvMaterial.setText(""+calibrationDataList.get(id).getSteelTexture());
        tvProbesNum.setText(""+calibrationDataList.get(id).getChannelCount());
        tvDefectPercent_1.setText(""+calibrationDataList.get(id).getDefectPercent1()+"%缺陷:");
        tvDefectPercent_2.setText(""+calibrationDataList.get(id).getDefectPercent2()+"%缺陷:");
        tvDefectPercent_3.setText(""+calibrationDataList.get(id).getDefectPercent3()+"%缺陷:");
        tvDefectPercent_4.setText(""+calibrationDataList.get(id).getDefectPercent4()+"%缺陷:");
        tvFlawValue1.setText(""+calibrationDataList.get(id).getDefectPercent1_value());
        tvFlawValue2.setText(""+calibrationDataList.get(id).getDefectPercent2_value());
        tvFlawValue3.setText(""+calibrationDataList.get(id).getDefectPercent3_value());
        tvFlawValue4.setText(""+calibrationDataList.get(id).getDefectPercent4_value());
    }

    public void setXYList(){      //根据四个极值模拟一条曲线
        P1 = new Point((float) calibrationDataList.get(id).getDefectPercent1_x()*(float)0.001,
                (float) calibrationDataList.get(id).getDefectPercent1_value());
        P2 = new Point((float) calibrationDataList.get(id).getDefectPercent2_x()*(float)0.001,
                (float) calibrationDataList.get(id).getDefectPercent2_value());
        P3 = new Point((float) calibrationDataList.get(id).getDefectPercent3_x()*(float)0.001,
                (float) calibrationDataList.get(id).getDefectPercent3_value());
        P4 = new Point((float) calibrationDataList.get(id).getDefectPercent4_x()*(float)0.001,
                (float) calibrationDataList.get(id).getDefectPercent4_value());
        double offset = 0.012;
        yList.add(new ArrayList<Double>());
        yList.add(new ArrayList<Double>());
        xList.add(0.0);
        yList.get(0).add(0.0);

        xList.add(P1.getX()-offset);
        yList.get(0).add(0.0);

        xList.add((double) P1.getX());
        yList.get(0).add((double) P1.getY());

        xList.add(P1.getX()+offset);
        yList.get(0).add(0.0);
        xList.add(P2.getX()-offset);
        yList.get(0).add(0.0);

        xList.add((double) P2.getX());
        yList.get(0).add((double) P2.getY());

        xList.add(P2.getX()+offset);
        yList.get(0).add(0.0);
        xList.add(P3.getX()-offset);
        yList.get(0).add(0.0);

        xList.add((double) P3.getX());
        yList.get(0).add((double) P3.getY());

        xList.add(P3.getX()+offset);
        yList.get(0).add(0.0);
        xList.add(P4.getX()-offset);
        yList.get(0).add(0.0);

        xList.add((double) P4.getX());
        yList.get(0).add((double) P4.getY());

        xList.add(P4.getX()+offset);
        yList.get(0).add(0.0);
        xList.add(P4.getX()+2*offset);
        yList.get(0).add(0.0);
    }

    //其他Activity调用这个Activity并传id方法
    public static void startDataSelectActivity(Context context, int id){
        Intent intent = new Intent(context,CalibrationDataSelectedActivity.class);
        intent.putExtra("position",id);
        context.startActivity(intent);
    }
}
