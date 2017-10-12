package com.tomatoLCJC.tools.chart;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.tomatoLCJC.main.MyApplication;
import com.tomatoLCJC.main.R;

public class ChartService {
    private GraphicalView graphicalView;      //小图
    private GraphicalView graphicalView_1;    //大图
    private XYMultipleSeriesDataset multipleSeriesDataset;  // 数据集容器
    private XYMultipleSeriesRenderer multipleSeriesRenderer;// 渲染器容器
    private XYSeries mSeries;               //单条曲线数据集
    private XYSeriesRenderer mRenderer;     //单条曲线渲染器
    private Context context;
    private Boolean viewFlag=false;         //false小图显示   ture大图显示
    private int chaneelNum=1;               //通道数
    private int chartIndex=1;               //1原始数据  2横向   3去噪
    private double warningLine=1.00;        //警戒值
    private MediaPlayer mediaPlayer;
    private double yMin=0;
    private double yMax=300;
    public boolean isFrist=true;   //是否是第一组数据
    String[] lineColors;
//    private TypedArray colors;    //引用类型资源，

    public ChartService(Context context,int chaneelNum,int chartIndex) {
        this.context = context;
        this.chaneelNum=chaneelNum;
        this.chartIndex=chartIndex;
        lineColors = MyApplication.CONTEXT.getResources().getStringArray(R.array.lineColors);
    }

    public XYMultipleSeriesRenderer getMultipleSeriesRenderer(){
        return multipleSeriesRenderer;
    }

    public XYMultipleSeriesDataset getMultipleSeriesDataset(){
        return multipleSeriesDataset;
    }
    //重置坐标轴范围
    public void initXY(){
        multipleSeriesRenderer.setRange(new double[] { 0, 0.2, 0, 300});//xy轴的范围
    }

    // 获取图表
    public GraphicalView getGraphicalView() {
        graphicalView=ChartFactory.getLineChartView(context,multipleSeriesDataset, multipleSeriesRenderer);
        return graphicalView;
    }

    public GraphicalView getGraphicalView_1() {
        graphicalView_1=ChartFactory.getLineChartView(context,multipleSeriesDataset, multipleSeriesRenderer);
        return graphicalView_1;
    }
    //设置某条线为透明
    public void setLineTransparent(int line){
        multipleSeriesRenderer.getSeriesRendererAt(line).setColor(Color.parseColor("#00eae8e9"));
        if (!viewFlag){
            graphicalView.repaint();
        }else{
            graphicalView_1.repaint();
        }
    }
    //设置某条线恢复原来的颜色
    public void setLineRecover(int line){
        String[] lineColors=context.getResources().getStringArray(R.array.lineColors);
        multipleSeriesRenderer.getSeriesRendererAt(line).setColor(Color.parseColor(lineColors[line]));
        if (!viewFlag){
            graphicalView.repaint();
        }else{
            graphicalView_1.repaint();
        }
    }

    public void setViewFlag(Boolean flag){
        this.viewFlag=flag;
        if(flag){
            graphicalView_1.repaint();
        }else{
            graphicalView.repaint();
        }
    }

    //初始化数据集合
    public void setXYMultipleSeriesDataset(String curveTitle) {
        multipleSeriesDataset = new XYMultipleSeriesDataset();
        for (int i=0;i<chaneelNum;i++){
            mSeries = new XYSeries(curveTitle);
            multipleSeriesDataset.addSeries(mSeries);
            multipleSeriesDataset.getSeriesAt(i).add(0,0);
        }
    }

