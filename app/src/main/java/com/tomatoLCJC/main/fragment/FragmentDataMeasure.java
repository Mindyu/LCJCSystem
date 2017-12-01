package com.tomatoLCJC.main.fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.tomatoLCJC.tools.Chart.ScanningChart;

import java.util.ArrayList;
import java.util.List;

/**
 * 磁力测量页面
 */
public class FragmentDataMeasure extends Fragment {

    private LinearLayout    chartLayout_1;                  // 存放折线图的 Layout
    private LinearLayout    chartLayout_2;                  // 存放扫描图的 Layout
    private LinearLayout    ChannelButtonLayout;            // 存放通道选择按钮的 Layout
    private LinearLayout    bigChartLayout;                 // 存放大图的 Layout
    private PopupWindow     mPopupWindow;                   // 弹出窗
    public  LineChart       chartView;                      // 折线图的 view
    public  ScanningChart   scanView;                       // 扫描图的 view
    private float smallChartWidth,smallChartHeight;           // 小图的画布大小

    // 单例模式
    private static FragmentDataMeasure fragmentDataMeasure = new FragmentDataMeasure();
    public static FragmentDataMeasure getInstance() {
        if (fragmentDataMeasure == null) {
            fragmentDataMeasure = new FragmentDataMeasure();
        }
        return fragmentDataMeasure;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_datameasure, container, false);
        // 折线图
        chartLayout_1 = (LinearLayout) view.findViewById(R.id.chart_1);
        // 扫描图
        chartLayout_2 = (LinearLayout) view.findViewById(R.id.scan_chart);

