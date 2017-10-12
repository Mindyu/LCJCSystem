package com.tomatoLCJC.main.fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.tomatoLCJC.tools.ScanningChart.ScanningService;
import com.tomatoLCJC.tools.chart.ChartService;

import org.achartengine.GraphicalView;
import java.util.ArrayList;
import java.util.List;

/**
 * 磁力测量页面
 */
public class FragmentDataMeasure extends Fragment {
    public ChartService mService = null;        // 画图工具类,控制去噪后的曲线的小表及弹出窗口的大表
    private LinearLayout chartLayout_1;         // 存放图表的布局容器
    public LinearLayout scanChart;              // 存放扫描图的 layout
    public ScanningService scanView;            // 绘制扫描图
    private LinearLayout ChannelButtonLayout;   // 存放通道选择按钮的布局容器
    public GraphicalView mGraphicalView;        // 原始曲线图表
    private View bigChartView;                  // 大表的视图
    private GraphicalView bigGraphicalView;     // 点击第一个小图标对应的大图表
    private ImageView backBtn;                  // 在大表中的返回按钮
    public PopupWindow mPopupWindow;            // 弹出窗
    public LinearLayout bigChartLayout;         // 放大图表的布局
    private long[] mHits = new long[2];         // 存储时间的数组
    private TextView horizontal_curve;
    private TextView primary_curve;
    private FragmentDataMeasure() {}

    private float xDown = 0;
    private double xMin1, xMin2;
    public float canvasWidth;

    private static FragmentDataMeasure fragmentDataMeasure = null;

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
        scanChart = (LinearLayout) view.findViewById(R.id.scan_chart);

        ChannelButtonLayout = (LinearLayout) view.findViewById(R.id.channelButtonGroupLayout);
        // 下面是实现关于小图表点击后边横屏的功能
        bigChartView = getActivity().getLayoutInflater().inflate(R.layout.fragment_bigchart, null);
        bigChartLayout=(LinearLayout)  bigChartView.findViewById(R.id.bigChart);
        backBtn=(ImageView) bigChartView.findViewById(R.id.back);

        // 初始化弹出框
        mPopupWindow = new PopupWindow(bigChartView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

        // 监听事件，popupWindow 被撤销时屏幕锁定为竖屏
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss(){
                mService.setViewFlag(false);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                bigChartLayout.removeAllViews();
                ViewGroup parent = (ViewGroup) scanView.getParent();
                if (parent != null) {
                    parent.removeAllViews();
                }
                scanChart.addView(scanView);
            }
        });

        // 大图表返回事件
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mService.setViewFlag(false);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mPopupWindow.dismiss();
                xMin2 = mService.getMultipleSeriesRenderer().getXAxisMin();
                double xTranslate = xMin1 - xMin2;
                scanView.xTransform(xTranslate, canvasWidth);
            }
        });
        return view;
    }

    // 每次执行onResume()方法都重新初始化画图工具类的参数
    @Override
    public void onResume() {
        super.onResume();
        // 根据系统参数初始化图表和通道按钮数量
        int channelNum = SystemParameter.getInstance().nChannelNumber;
        initChartAndChannelButtonNum(channelNum);
    }

    // 根据参数channelNum初始化图标和通道按钮数量
    public void initChartAndChannelButtonNum(int channelNum){
        //重新初始化画图工具类的参数
        mService = new ChartService(getActivity(), channelNum,3);
        mService.setXYMultipleSeriesDataset("");
        mService.setXYMultipleSeriesRenderer();
        mGraphicalView= mService.getGraphicalView();//第一个的小表
        bigGraphicalView= mService.getGraphicalView_1();//第一个的大表
        scanView = new ScanningService(getContext(), false); // 扫描图的小表

        // 将创建的图表加入layout
        chartLayout_1.removeAllViews();
        chartLayout_1.addView(mGraphicalView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scanChart.removeAllViews();
        scanChart.addView(scanView);

        // 设置第一个小图表的双击事件
        mGraphicalView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 实现数组的移位操作，点击一次，左移一位，末尾补上当前开机时间（cpu的时间）
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                // 双击事件的时间间隔500ms
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    mService.setViewFlag(true);
                    bigChartLayout.removeAllViews();
                    bigChartLayout.addView(bigGraphicalView);
                    bigGraphicalView.setClickable(true);
                    mPopupWindow.showAtLocation(v, 0, 0, 0);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  //锁定横屏
                    xMin1 = mService.getMultipleSeriesRenderer().getXAxisMin();
                    canvasWidth = scanView.getCanvasWidth();
                }
            }
        });


        // 设置第一个小表的触摸事件  坐标的同步
        mGraphicalView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xDown = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getX() > xDown) {
                            scanView.xTranslate += event.getX() - xDown;
                            xDown = event.getX();
                        } else if (event.getX() < xDown) {
                            scanView.xTranslate -= xDown - event.getX();
                            xDown = event.getX();
                        }
                        break;
                }
                return false;
            }
        });

        primary_curve= (TextView) getActivity().findViewById(R.id.primary);
        primary_curve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.resetXYAxis();
            }
        });
        horizontal_curve= (TextView) getActivity().findViewById(R.id.horizontal_curve);
        horizontal_curve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.resetXYAxis();
            }
        });
        // 更新通道按钮数
        ChangeButtonNumsUtil changeButtonNumsUtil = new ChangeButtonNumsUtil(channelNum,
                ChannelButtonLayout, getActivity(),mService,null);
        changeButtonNumsUtil.initChannelButtons();
    }

    public void bigScanChart(ScanningService scanView) {
        ViewGroup parent = (ViewGroup) scanView.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        bigChartLayout.addView(scanView);
        mPopupWindow.showAtLocation(scanView, 0, 0, 0);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 锁定横屏
    }

    // 画历史记录图
    public void drawHistoryChart(List<Double> xList,List<ArrayList<Double>> yList,
                                 List<ArrayList<Double>> denoisingValue,
                                 List<ArrayList<Double>> flawValue,String title ,int channelNum){
        initChartAndChannelButtonNum(channelNum); //重新初始化
        mService.drawHistoryChart(xList,denoisingValue,title);//去噪后的曲线
        // 添加扫描图
        scanView = new ScanningService(getContext(), xList, yList, flawValue, true);
        scanChart.removeAllViews();
        scanChart.addView(scanView);
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