    //初始化渲染器集合并添加渲染器
    public void setXYMultipleSeriesRenderer() {
        multipleSeriesRenderer = new XYMultipleSeriesRenderer();
        multipleSeriesRenderer.setXTitle("");
        multipleSeriesRenderer.setRange(new double[] { 0, 0.2, 0, 300 });//xy轴的范围
        multipleSeriesRenderer.setLabelsColor(Color.BLACK);
        multipleSeriesRenderer.setXLabelsColor(Color.BLACK);//轴标签颜色
        multipleSeriesRenderer.setYLabelsColor(0, Color.BLACK);
        multipleSeriesRenderer.setXLabels(5);//设置X轴显示的刻度标签的个数
        multipleSeriesRenderer.setYLabels(6);//设置Y轴显示的刻度标签的个数
        multipleSeriesRenderer.setXLabelsAlign(Align.RIGHT);//设置刻度线与X轴之间的相对位置关系
        multipleSeriesRenderer.setYLabelsAlign(Align.RIGHT);//设置刻度线与Y轴之间的相对位置关系
        multipleSeriesRenderer.setAxisTitleTextSize(45);// 设置坐标轴标题文本大小
        multipleSeriesRenderer.setLabelsTextSize(35);// 设置轴标签文本大小
        multipleSeriesRenderer.setLegendTextSize(0);// 设置图例文本大小
        multipleSeriesRenderer.setShowLegend(false);
        multipleSeriesRenderer.setPointSize(2f);//曲线描点尺寸
        multipleSeriesRenderer.setFitLegend(true);// 调整合适的位置
        multipleSeriesRenderer.setMargins(new int[] { 40, 120, 30, 30 });// 图形4边距
        multipleSeriesRenderer.setZoomEnabled(true, false);    //X Y轴均可以缩放
        multipleSeriesRenderer.setAxesColor(Color.BLACK);//轴颜色
        multipleSeriesRenderer.setBackgroundColor(Color.WHITE);//背景色
        multipleSeriesRenderer.setMarginsColor(Color.parseColor("#eae9e9"));//边距背景色，默认背景色为黑色，这里修改为白色
        multipleSeriesRenderer.setShowGrid(false);
        multipleSeriesRenderer.setClickEnabled(true);
        for (int i=0;i<chaneelNum;i++) {          //15种折线的颜色
            mRenderer = new XYSeriesRenderer();
//            mRenderer.setColor(colors.getColor(i,0));
            mRenderer.setColor(Color.parseColor(lineColors[i]));
            mRenderer.setPointStyle(PointStyle.CIRCLE);
            mRenderer.setLineWidth(4f);
            multipleSeriesRenderer.addSeriesRenderer(mRenderer);
        }
    }

    //清空图表
    public void clearChart() {
        multipleSeriesRenderer.setChartTitle("");
        initXY();
        yMax=300;
        yMin=0;
        for (int i=0;i<chaneelNum;i++){
            multipleSeriesDataset.getSeriesAt(i).clear();
            multipleSeriesDataset.getSeriesAt(i).add(0,0);
        }
        graphicalView.repaint();
        graphicalView_1.repaint();
    }

