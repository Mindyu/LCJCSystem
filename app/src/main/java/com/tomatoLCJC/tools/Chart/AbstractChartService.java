package com.tomatoLCJC.tools.Chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YCQ on 2017/11/25.
 */

public abstract class AbstractChartService extends View {

    protected long upTime = 0;                        // 手指抬起时的时间，用于控制双击事件
    protected float xDown = 0, yDown = 0;             // 平移时，手指刚按下时的坐标
    protected double lenDown = 1;                     // 缩放时，两根手指刚按下时之间的距离
    protected float xTranslate = 0, yTranslate = 0;   // 分别控制 x 和 y 方向的平移距离
    protected float xScale = 1, yScale = 1;           // 分别控制 x 和 y 方向的缩放程度

    protected ArrayList<Integer> showIDs = new ArrayList<>();    //所需显示的曲线下标
    protected List<Float> xList = new ArrayList<>();             // 检测图中矩形左上角的坐标
    protected List<ArrayList<Float>> yList = new ArrayList<>();  // 检测图中矩形左上角的坐标

    protected float xDistance;       // x 方向总距离
    protected float yDistance;       // y 方向总距离
    protected float xStart=0, yStart=0;  // 起始点坐标

    protected float canvasWidth, canvasHeight;  // 画布宽度和高度
    protected Paint paint;                      // 画笔


    public AbstractChartService(Context context) {
        super(context);
    }
    public abstract void initPaints();                      //初始化画笔
    public abstract void drawAxis(Canvas canvas);           //初始化坐标轴
    public abstract void drawCoordinates(Canvas canvas);    //绘制刻度
    public abstract void drawAction(Canvas canvas);
    public abstract void Double2Float(List<Double> xs, List<ArrayList<Double>> ys,List<ArrayList<Double>> zs);
    public abstract void setLineRemove(int line);
    public abstract void setLineRecover(int line);
    public abstract void cleanChart();

    public void resetAxis(){
        xTranslate = 0; yTranslate = 0;   // 分别控制 x 和 y 方向的平移距离
        xScale = 1; yScale = 1;           // 分别控制 x 和 y 方向的缩放程度
        invalidate();  //完成时重绘
    }
    public float measureInterval(float temp){
        float res = 1;
        temp /= 5;
        if (temp<0.01){
            res = (float) Math.ceil(temp*1000)/1000;
        }else if (temp<0.1){
            res = (float) Math.ceil(temp*100)/100;
        }else if (temp<0.5){
            res = (float) Math.ceil(temp*10)/10;
        }else if (temp<1){
            res = 1;
        }else if (temp<5){
            res = (float) Math.ceil(temp*10);
        }else if (temp<10){
            res = 10;
        }else if (temp<50){
            res = (float) Math.ceil(temp/10)*10;
        }else if (temp<100){
            res = 100;
        }else if (temp<500){
            res = (float) Math.ceil(temp/100)*100;
        }else if (temp<1000){
            res = 1000;
        }else if (temp<5000){
            res = (float) Math.ceil(temp/1000)*1000;
        }
        return res;
    }

    public float getxTranslate() {
        return xTranslate;
    }
    public void setxTranslate(float xTranslate) {
        this.xTranslate = xTranslate;
    }
    public float getyTranslate() {
        return yTranslate;
    }
    public void setyTranslate(float yTranslate) {
        this.yTranslate = yTranslate;
    }
    public float getxScale() {
        return xScale;
    }
    public void setxScale(float xScale) {
        this.xScale = xScale;
    }
    public float getyScale() {
        return yScale;
    }
    public void setyScale(float yScale) {
        this.yScale = yScale;
    }
    public float getxDistance() {
        return xDistance;
    }
    public void setxDistance(float xDistance) {
        this.xDistance = xDistance;
    }
    public float getyDistance() {
        return yDistance;
    }
    public void setyDistance(float yDistance) {
        this.yDistance = yDistance;
    }
    public float getxStart() {
        return xStart;
    }
    public void setxStart(float xStart) {
        this.xStart = xStart;
    }
    public float getyStart() {
        return yStart;
    }
    public void setyStart(float yStart) {
        this.yStart = yStart;
    }
    public long getUpTime() {
        return upTime;
    }
    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }
    public float getxDown() {
        return xDown;
    }
    public void setxDown(float xDown) {
        this.xDown = xDown;
    }
    public float getyDown() {
        return yDown;
    }
    public void setyDown(float yDown) {
        this.yDown = yDown;
    }
    public double getLenDown() {
        return lenDown;
    }
    public void setLenDown(double lenDown) {
        this.lenDown = lenDown;
    }
    public float getCanvasWidth() {
        return canvasWidth;
    }
    public void setCanvasWidth(float canvasWidth) {
        this.canvasWidth = canvasWidth;
    }
    public float getCanvasHeight() {
        return canvasHeight;
    }
    public void setCanvasHeight(float canvasHeight) {
        this.canvasHeight = canvasHeight;
    }
}
