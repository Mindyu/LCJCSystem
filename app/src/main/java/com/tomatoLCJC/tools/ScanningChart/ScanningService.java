package com.tomatoLCJC.tools.ScanningChart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tomatoLCJC.main.fragment.FragmentDataMeasure;
import com.tomatoLCJC.tools.Parameter.SystemParameter;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * 扫描图服务类。用于提供生成扫描图的一系列方法和接口。
 */

@SuppressLint("ViewConstructor")
public class ScanningService extends View implements View.OnTouchListener {

    private long upTime = 0;                        // 手指抬起时的时间，用于控制双击事件
    private double xDown = 0, yDown = 0;            // 平移时，手指刚按下时的坐标
    private double lenDown = 1;                     // 缩放时，两根手指刚按下时之间的距离
    public double xTranslate = 0, yTranslate = 0;  // 分别控制 x 和 y 方向的平移距离
    private float xScale = 1, yScale = 1;           // 分别控制 x 和 y 方向的缩放程度

    List<Float> xList = new ArrayList<>();             // 检测图中矩形左上角的坐标
    List<ArrayList<Float>> yList = new ArrayList<>();  // 检测图中矩形左上角的坐标
    List<ArrayList<Float>> zList = new ArrayList<>();  // 决定矩形的颜色

    float stepDistance;            // 步长
    float probeDistance;           // 探头间距
    int probeNumber;               // 探头个数
    private float xDistance;       // x 方向总距离
    private float yDistance;       // y 方向总距离
    private float xStart, yStart;  // 起始点坐标

    private Context context = null;
    private float canvasWidth, canvasHeight;  // 画布宽度和高度
    private boolean flag;                     // 决定是否开始绘制扫描图
    private Paint paint;                      // 画笔

    /**
     * 构造方法。完成对画笔的初始化和手势检测的初始化。
     * */
    public ScanningService(Context context, List<Double> xs, List<ArrayList<Double>> ys,
                           List<ArrayList<Double>> zs, boolean flag) {
        super(context);
        this.context = context;
        this.flag = flag;   // 判断是否绘制扫描图
        for(int i=0;i<SystemParameter.getInstance().nChannelNumber;i++){
            this.yList.add(new ArrayList<Float>());
            this.zList.add(new ArrayList<Float>());
        }
        Double2Float(xs, ys, zs);  // 将 double 转化为 float
        stepDistance = (float) SystemParameter.getInstance().disSensorStepLen;  // 步长
        probeDistance = (float) SystemParameter.getInstance().nChannelDistance; // 探头间距
        probeNumber = SystemParameter.getInstance().nChannelNumber;             // 探头个数
        xDistance = (xList.get(xList.size() - 1) - xList.get(0));
        yDistance = (probeDistance * probeNumber);
        xStart = xList.get(0);
        yStart = yList.get(0).get(0);

        this.setOnTouchListener(this);  // 设置手势监听

        initPaints();   // 初始化画笔
    }

    public ScanningService(Context context, boolean flag) {
        super(context);
        this.flag = flag;   // 判断是否绘制扫描图

        this.setOnTouchListener(this);  // 设置手势监听
        paint = new Paint();            // 初始化画笔
        paint.setTextSize(20);          // 设置画笔写字时字体的大小
        paint.setAntiAlias(true);       // 开启抗锯齿

        xDistance = (float) 0.2;
        yDistance = 6;
        stepDistance = (float) SystemParameter.getInstance().disSensorStepLen;  // 步长
        probeDistance = (float) SystemParameter.getInstance().nChannelDistance; // 探头间距
        probeNumber = SystemParameter.getInstance().nChannelNumber;             // 探头个数

        initPaints();   // 初始化画笔
    }

    /**
     * 初始化 paint
     * */
    private void initPaints() {
        paint = new Paint();                    // 初始化画笔
        paint.setTextSize(30);                  // 设置画笔写字时字体的大小
        paint.setTextAlign(Paint.Align.RIGHT);  // 设置对齐方向
        paint.setAntiAlias(true);               // 开启抗锯齿
    }

    /**
     * 画图方法。所有画图的步骤均在此方法中实现。
     * */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvasWidth = canvas.getWidth();    // 得到画布宽度
        canvasHeight = canvas.getHeight();  // 得到画布高度