    //测量过程中画图的方法
    public void drawMeasureChart(double x,double[] y,int shiftStatus){
        if (shiftStatus == 3) {
            double min = 10000;
            double max = -10000;
            if (isFrist){
                isFrist=false;
                multipleSeriesRenderer.setYAxisMax(Math.ceil(y[0]));
                for(int i=0;i<chaneelNum;i++){
                    multipleSeriesDataset.getSeriesAt(i).clear();
                }
            }else{
                min = multipleSeriesRenderer.getYAxisMin();
                max = multipleSeriesRenderer.getYAxisMax();
            }
            for(int i=0;i<y.length;i++) {
                if (y[i] > max)
                    max = y[i];
                if (y[i] < min)
                    min = y[i];
            }
            if (x > 1.0) {
                multipleSeriesRenderer.setXAxisMin(x - 1.0);
                multipleSeriesRenderer.setXAxisMax(x);
            } else if (x>0.8){
                multipleSeriesRenderer.setXAxisMax(1.0);
            }else if (x>0.6){
                multipleSeriesRenderer.setXAxisMax(0.8);
            }else if (x>0.4){
                multipleSeriesRenderer.setXAxisMax(0.6);
            }else if (x>0.2){
                multipleSeriesRenderer.setXAxisMax(0.4);
            }
            //设置动态y轴
            if (max > multipleSeriesRenderer.getYAxisMax()) {
                multipleSeriesRenderer.setYAxisMax(Math.ceil(max * 1.002));  //向上取整
                multipleSeriesRenderer.setYAxisMin(Math.floor(min - Math.abs(min) * 0.002));
            } else if (min < multipleSeriesRenderer.getYAxisMin()) {
                multipleSeriesRenderer.setYAxisMax(Math.ceil(max * 1.002));
                multipleSeriesRenderer.setYAxisMin(Math.floor(min - Math.abs(min) * 0.002));
            }
            for (int i = 0; i < chaneelNum; i++) {
                multipleSeriesDataset.getSeriesAt(i).add(x, y[i]);
            }
        } else if (shiftStatus == 4) {    //后退
            int lastIndex = multipleSeriesDataset.getSeriesAt(0).getItemCount() - 1;
            if (lastIndex==-1){
                return;
            }else if (lastIndex==0){
                isFrist=true;
                for(int i=0;i<chaneelNum;i++){
                    multipleSeriesDataset.getSeriesAt(i).remove(lastIndex);
                }
                refreshChart();
                return;
            }
            double xEnd = multipleSeriesDataset.getSeriesAt(0).getX(lastIndex);
            if (xEnd > 1.0) {
                multipleSeriesRenderer.setXAxisMin(xEnd - 1.0);
                multipleSeriesRenderer.setXAxisMax(xEnd);
            }
            for (int i = 0; i < chaneelNum; i++) {
                multipleSeriesDataset.getSeriesAt(i).remove(lastIndex);
            }
        }
        yMax=multipleSeriesRenderer.getYAxisMax();
        yMin=multipleSeriesRenderer.getYAxisMin();
    }

    public void refreshChart(){
        if (!viewFlag) {
            graphicalView.repaint();
        } else {
            graphicalView_1.repaint();
        }
    }

    public void repaintDetectionChart(){
        graphicalView.repaint();
    }

    //画地磁场校准图表的方法
    public void drawDetectionChart(List<Double> xList, List<ArrayList<Double>> yList){
        for(int i=0;i<chaneelNum;i++){
            multipleSeriesDataset.getSeriesAt(i).clear();
        }
        int size=xList.size();                  //取所有list中size最小的size
        for(int i=0;i<yList.size();i++){
            if(size>yList.get(i).size())
                size=yList.get(i).size();
        }
        //向点集添加数据
        double min=10000,max=-10000;
        for (int j = 0; j < size ; j++) {
            for (int i=0;i<chaneelNum;i++) {
                if(min>yList.get(i).get(j)){
                    min=yList.get(i).get(j);
                }
                if(max<yList.get(i).get(j)){
                    max=yList.get(i).get(j);
                }
                multipleSeriesDataset.getSeriesAt(i).add(xList.get(j), yList.get(i).get(j));
            }
        }
        yMin=min - Math.abs(min) * 0.002;
        yMax=max + Math.abs(max) * 0.002;
        resetXYAxis();

        multipleSeriesRenderer.setXAxisMin(0);
        multipleSeriesRenderer.setXAxisMax(xList.get(xList.size()-1));

        repaintDetectionChart();
    }

    //清空图表
    public void clearDetectionChart() {
        multipleSeriesRenderer.setChartTitle("");
        initXY();
        for (int i=0;i<chaneelNum;i++){
            multipleSeriesDataset.getSeriesAt(i).clear();
            multipleSeriesDataset.getSeriesAt(i).add(0,0);
        }
        graphicalView.repaint();
    }