        ChannelButtonLayout = (LinearLayout) view.findViewById(R.id.channelButtonGroupLayout);
        // 下面是实现关于双击小图后切换到横屏的功能
        final View bigChartView = getActivity().getLayoutInflater().inflate(R.layout.fragment_bigchart,
                null);
        bigChartLayout = (LinearLayout) bigChartView.findViewById(R.id.bigChart);
        ImageView backBtn = (ImageView) bigChartView.findViewById(R.id.back);
        // 初始化弹出框
        mPopupWindow = new PopupWindow(bigChartView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

        // 监听事件，popupWindow 被撤销时屏幕锁定为竖屏
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss(){
                bigChartLayout.removeAllViews();
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                ViewGroup parent = (ViewGroup) scanView.getParent();
                if (parent != null) {
                    parent.removeAllViews();
                }
                parent = (ViewGroup) chartView.getParent();
                if (parent != null) {
                    parent.removeAllViews();
                }
                //Log.d("Fragment",chartView.getWidth()+"  "+chartView.getHeight());
                chartView.setxTranslate(chartView.getxTranslate()/chartView.getWidth()*smallChartWidth);
                chartView.setyTranslate(chartView.getyTranslate()/chartView.getHeight()*smallChartHeight);
                scanView.setxTranslate(scanView.getxTranslate()/scanView.getWidth()*smallChartWidth);
                scanView.setyTranslate(scanView.getyTranslate()/scanView.getHeight()*smallChartHeight);
                chartLayout_1.addView(chartView);
                chartLayout_2.addView(scanView);
            }
        });

        // 大图返回事件
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chartLayout_1.removeAllViews();
                chartLayout_2.removeAllViews();
                bigChartLayout.removeAllViews();
                chartLayout_1.addView(chartView);
                chartLayout_2.addView(scanView);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mPopupWindow.dismiss();
            }
        });
        return view;
    }

    // 每次执行 onResume() 方法都重新初始化画图工具类的参数
    @Override
    public void onResume() {
        super.onResume();
        // 根据系统参数初始化图表和通道按钮数量
        int channelNum = SystemParameter.getInstance().nChannelNumber;
        initChartAndChannelButtonNum(channelNum);
    }

    // 根据参数channelNum初始化图标和通道按钮数量
    public void initChartAndChannelButtonNum(int channelNum){

        chartView = new LineChart(getContext());    // 折线图的小图
        scanView  = new ScanningChart(getContext());     // 扫描图的小图
        chartView.setClickable(true);
        scanView.setClickable(true);

        // 将创建的图表加入 Layout
        chartLayout_1.removeAllViews();
        chartLayout_1.addView(chartView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        chartLayout_2.removeAllViews();
        chartLayout_2.addView(scanView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // 设置折线图的触摸事件，使扫描图保持同步平移和缩放
        scanView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    // 一根手指按下时
                    case MotionEvent.ACTION_DOWN:
                        if (!mPopupWindow.isShowing()) {      //大图未显示时
                            // 双击扫描图跳转到大图
                            if (event.getEventTime() - scanView.getUpTime() < 200) {
                                addToBigchartLayout(scanView);
                            }
                        }
                        scanView.setxDown(event.getX());
                        scanView.setyDown(event.getY());
                        break;
                    // 手指抬起时
                    case MotionEvent.ACTION_UP:
                        scanView.setUpTime(event.getEventTime());
                        break;
                    // 手指移动时
                    case MotionEvent.ACTION_MOVE:
                        // 只有一根手指移动时
                        if (event.getPointerCount() == 1 && event.getAction() != 261
                                && scanView.getxDown() != 0 && scanView.getyDown() != 0 ) {
                            // 实现图形平移
                            scanView.setxTranslate(scanView.getxTranslate()+(event.getX() - scanView.getxDown()));
                            scanView.setyTranslate(scanView.getyTranslate()+(event.getY() - scanView.getyDown()));
                            scanView.setxDown(event.getX());
                            scanView.setyDown(event.getY());
                        }
                        // 有两根手指移动时
                        else if (event.getPointerCount() == 2) {
                            // 实现扫描图缩放
                            double xLenMove = Math.abs(event.getX(0) - event.getX(1));
                            double yLenMove = Math.abs(event.getY(0) - event.getY(1));
                            double lenMove = Math.sqrt(xLenMove * xLenMove + yLenMove * yLenMove);
                            // 动态更新
                            // 设置最小缩放比例为 0.4
                            if (scanView.getxScale() + (lenMove / scanView.getLenDown() - 1) > 0.4) {
                                scanView.setxScale((float) (scanView.getxScale() + (lenMove / scanView.getLenDown()  - 1)));
                                scanView.setyScale((float) (scanView.getyScale() + (lenMove / scanView.getLenDown()  - 1)));
                                scanView.setLenDown(lenMove);
                            }
                            scanView.setxDown(0);
                            scanView.setyDown(0);
                        }
                        break;
                    // 有两根手指按下时
                    case 261:
                        double xLenDown = Math.abs(event.getX(0) - event.getX(1));
                        double yLenDown = Math.abs(event.getY(0) - event.getY(1));
                        scanView.setLenDown(Math.sqrt(xLenDown * xLenDown + yLenDown * yLenDown));
                        break;
                    // 两根手指中的一根抬起时
                    case MotionEvent.ACTION_POINTER_UP:
                        scanView.setxDown(0);
                        scanView.setyDown(0);
                        break;
                    default:
                        break;
                }
                scanView.invalidate();  //手势完成时重绘
                chartView.setxTranslate(scanView.getxTranslate());
                chartView.setxScale(scanView.getxScale());
                chartView.postInvalidate();
                // 只有当返回 false 时才会开启手势检测效果，否则折线图将无法移动和缩放
                return false;
            }
        });

        // 设置折线图的触摸事件，使扫描图保持同步平移和缩放
        chartView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    // 一根手指按下时
                    case MotionEvent.ACTION_DOWN:
                        if (!mPopupWindow.isShowing()) {      //大图未显示时
                            // 双击扫描图跳转到大图
                            if (event.getEventTime() - chartView.getUpTime() < 200) {
                                addToBigchartLayout(chartView);
                            }
                        }
                        chartView.setxDown(event.getX());
                        chartView.setyDown(event.getY());
                        break;
                    // 手指抬起时
                    case MotionEvent.ACTION_UP:
                        chartView.setUpTime(event.getEventTime());
                        break;
                    // 手指移动时
                    case MotionEvent.ACTION_MOVE:
                        // 只有一根手指移动时
                        if (event.getPointerCount() == 1 && event.getAction() != 261
                                && chartView.getxDown() != 0 && chartView.getyDown() != 0 ) {
                            // 实现图形平移
                            chartView.setxTranslate(chartView.getxTranslate()+(event.getX() - chartView.getxDown()) );
                            chartView.setyTranslate(chartView.getyTranslate()+(event.getY() - chartView.getyDown()) );
                            chartView.setxDown(event.getX());
                            chartView.setyDown(event.getY());
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
                            if (chartView.getxScale() + (lenMove / chartView.getLenDown() - 1) > 0.4) {
                                chartView.setxScale((float) (chartView.getxScale() + (lenMove / chartView.getLenDown()  - 1)));
                                chartView.setyScale((float) (chartView.getyScale() + (lenMove / chartView.getLenDown()  - 1)));
                                chartView.setLenDown(lenMove);
                            }
                            chartView.setxDown(0);
                            chartView.setyDown(0);
                        }
                        break;
                    // 有两根手指按下时
                    case 261:
                        double xLenDown = Math.abs(event.getX(0) - event.getX(1));
                        double yLenDown = Math.abs(event.getY(0) - event.getY(1));
                        chartView.setLenDown(Math.sqrt(xLenDown * xLenDown + yLenDown * yLenDown));
                        break;
                    // 两根手指中的一根抬起时
                    case MotionEvent.ACTION_POINTER_UP:
                        chartView.setxDown(0);
                        chartView.setyDown(0);
                        break;
                    default:
                        break;
                }
                chartView.postInvalidate();   //手势完成时重绘
                scanView.setxTranslate(chartView.getxTranslate());
                scanView.setxScale(chartView.getxScale());
                scanView.postInvalidate();
                // 只有当返回 false 时才会开启手势检测效果，否则折线图将无法移动和缩放
                return false;
            }
        });


        final TextView primary_curve= (TextView) getActivity().findViewById(R.id.primary);
        primary_curve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartView.resetAxis();
                scanView.resetAxis();
            }
        });
        final TextView horizontal_curve= (TextView) getActivity().findViewById(R.id.horizontal_curve);
        horizontal_curve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                primary_curve.callOnClick();
            }
        });
        // 更新通道按钮数
        ChangeButtonNumsUtil changeButtonNumsUtil = new ChangeButtonNumsUtil(channelNum, ChannelButtonLayout,this, getActivity());
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
        chartView.cleanChart();
        scanView.cleanChart();
    }

    // 画历史记录图
    public void drawHistoryChart(List<Double> xList,List<ArrayList<Double>> yList,
                                 List<ArrayList<Double>> denoisingValue,
                                 List<ArrayList<Double>> flawValue,String title ,int channelNum){
        initChartAndChannelButtonNum(channelNum); //重新初始化
        // 添加扫描图
        chartView.drawView(xList, denoisingValue);
        chartLayout_1.removeAllViews();
        chartLayout_1.addView(chartView);
        chartView.setClickable(true);
        // 添加扫描图
        scanView.drawView(xList, yList, flawValue);
        chartLayout_2.removeAllViews();
        chartLayout_2.addView(scanView);
        scanView.setClickable(true);
    }

    // 获得第一个图标的屏幕截图
    public Bitmap picCut(){
        chartLayout_1.setDrawingCacheEnabled(true);
        Bitmap tBitmap = chartLayout_1.getDrawingCache();
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = tBitmap.createBitmap(tBitmap);
        chartLayout_1.setDrawingCacheEnabled(false);
        if (tBitmap != null) {
            return tBitmap;
        } else {
            return null;
        }
    }

}

