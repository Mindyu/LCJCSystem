package com.tomatoLCJC.main.fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import com.tomatoLCJC.tools.Chart.AbstractChartService;
import com.tomatoLCJC.tools.Chart.LineChart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zch22 on 2017/5/14.
 */
public class SecondFragmentDataMeasure extends Fragment {
    private LinearLayout chartLayout_1;                 //存放图表的布局容器
    private LinearLayout chartLayout_2;                 //存放图表的布局容器
    public  LineChart    chartView_1;                   // 原始曲线图的 view
    public  LineChart    chartView_2;                   // 梯度曲线图的 view
    private LinearLayout ChannelButtonLayout;           //存放通道选择按钮的布局容器
    private LinearLayout bigChartLayout;                //放大图表的布局
    private PopupWindow  mPopupWindow;                  //弹出窗
    private float smallChartWidth,smallChartHeight;           // 小图的画布大小

    private TextView     primary_curve;
    private TextView     portait_curve;
    private ImageView    backBtn;                       //在大表中的返回按钮

    // 单例模式
    private static SecondFragmentDataMeasure secondFragmentDataMeasure = new SecondFragmentDataMeasure();
    public static SecondFragmentDataMeasure getInstance() {
        if (secondFragmentDataMeasure == null) {
            secondFragmentDataMeasure = new SecondFragmentDataMeasure();
        }
        return secondFragmentDataMeasure;
    }

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