    //测量过程中画图的方法
    public void drawMeasureChart(List<Double> xList, List<ArrayList<Double>> yList){
        for(int i=0;i<chaneelNum;i++){
            multipleSeriesDataset.getSeriesAt(i).clear();
        }
        int size=xList.size();                  //取所有list中size最小的size
        for(int i=0;i<yList.size();i++){
            if(size>yList.get(i).size())
                size=yList.get(i).size();
        }
        //向点集添加数据
        double min=10000,max=-10000;
        for (int j = 0; j < size ; j++) {
            for (int i=0;i<chaneelNum;i++) {
                if(min>yList.get(i).get(j)){
                    min=yList.get(i).get(j);
                }
                if(max<yList.get(i).get(j)){
                    max=yList.get(i).get(j);
                }
                multipleSeriesDataset.getSeriesAt(i).add(xList.get(j), yList.get(i).get(j));
            }
        }
        yMin=min - Math.abs(min) * 0.002;
        yMax=max + Math.abs(max) * 0.002;
        resetXYAxis();

        multipleSeriesRenderer.setXAxisMin(0);
        multipleSeriesRenderer.setXAxisMax(xList.get(xList.size()-1));

        refreshChart();
    }

    //画历史数据图表
    public void drawHistoryChart(List<Double> xList, List<ArrayList<Double>> yList, String title) {
        for(int i=0;i<chaneelNum;i++){
            multipleSeriesDataset.getSeriesAt(i).clear();
        }
        //添加标题
        multipleSeriesRenderer.setChartTitle(title);
        multipleSeriesRenderer.setChartTitleTextSize(40);
        int size=xList.size();                  //取所有list中size最小的size
        for(int i=0;i<yList.size();i++){
            if(size>yList.get(i).size())
                size=yList.get(i).size();
        }
        //向点集添加数据
        double min=10000,max=-10000;
        for (int j = 0; j < size ; j++) {
            for (int i=0;i<chaneelNum;i++) {
                if(min>yList.get(i).get(j)){
                    min=yList.get(i).get(j);
                }
                if(max<yList.get(i).get(j)){
                    max=yList.get(i).get(j);
                }
                multipleSeriesDataset.getSeriesAt(i).add(xList.get(j), yList.get(i).get(j));
            }
        }
        yMin=min - Math.abs(min) * 0.002;
        yMax=max + Math.abs(max) * 0.002;
        resetXYAxis();

        Log.d("xList.size()",String.valueOf(xList.size()));
        multipleSeriesRenderer.setXAxisMin(0);
        multipleSeriesRenderer.setXAxisMax(xList.get(xList.size()-1));

        refreshChart();
    }

    public void resetXYAxis(){
        multipleSeriesRenderer.setYAxisMax(yMax);
        multipleSeriesRenderer.setYAxisMin(yMin);
        if (multipleSeriesDataset.getSeriesAt(0).getMaxX()<0.1){
            multipleSeriesRenderer.setXAxisMax(0.1);
        }else if (multipleSeriesDataset.getSeriesAt(0).getMaxX()<0.2){
            multipleSeriesRenderer.setXAxisMax(0.2);
        }else{
            multipleSeriesRenderer.setXAxisMax(multipleSeriesDataset.getSeriesAt(0).getMaxX());
        }
        multipleSeriesRenderer.setXAxisMin(0);
//        multipleSeriesRenderer.setXAxisMax(multipleSeriesDataset.getSeriesAt(0).getMaxX());
        graphicalView.repaint();
        if (graphicalView_1!=null){
            graphicalView_1.repaint();
        }
    }

    public void setWarningLine(double warningValues){
        this.warningLine=warningValues;
        if (chartIndex==2){
            mRenderer = new XYSeriesRenderer();      //增加警戒线的渲染器
            mRenderer.setColor(Color.RED);
            mRenderer.setPointStyle(PointStyle.POINT);
            mRenderer.setLineWidth(4f);
            multipleSeriesRenderer.addSeriesRenderer(mRenderer);
            mSeries =new XYSeries("Warning");          //数据集
            mSeries.add(0,warningValues);
            multipleSeriesDataset.addSeries(mSeries);
        }
    }

}