        // 绘制坐标轴
        drawAxis(canvas);
        // 绘制刻度
        drawCoordinates(canvas);
        if (flag && xList.size() > 0) {
            // 绘制扫描图
            drawScanningChart(canvas);
        }
        // 实时更新
        invalidate();
    }

    /**
     * 绘制坐标轴
     * */
    private void drawAxis(Canvas canvas) {
        canvas.translate(110f, 40f);     // 使画布向 x 轴正向移动 110f，向 y 轴移动 40f
        canvas.drawLine(0, 0, canvasWidth, 0, paint);   // 绘制 x 轴
        canvas.drawLine(0, 0, 0, canvasHeight, paint);  // 绘制 y 轴
        canvasWidth -= 140f;
        canvasHeight -= 40f;
    }

    /**
     * 绘制刻度
     * */
    private void drawCoordinates(Canvas canvas) {
        // 绘制 x 轴坐标
        canvas.save();  // 保存画布状态
        canvas.clipRect(0, -40f, canvasWidth, 0);       // 切割画布，使坐标显示在一定范围内
        canvas.translate((float) xTranslate, 0);        // 使坐标跟着图形一起平移
        int n = 1;
        if (xDistance < 1)
            n = 1;
        else if (xDistance > 1 && xDistance <=10)
            n = 10;
        else if (xDistance > 10)
            n = 100;
        for (int i = -100; i < xDistance * 200; i++) {
            if (i % (5 * n) == 0) {
                canvas.drawText(
                        String.valueOf((float) Math.round(i) / 100),
                        canvasWidth * i / 100 / xDistance * xScale,
                        -10f,
                        paint);
            }
        }
        canvas.restore();   // 使画布返回上一个状态

        // 绘制 y 轴坐标
        canvas.save();  // 保存画布状态
        canvas.clipRect(-60f, 0, 0, canvasHeight);      // 切割画布，使坐标显示在一定范围内
        canvas.translate(0, (float) yTranslate);        // 使坐标跟着图形一起平移
        for (int i = 0; i < 20; i++) {
            canvas.drawText(
                    String.valueOf(i),
                    -5f,
                    canvasHeight * i / yDistance * yScale,
                    paint);
        }
        canvas.restore();   // 使画布返回上一个状态
    }

    /**
     * 绘制扫描图
     * */
    private void drawScanningChart(Canvas canvas) {
        // 裁切矩形，把画面控制在坐标平面内
        canvas.clipRect(0, 0, canvasWidth, canvasHeight);
        // 手势缩放移动
        canvas.translate((float) xTranslate, (float) yTranslate);
        canvas.scale(xScale, yScale);
        // 绘制底色
        paint.setColor(Color.rgb(190, 130, 110));
        canvas.drawRect(0, 0, canvasWidth, canvasHeight, paint);
        // 绘制图形
        for (int i=0; i<yList.size(); i++) {
            for (int j=0; j<xList.size(); j++) {
                // 只有当缺陷程度大于 10 时才绘制，不然以底色填充
                if (zList.get(i).get(j) >= 10) {
                    // 控制颜色
                    if (zList.get(i).get(j) < 20 && zList.get(i).get(j) >= 10)
                        paint.setColor(Color.rgb(243, 160, 38));
                    else if (zList.get(i).get(j) < 30 && zList.get(i).get(j) >= 20)
                        paint.setColor(Color.rgb(226, 244, 41));
                    else if (zList.get(i).get(j) < 40 && zList.get(i).get(j) >= 30)
                        paint.setColor(Color.rgb(34, 227, 44));
                    else if (zList.get(i).get(j) < 50 && zList.get(i).get(j) <= 40)
                        paint.setColor(Color.rgb(53, 132, 235));
                    else if (zList.get(i).get(j) < 60 && zList.get(i).get(j) <= 50)
                        paint.setColor(Color.rgb(5, 53, 126));
                    else if (zList.get(i).get(j) < 70 && zList.get(i).get(j) <= 60)
                        paint.setColor(Color.rgb(131, 0, 99));
                    else if (zList.get(i).get(j) < 80 && zList.get(i).get(j) <= 70)
                        paint.setColor(Color.rgb(251, 48, 64));
                    else
                        paint.setColor(Color.rgb(208, 3, 81));

                    canvas.drawRect(
                            (xList.get(j) - xStart) / xDistance * canvasWidth,
                            (yList.get(i).get(j) - yStart) / yDistance * canvasHeight,
                            (xList.get(j) - xStart + xDistance / (xList.size() - 1)) / xDistance * canvasWidth,
                            (yList.get(i).get(j) - yStart + probeDistance) / yDistance * canvasHeight,
                            paint
                    );
                }
                paint.setColor(Color.BLACK);
            }
        }
    }

    /**
     * 手势检测。只要有手指按上屏幕就会触发该方法，并返回一个或连续返回多个 MotionEvent，
     * 通过 event.getAction() 可以得到 MotionEvent，根据得到的 MotionEvent 自动判断
     * 执行 switch - case 中的哪一段代码。
     * */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            // 一根手指按下时
            case MotionEvent.ACTION_DOWN:
                // 双击扫描图跳转到大图
                if (event.getEventTime() - upTime < 200) {
                    if (context != null) {
                        FragmentDataMeasure.getInstance().bigScanChart(
                                FragmentDataMeasure.getInstance().scanView);
                    }
                }
                xDown = event.getX();
                yDown = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                upTime = event.getEventTime();
                break;
            // 手指移动时
            case MotionEvent.ACTION_MOVE:
                // 只有一根手指移动时
                if (event.getPointerCount() == 1 && event.getAction() != 261
                        && xDown != 0 && yDown != 0 ) {
                    // 实现图形平移
                    xTranslate += event.getX() - xDown;
                    xDown = event.getX();
                    yTranslate += event.getY() - yDown;
                    yDown = event.getY();
                    // 实现折线图平移
                    // 计算水平移动的坐标长度
                    double xTrans = xTranslate / canvasWidth * xDistance;
                    // 更新折线图的 x 轴的最小和最大坐标
                    FragmentDataMeasure.getInstance().mService.getMultipleSeriesRenderer()
                            .setXAxisMin(-xTrans);
                    FragmentDataMeasure.getInstance().mService.getMultipleSeriesRenderer()
                            .setXAxisMax(xDistance - xTrans);
                    // 重绘折线图
                    FragmentDataMeasure.getInstance().mGraphicalView.repaint();
                }
                // 有两根手指移动时
                else if (event.getPointerCount() == 2) {
                    // 实现图形缩放
                    double xLenMove = Math.abs(event.getX(0) - event.getX(1));
                    double yLenMove = Math.abs(event.getY(0) - event.getY(1));
                    double lenMove = Math.sqrt(xLenMove * xLenMove + yLenMove * yLenMove);
                    if (lenMove > lenDown) {
                        if (xScale > 2) {
                            break;
                        }
                        xScale += (float) (lenMove / lenDown - 1);
                        yScale += (float) (lenMove / lenDown - 1);
                        lenDown = lenMove;
                    } else if (lenMove < lenDown){
                        if (xScale < 0.65) {
                            break;
                        }
                        xScale -= (float) (1 - lenMove / lenDown);
                        yScale -= (float) (1 - lenMove / lenDown);
                        lenDown = lenMove;
                    }
                    xDown = 0;
                    yDown = 0;
                }
                break;
            // 有两根手指按下时
            case 261:
                double xLenDown = Math.abs(event.getX(0) - event.getX(1));
                double yLenDown = Math.abs(event.getY(0) - event.getY(1));
                lenDown = Math.sqrt(xLenDown * xLenDown + yLenDown * yLenDown);
                break;
            // 两根手指中的一根抬起时
            case MotionEvent.ACTION_POINTER_UP:
                xDown = 0;
                yDown = 0;
                break;
        }
        return false;
    }

    // 首页折线图的大图平移后，扫描图也要相应平移
    public void xTransform(double x, float width) {
        xTranslate += width * x / xDistance;
    }

    public float getCanvasWidth() {
        return canvasWidth;
    }

    /**
     * 将 double 数组转化为 float 数组
     * */
    private void Double2Float(List<Double> xs, List<ArrayList<Double>> ys,
                              List<ArrayList<Double>> zs) {
        String xStr, yStr, zStr;
        for (int i=0; i<xs.size(); i++) {
            xStr = xs.get(i).toString();
            this.xList.add(Float.parseFloat(xStr));
        }
        for (int i=0; i<ys.size(); i++) {
            for (int j=0; j<ys.get(i).size(); j++) {
                yStr = ys.get(i).get(j).toString();
                this.yList.get(i).add(Float.parseFloat(yStr));
                zStr = zs.get(i).get(j).toString();
                this.zList.get(i).add(Float.parseFloat(zStr));
            }
        }
    }

}