        final View bigChartView = getActivity().getLayoutInflater().inflate(R.layout.fragment_bigchart,
                null);
        //下面是实现关于小图表点击后边横屏的功能
        bigChartLayout = (LinearLayout) bigChartView.findViewById(R.id.bigChart);
        backBtn = (ImageView) bigChartView.findViewById(R.id.back);
        //初始化弹出框
        mPopupWindow = new PopupWindow(bigChartView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

        //监听事件，popuwindow被撤销时屏幕锁定为竖屏
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                bigChartLayout.removeAllViews();
                ViewGroup parent = (ViewGroup) chartView_1.getParent();
                if (parent != null) {
                    parent.removeAllViews();
                }
                parent = (ViewGroup) chartView_2.getParent();
                if (parent != null) {
                    parent.removeAllViews();
                }
                chartView_1.setxTranslate(chartView_1.getxTranslate()/chartView_1.getWidth()*smallChartWidth);
                chartView_1.setyTranslate(chartView_1.getyTranslate()/chartView_1.getHeight()*smallChartHeight);
                chartView_2.setxTranslate(chartView_2.getxTranslate()/chartView_2.getWidth()*smallChartWidth);
                chartView_2.setyTranslate(chartView_2.getyTranslate()/chartView_2.getHeight()*smallChartHeight);
                chartLayout_1.addView(chartView_1);
                chartLayout_2.addView(chartView_2);
            }
        });
        //大图表返回事件
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chartLayout_1.removeAllViews();
                chartLayout_2.removeAllViews();
                bigChartLayout.removeAllViews();
                chartLayout_1.addView(chartView_1);
                chartLayout_2.addView(chartView_2);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mPopupWindow.dismiss();
            }
        });
        primary_curve = (TextView) view.findViewById(R.id.primary);
        primary_curve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartView_1.resetAxis();
                chartView_2.resetAxis();
            }
        });
        portait_curve = (TextView) view.findViewById(R.id.portait);
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
        initChartAndChannelButtonNum(channelNum);
    }

    //根据参数channelNum初始化图标和通道按钮数量
    public void initChartAndChannelButtonNum(int channelNum) {
        //重新初始化画图工具类的参数
        chartView_1 = new LineChart(getContext());    // 折线图的小图
        chartView_2 = new LineChart(getContext());    // 折线图的小图
        chartView_1.setClickable(true);
        chartView_2.setClickable(true);

        //将创建的图表加入layout
        chartLayout_1.removeAllViews();
        chartLayout_1.addView(chartView_1, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        chartLayout_2.removeAllViews();
        chartLayout_2.addView(chartView_2, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        chartView_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    // 一根手指按下时
                    case MotionEvent.ACTION_DOWN:
                        if (!mPopupWindow.isShowing()) {   //大图未显示时
                            // 双击扫描图跳转到大图
                            if (event.getEventTime() - chartView_1.getUpTime() < 200) {
                                addToBigchartLayout(chartView_1);
                            }
                        }
                        chartView_1.setxDown(event.getX());
                        chartView_1.setyDown(event.getY());
                        break;
                    // 手指抬起时
                    case MotionEvent.ACTION_UP:
                        chartView_1.setUpTime(event.getEventTime());
                        break;
                    // 手指移动时
                    case MotionEvent.ACTION_MOVE:
                        // 只有一根手指移动时
                        if (event.getPointerCount() == 1 && event.getAction() != 261
                                && chartView_1.getxDown() != 0 && chartView_1.getyDown() != 0) {
                            // 实现图形平移
                            chartView_1.setxTranslate(chartView_1.getxTranslate() + (event.getX() - chartView_1.getxDown()));
                            chartView_1.setyTranslate(chartView_1.getyTranslate() + (event.getY() - chartView_1.getyDown()));
                            chartView_1.setxDown(event.getX());
                            chartView_1.setyDown(event.getY());
                            // 实现折线图平移
                        }
                        // 有两根手指移动时
                        else if (event.getPointerCount() == 2) {
                            // 实现扫描图缩放
                            double xLenMove = Math.abs(event.getX(0) - event.getX(1));
                            double yLenMove = Math.abs(event.getY(0) - event.getY(1));
                            double lenMove = Math.sqrt(xLenMove * xLenMove + yLenMove * yLenMove);
                            // 动态更新
                            // 设置最小缩放比例为 0.4
                            if (chartView_1.getxScale() + (lenMove / chartView_1.getLenDown() - 1) > 0.4) {
                                chartView_1.setxScale((float) (chartView_1.getxScale() + (lenMove / chartView_1.getLenDown() - 1)));
                                chartView_1.setyScale((float) (chartView_1.getyScale() + (lenMove / chartView_1.getLenDown() - 1)));
                                chartView_1.setLenDown(lenMove);
                            }
                            chartView_1.setxDown(0);
                            chartView_1.setyDown(0);
                        }
                        break;
                    // 有两根手指按下时
                    case 261:
                        double xLenDown = Math.abs(event.getX(0) - event.getX(1));
                        double yLenDown = Math.abs(event.getY(0) - event.getY(1));
                        chartView_1.setLenDown(Math.sqrt(xLenDown * xLenDown + yLenDown * yLenDown));
                        break;
                    // 两根手指中的一根抬起时
                    case MotionEvent.ACTION_POINTER_UP:
                        chartView_1.setxDown(0);
                        chartView_1.setyDown(0);
                        break;
                    default:
                        break;
                }
                chartView_1.invalidate();  //手势完成时重绘
                chartView_2.setxTranslate(chartView_1.getxTranslate());
                chartView_2.setxScale(chartView_1.getxScale());
                chartView_2.postInvalidate();
                // 只有当返回 false 时才会开启手势检测效果，否则折线图将无法移动和缩放
                return false;
            }
        });

        chartView_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    // 一根手指按下时
                    case MotionEvent.ACTION_DOWN:
                        if (!mPopupWindow.isShowing()) {
                            // 双击扫描图跳转到大图
                            if (event.getEventTime() - chartView_2.getUpTime() < 200) {
                                addToBigchartLayout(chartView_2);
                            }
                        }
                        chartView_2.setxDown(event.getX());
                        chartView_2.setyDown(event.getY());
                        break;
                    // 手指抬起时
                    case MotionEvent.ACTION_UP:
                        chartView_2.setUpTime(event.getEventTime());
                        break;
                    // 手指移动时
                    case MotionEvent.ACTION_MOVE:
                        // 只有一根手指移动时
                        if (event.getPointerCount() == 1 && event.getAction() != 261
                                && chartView_2.getxDown() != 0 && chartView_2.getyDown() != 0) {
                            // 实现图形平移
                            chartView_2.setxTranslate(chartView_2.getxTranslate() + (event.getX() - chartView_2.getxDown()));
                            chartView_2.setyTranslate(chartView_2.getyTranslate() + (event.getY() - chartView_2.getyDown()));
                            chartView_2.setxDown(event.getX());
                            chartView_2.setyDown(event.getY());
                            // 实现折线图平移
                        }
                        // 有两根手指移动时
                        else if (event.getPointerCount() == 2) {
                            // 实现扫描图缩放
                            double xLenMove = Math.abs(event.getX(0) - event.getX(1));
                            double yLenMove = Math.abs(event.getY(0) - event.getY(1));
                            double lenMove = Math.sqrt(xLenMove * xLenMove + yLenMove * yLenMove);
                            // 动态更新
                            // 设置最小缩放比例为 0.4
                            if (chartView_2.getxScale() + (lenMove / chartView_2.getLenDown() - 1) > 0.4) {
                                chartView_2.setxScale((float) (chartView_2.getxScale() + (lenMove / chartView_2.getLenDown() - 1)));
                                chartView_2.setyScale((float) (chartView_2.getyScale() + (lenMove / chartView_2.getLenDown() - 1)));
                                chartView_2.setLenDown(lenMove);
                            }
                            chartView_2.setxDown(0);
                            chartView_2.setyDown(0);
                        }
                        break;
                    // 有两根手指按下时
                    case 261:
                        double xLenDown = Math.abs(event.getX(0) - event.getX(1));
                        double yLenDown = Math.abs(event.getY(0) - event.getY(1));
                        chartView_2.setLenDown(Math.sqrt(xLenDown * xLenDown + yLenDown * yLenDown));
                        break;
                    // 两根手指中的一根抬起时
                    case MotionEvent.ACTION_POINTER_UP:
                        chartView_2.setxDown(0);
                        chartView_2.setyDown(0);
                        break;
                    default:
                        break;
                }
                chartView_2.invalidate();  //手势完成时重绘
                chartView_1.setxTranslate(chartView_2.getxTranslate());
                chartView_1.setxScale(chartView_2.getxScale());
                chartView_1.postInvalidate();
                // 只有当返回 false 时才会开启手势检测效果，否则折线图将无法移动和缩放
                return false;
            }
        });

        //更新通道按钮数
        ChangeButtonNumsUtil changeButtonNumsUtil = new ChangeButtonNumsUtil(channelNum, ChannelButtonLayout, this, getActivity());
        changeButtonNumsUtil.initChannelButtons();
    }

    // 将扫描图添加至大图
    public void addToBigchartLayout(AbstractChartService chart) {
        ViewGroup parent = (ViewGroup) chart.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        smallChartWidth=chart.getCanvasWidth();
        smallChartHeight=chart.getCanvasHeight();
        chart.setxTranslate(chart.getxTranslate()/chart.getCanvasWidth()*bigChartLayout.getWidth());
        chart.setyTranslate(chart.getyTranslate()/chart.getCanvasHeight()*bigChartLayout.getHeight());
        bigChartLayout.addView(chart);
        mPopupWindow.showAtLocation(chart, 0, 0, 0);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 锁定横屏
    }

    // 清屏
    public void cleanChart() {
        chartView_1.cleanChart();
        chartView_2.cleanChart();
    }

    //画历史记录图
    public void drawHistoryChart(List<Double> xList, List<ArrayList<Double>> detectionValue, List<ArrayList<Double>> gradientValue, String title, int channelNum) {
        initChartAndChannelButtonNum(channelNum); //重新初始化
        chartView_1.drawView(xList, detectionValue);
        chartView_2.drawView(xList, gradientValue);
    }

}
