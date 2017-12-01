package com.tomatoLCJC.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tomatoLCJC.main.spinner.ListItemAdapter;
import com.tomatoLCJC.main.spinner.PhoneListItemAdapter;
import com.tomatoLCJC.main.utils.HoriScrollView;
import com.tomatoLCJC.main.utils.ScrollListView;
import com.tomatoLCJC.tools.dataBase.Bean.DeviceDetectionRecordBean;
import com.tomatoLCJC.tools.dataBase.Dao.DeviceDetectionRecordDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalibrationDataImportActivity extends Activity implements PhoneListItemAdapter.MyClickListener,ListItemAdapter.MyClickListener{

    private ImageView finish;           //返回按钮
    private TextView select_tv;

    List<DeviceDetectionRecordBean> calibrationDataList = DeviceDetectionRecordDao.getInstance().queryAllByID();

    private ScrollListView mData;       //表格内数据
    private PhoneListItemAdapter mDataAdapter;   //数据区滑动适配器
    private ListItemAdapter adapter;

    private HoriScrollView mHeaderHorizontal;   //列标题栏
    private HoriScrollView mDataHorizontal;     //数据区

    private ScrollView mScrollView;
    private ArrayList<Map<String,Object>> list;
    private ListView listListView;
    private long mClickTime = 0;   //点击时间

    private int position;
    private boolean selected;//判断RadioButton是否被选中

    private float x1 = 0,x2 = 0;//空白区滑动坐标标记
    private int firstClickId = -1;//双击事件第一次点击id记录
    private int clickedNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calibration_data_import);

        iniList();
        initView();
        initEvent();
    }

    private void initView(){

        //判断是平板还是手机
        if( isTablet(this) == true ) {           //isTablet(this) == true
            setContentView(R.layout.activity_calibration_data_import_tab);
            finish=(ImageView)findViewById(R.id.return_key);
            select_tv = (TextView)findViewById(R.id.tv_select);

            listListView = (ListView) findViewById(R.id.tab_list);
            adapter = new ListItemAdapter(this, list,this);
            listListView.setAdapter(adapter);                          // < 华为平板适配器这里有问题？？？>

            listListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    RadioButton radioButton = (RadioButton) view.findViewById(R.id.text_radioButton);
                    adapter.clearStates(i);
                    radioButton.setChecked(adapter.getStates(i));
                    adapter.changeSelector(i);
                    position = i;
//                    Log.d("Calibration-list",""+calibrationDataList.get(i).getId());
                    selected = true;
                    //双击事件
                    if ((System.currentTimeMillis() - mClickTime) > 1000 || firstClickId != i || clickedNum < 2 ) {
                        mClickTime = System.currentTimeMillis();
                        firstClickId = i;
                        clickedNum++;
                    } else {
                        clickedNum = 0;
                        select_tv.callOnClick();
                }
                }
            });

            // 判断是否有 RadioButton 被选中，若有，则 selected 为true
            for (int i = 0 ; i < calibrationDataList.size() ; i++) {
                if (!adapter.getStates(i))
                    selected = false;
                else {
                    selected = true;
                    position = i;
                    break;
                }
            }
        }
        else{
            setContentView(R.layout.activity_calibration_data_import);
            finish=(ImageView)findViewById(R.id.return_key);
            select_tv = (TextView)findViewById(R.id.tv_select);

            mData = (ScrollListView) findViewById(R.id.lv_data);
            mDataHorizontal = (HoriScrollView) findViewById(R.id.data_horizontal);
            mHeaderHorizontal = (HoriScrollView) findViewById(R.id.header_horizontal);

            mDataHorizontal.setScrollView(mHeaderHorizontal);
            mHeaderHorizontal.setScrollView(mDataHorizontal);

            mDataAdapter = new PhoneListItemAdapter(this,list,this);
            mData.setAdapter(mDataAdapter);

            //空白区滑动时间监听
            mScrollView = (ScrollView)findViewById(R.id.lv_scrollview);
            mScrollView.setOnTouchListener(new AdapterView.OnTouchListener(){
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            x1 = motionEvent.getX();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            x2 = motionEvent.getX();
                            mHeaderHorizontal.scrollBy((int)((x1-x2)/4),0);
                    }
                    return false;
                }
            });

            mData.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    RadioButton radioButton = (RadioButton) view.findViewById(R.id.tv_radioButton);
                    mDataAdapter.clearStates(i);
                    radioButton.setChecked(mDataAdapter.getStates(i));
                    mDataAdapter.changeSelector(i);
                    position = i;
                    selected = true;
                    //双击事件
                    if ((System.currentTimeMillis() - mClickTime) > 1000 || firstClickId != i || clickedNum < 2) {
                        mClickTime = System.currentTimeMillis();
                        firstClickId = i;
                        clickedNum++;
                    } else {
                        clickedNum = 0;
                        select_tv.callOnClick();
                    }
                }
            });

            // 判断是否有 RadioButton 被选中，若有，则 selected 为true
            for (int i = 0 ; i < calibrationDataList.size() ; i++) {
                if (!mDataAdapter.getStates(i))
                    selected = false;
                else {
                    selected = true;
                    position = i;
                    break;
                }
            }
        }

    }

    private void iniList(){
        list = new ArrayList<>();
        for(int i = 0 ; i < calibrationDataList.size() ; i++){
            Map<String,Object> map = new HashMap<>();
            map.put("radioButton","");
            //提取时间（只含年月日）并设置时间格式
            String str = calibrationDataList.get(i).getDetectionTime();
            String time,year = str.substring(0,4),month= str.substring(4,6),day= str.substring(6,8);
            time = year + "-" + month + "-" + day;
            map.put("time",time);
            map.put("thickness",""+calibrationDataList.get(i).getSteelThickness());
            map.put("liftOffValue",""+calibrationDataList.get(i).getUpliftValue());
            map.put("material",calibrationDataList.get(i).getSteelTexture());
            map.put("deviceName",calibrationDataList.get(i).getDeviceName());
            map.put("numOfProbes",""+calibrationDataList.get(i).getChannelCount());
            list.add(map);
        }
    }

    public void initEvent(){
        finish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        select_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final Dialog selectSuccessfully = new Dialog(CalibrationDataImportActivity.this, R.style.select_successfully);
                if(selected){
                    Intent intent = new Intent(CalibrationDataImportActivity.this,CalibrationDataSelectedActivity.class);
                    //用Bundle携带数据
                    Bundle bundle=new Bundle();
                    //传递name参数为tinyphp
                    bundle.putInt("position", firstClickId);
//                    Log.d("CaliImport",String.valueOf(firstClickId));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    /*selectSuccessfully.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            selectSuccessfully.dismiss();
                        }
                    }, 500);
                    selectSuccessfully.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
//                            Intent intent = new Intent(CalibrationDataImportActivity.this,CalibrationDataSelectedActivity.class);
//                            intent.putExtra("deviceID",firstClickId);
//                            startActivity(intent);
//                            CalibrationDataSelectedActivity.startDataSelectActivity(CalibrationDataImportActivity.this,firstClickId);
//                            finish();
                        }
                    });*/
                }else {
                    Toast.makeText(CalibrationDataImportActivity.this,"请先选择",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void clickListener(View v) {
        position = (Integer)v.getTag();
        selected = true;
    }

    //判断是手机还是平板方法
    public static boolean isTablet(Context context){
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >=Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
