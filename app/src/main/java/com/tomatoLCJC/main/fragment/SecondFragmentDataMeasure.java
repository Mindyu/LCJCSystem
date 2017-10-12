package com.tomatoLCJC.main.fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
//import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tomatoLCJC.main.R;
import com.tomatoLCJC.main.utils.ChangeButtonNumsUtil;
import com.tomatoLCJC.tools.Parameter.SystemParameter;
import com.tomatoLCJC.tools.chart.ChartService;

import org.achartengine.GraphicalView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zch22 on 2017/5/14.
 */
public class SecondFragmentDataMeasure extends Fragment {
    public ChartService mService = null;   //画图工具类,控制原始曲线的小表及弹出窗口的大表
    public ChartService mService1 = null;   //画图工具类,控制横向梯度曲线的小表及弹出窗口的大表
    private LinearLayout chartLayout_1;//存放图表的布局容器
    private LinearLayout chartLayout_2;//存放图表的布局容器
    private LinearLayout ChannelButtonLayout;//存放通道选择按钮的布局容器
    private GraphicalView mGraphicalView;  //原始曲线图表
    private GraphicalView mGraphicalView_1;  //梯度曲线图表
    private View  bigChartView; //大表的视图
    private GraphicalView bigGraphicalView;//点击第一个小图标对应的大图表
    private GraphicalView bigGraphicalView_1;//点击第二个小图标对应的大图表
    private ImageView backBtn;  //在大表中的返回按钮
    private PopupWindow mPopupWindow;//弹出窗
    private  LinearLayout bigChartLayout;//放大图表的布局
    private long[] mHits = new long[2];//存储时间的数组
    private long[] mHits1 = new long[2];//存储时间的数组
    private TextView primary_curve;
    private TextView portait_curve;
    public SecondFragmentDataMeasure(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.second_fragment_datameasure, container, false);
        //折线图
        chartLayout_1 = (LinearLayout) view.findViewById(R.id.chart_1);
        chartLayout_2 = (LinearLayout) view.findViewById(R.id.chart_2);
        ChannelButtonLayout = (LinearLayout) view.findViewById(R.id.channelButtonGroupLayout);

        //下面是实现关于小图表点击后边横屏的功能
        bigChartView = getActivity().getLayoutInflater().inflate(R.layout.fragment_bigchart, null);
        bigChartLayout=(LinearLayout)  bigChartView.findViewById(R.id.bigChart);
        backBtn=(ImageView) bigChartView.findViewById(R.id.back);
        //初始化弹出框
        mPopupWindow = new PopupWindow(bigChartView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

        //监听事件，popuwindow被撤销时屏幕锁定为竖屏
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss(){
                mService.setViewFlag(false);
                mService1.setViewFlag(false);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
        //大图表返回事件
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mPopupWindow.dismiss();
            }
        });
        primary_curve= (TextView) view.findViewById(R.id.primary);
        primary_curve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.resetXYAxis();
                mService1.resetXYAxis();
            }
        });
        portait_curve= (TextView) view.findViewById(R.id.portait);
        portait_curve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                primary_curve.callOnClick();
            }
        });
        return view;
    }
    //每次执行onResume()方法都重新初始化画图工具类的参数
    @Override
    public void onResume() {
        super.onResume();
        int channelNum = SystemParameter.getInstance().nChannelNumber;
        initChartAndChannelButtonNum(channelNum );
    }

    //根据参数channelNum初始化图标和通道按钮数量
    public void initChartAndChannelButtonNum(int channelNum ){
        //重新初始化画图工具类的参数
        mService = new ChartService(getActivity(), channelNum,1);
        mService.setXYMultipleSeriesDataset("");
        mService.setXYMultipleSeriesRenderer();
        mService1 = new ChartService(getActivity(),channelNum,2);
        mService1.setXYMultipleSeriesDataset("");
        mService1.setXYMultipleSeriesRenderer();
        mGraphicalView= mService.getGraphicalView();//第一个的小表
        bigGraphicalView= mService.getGraphicalView_1();//第一个的大表
        mGraphicalView_1 = mService1.getGraphicalView();//第二个的小表
        bigGraphicalView_1=mService1.getGraphicalView_1();//第二个的大表

        //将创建的图表加入layout
        chartLayout_1.removeAllViews();
        chartLayout_1.addView(mGraphicalView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        chartLayout_2.removeAllViews();
        chartLayout_2.addView(mGraphicalView_1, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //设置第一个小图表的双击事件
        mGraphicalView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //实现数组的移位操作，点击一次，左移一位，末尾补上当前开机时间（cpu的时间）
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                //双击事件的时间间隔500ms
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    mService1.setViewFlag(true);
                    bigChartLayout.removeAllViews();
                    bigChartLayout.addView(bigGraphicalView);
                    bigGraphicalView.setClickable(true);
                    mPopupWindow.showAtLocation(v,0,0,0);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  //锁定横屏
                }
            }
        });
        mGraphicalView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mService1.getMultipleSeriesRenderer().setXAxisMin(mService.getMultipleSeriesRenderer().getXAxisMin());
                mService1.getMultipleSeriesRenderer().setXAxisMax(mService.getMultipleSeriesRenderer().getXAxisMax());
                mGraphicalView_1.repaint();
                return false;
            }
        });
        //设置第二个小图表的双击事件
        mGraphicalView_1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //实现数组的移位操作，点击一次，左移一位，末尾补上当前开机时间（cpu的时间）
                System.arraycopy(mHits1, 1, mHits1, 0, mHits1.length - 1);
                mHits1[mHits1.length - 1] = SystemClock.uptimeMillis();
                //双击事件的时间间隔500ms
                if (mHits1[0] >= (SystemClock.uptimeMillis() - 500)) {
                    bigChartLayout.removeAllViews();
                    bigChartLayout.addView(bigGraphicalView_1);
                    bigGraphicalView_1.setClickable(true);
                    mPopupWindow.showAtLocation(v, 0, 0, 0);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  //锁定横屏
                }
            }
        });
        mGraphicalView_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mService.getMultipleSeriesRenderer().setXAxisMin(mService1.getMultipleSeriesRenderer().getXAxisMin());
                mService.getMultipleSeriesRenderer().setXAxisMax(mService1.getMultipleSeriesRenderer().getXAxisMax());
                mGraphicalView.repaint();
                return false;
            }
        });

        //更新通道按钮数
        ChangeButtonNumsUtil changeButtonNumsUtil = new ChangeButtonNumsUtil(channelNum,ChannelButtonLayout, getActivity(),mService,mService1);
        changeButtonNumsUtil.initChannelButtons();

    }

    //画历史记录图
    public void drawHistoryChart(List<Double> xList,List<ArrayList<Double>> detectionValue,List<ArrayList<Double>> gradientValue,String title ,int channelNum ){
        initChartAndChannelButtonNum(channelNum ); //重新初始化
        mService.drawHistoryChart(xList,detectionValue,title);
        mService1.drawHistoryChart(xList,gradientValue,title);
    }
}
