package com.tomatoLCJC.tools.Chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.tomatoLCJC.tools.Parameter.SystemParameter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YCQ on 2017/11/25.
 */

public class ScanningChart extends AbstractChartService {

    //特有的属性
    private List<ArrayList<Float>> zList = new ArrayList<>();  // 缺陷等级
    private float probeDistance;

    public ScanningChart(Context context) {
        super(context);

        xDistance = (float) 0.2;
        yDistance = 6;

        probeDistance = (float) SystemParameter.getInstance().nChannelDistance; // 探头间距

        initPaints();   // 初始化画笔
    }

    public ScanningChart(Context context, List<Double> xs, List<ArrayList<Double>> ys, List<ArrayList<Double>> zs) {
        super(context);
        for (int i = 0; i < ys.size(); i++) {
            yList.add(new ArrayList<Float>());
            zList.add(new ArrayList<Float>());
        }
        probeDistance = (float) SystemParameter.getInstance().nChannelDistance; // 探头间距
        Double2Float(xs, ys, zs);  // 将 double 转化为 float
        if (xList.get(xList.size() - 1) < 0.2)
            xDistance = (float) 0.2;
        else
            xDistance = (xList.get(xList.size() - 1) - xList.get(0));
        yDistance = (probeDistance * ys.size());    ///y方向上的距离
        xStart = xList.get(0);
        yStart = yList.get(0).get(0);      //y方向上的最小值

        initPaints();   // 初始化画笔
    }

    public void drawView(List<Double> xs, List<ArrayList<Double>> ys, List<ArrayList<Double>> zs){
        xList.clear();yList.clear();yList.clear();
        for (int i = 0; i < ys.size(); i++) {
            yList.add(new ArrayList<Float>());
            zList.add(new ArrayList<Float>());
        }
        probeDistance = (float) SystemParameter.getInstance().nChannelDistance; // 探头间距
        Double2Float(xs, ys, zs);                           // 将 double 转化为 float
        if (xList.get(xList.size() - 1) < 0.2)
            xDistance = (float) 0.2;
        else
            xDistance = (xList.get(xList.size() - 1) - xList.get(0));
        yDistance = (probeDistance * ys.size());            ///y方向上的距离
        xStart = xList.get(0);
        yStart = yList.get(0).get(0);                       //y方向上的最小值

        initPaints();                                       // 初始化画笔
    }

    @Override
    public void initPaints() {
        if (paint == null) {
            paint = new Paint();                    // 初始化画笔
        } else {
            paint.reset();
        }
        paint.setTextSize(40);                  // 设置画笔写字时字体的大小
        paint.setTextAlign(Paint.Align.RIGHT);  // 设置对齐方向
        paint.setAntiAlias(true);               // 设置是否使用抗锯齿功能，会消耗较大资源，绘制图形速度会变慢。
    }

    @Override
    public void drawAxis(Canvas canvas) {
        canvasWidth -= 140f;        //留有边距
        canvasHeight -= 140f;
        canvas.translate(110f, canvasHeight + 40f);     // 使画布向 x 轴正向移动 110f，向 y 轴移动 40f
        canvas.drawLine(0, 0, canvasWidth, 0, paint);   // 绘制 x 轴
        canvas.drawLine(0, 0, 0,-canvasHeight, paint);  // 绘制 y 轴
    }

    @Override
    public void drawCoordinates(Canvas canvas) {
        String number;
        DecimalFormat df1 = new DecimalFormat("0.0");
        DecimalFormat df2 = new DecimalFormat("0.00");
        DecimalFormat df3 = new DecimalFormat("0.000");
        // 绘制 x 轴坐标
        canvas.save();  // 保存画布状态
        canvas.clipRect(0, 0, canvasWidth, 60f);                   // 切割画布，使坐标显示在一定范围内
        canvas.translate((1 - xScale) / 2 * canvasWidth, 0);       // 缩放时，平移使得与折线图一致
        canvas.translate(xTranslate, 0);                  // 使坐标跟着图形一起平移

        float interval = measureInterval(xDistance/xScale);        //每组值得间隔
        int n = (int) Math.ceil((xDistance/xScale)/interval);      //一组有几个值
        int first = (int) Math.floor((xStart-((1-xScale)/2+xTranslate/canvasWidth)*xDistance/xScale)/interval);
        for (int i = 0; i <= n; i++){
            if (interval < 0.01){
                number = df3.format((first+i)*interval);
            }else if (interval <= 0.1){
                number = df2.format((first+i)*interval);
            }else if (interval < 1){
                number = df1.format((first+i)*interval);
            }else {
                number = String.valueOf((first+i)*(int)interval);
            }
            canvas.drawLine((canvasWidth * (first+i) * interval / xDistance * xScale), 0f,
                    (canvasWidth * (first+i) * interval / xDistance * xScale), 10f,paint);
            canvas.drawText(
                    number,
                    (canvasWidth * (first+i) * interval / xDistance * xScale),
                    40f,
                    paint);
        }
        canvas.restore();   // 使画布返回上一个状态

        // 绘制 y 轴坐标
        canvas.save();                                                   // 保存画布状态
        canvas.clipRect(-110f, -canvasHeight, 0, 0);                     // 切割画布，使坐标显示在一定范围内
        canvas.translate(0, -(1 - yScale) / 2 * canvasHeight);           // 缩放时平移使得与折线图一致
        canvas.translate(0, yTranslate);                                 // 使坐标跟着图形一起平移
        interval = 1;                                                    // 相邻值的间隔
        n = (int) Math.ceil((yDistance/yScale)/interval);                // 一组有几个值
        first = - Math.round((yDistance/yScale/2*(1-yScale)-yTranslate/yScale/canvasHeight*yDistance));
        for (int i = 0; i <n; i++) {
            canvas.drawLine(-10f, -(canvasHeight * (first+i) * interval / yDistance * yScale),
                    0f, -(canvasHeight * (first+i) * interval / yDistance * yScale),paint);
            canvas.drawText(
                    String.valueOf(first+i),
                    -5f,
                    -(canvasHeight * (first+i) * interval / yDistance * yScale)-4,
                    paint);
        }

        canvas.restore();   // 使画布返回上一个状态
    }

    @Override
    public void drawAction(Canvas canvas) {
        // 裁切矩形，把画面控制在坐标平面内
        canvas.clipRect(0, 0, canvasWidth, -canvasHeight);
        // 手势缩放移动
        canvas.translate(xTranslate/xScale, yTranslate/yScale);
        float px = (canvasWidth / 2 - xTranslate/xScale);
        float py = (canvasHeight / 2 + yTranslate/yScale);
        canvas.scale(xScale, yScale, px, -py);               //以图的中心点缩放
        // 绘制底色
        paint.setColor(Color.rgb(203, 149, 128));
        if (xList.get(xList.size() - 1) < 0.2)
            canvas.drawRect(0, 0, xList.get(xList.size() - 1) / (float) 0.2 * canvasWidth, -canvasHeight, paint);
        else
            canvas.drawRect(0, 0, canvasWidth, -canvasHeight, paint);
        // 绘制图形
        for (int i = 0; i < zList.size(); i++) {           //通道数
            int index = zList.get(i).size()<xList.size()?zList.get(i).size():xList.size();
            for (int j = 0; j < index; j++) {
                // 只有当缺陷程度大于 10 时才绘制，不然以底色填充
                if (zList.get(i).get(j) >= 10) {
                    // 控制颜色
                    if (zList.get(i).get(j) < 20 && zList.get(i).get(j) >= 10)
                        paint.setColor(Color.rgb(247, 175, 49));
                    else if (zList.get(i).get(j) < 30 && zList.get(i).get(j) >= 20)
                        paint.setColor(Color.rgb(231, 242, 53));
                    else if (zList.get(i).get(j) < 40 && zList.get(i).get(j) >= 30)
                        paint.setColor(Color.rgb(19, 228, 58));
                    else if (zList.get(i).get(j) < 50 && zList.get(i).get(j) >= 40)
                        paint.setColor(Color.rgb(66, 152, 239));
                    else if (zList.get(i).get(j) < 60 && zList.get(i).get(j) >= 50)
                        paint.setColor(Color.rgb(0, 72, 144));
                    else if (zList.get(i).get(j) < 70 && zList.get(i).get(j) >= 60)
                        paint.setColor(Color.rgb(151, 26, 228));
                    else if (zList.get(i).get(j) < 80 && zList.get(i).get(j) >= 70)
                        paint.setColor(Color.rgb(255, 75, 81));
                    else
                        paint.setColor(Color.rgb(215, 34, 41));

                    canvas.drawRect(                    //xDistance x方向的总距离
                            (xList.get(j) - xStart) / xDistance * canvasWidth,
                            -(yList.get(i).get(j) - yStart) / yDistance * canvasHeight,
                            (xList.get(j) - xStart + xDistance / (xList.size() - 1)) / xDistance * canvasWidth,
                            -(yList.get(i).get(j) - yStart + probeDistance) / yDistance * canvasHeight,
                            paint
                    );
                }
                paint.setColor(Color.BLACK);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("Scanning","执行onDraw");

        canvasWidth = canvas.getWidth();    // 得到画布宽度
        canvasHeight = canvas.getHeight();  // 得到画布高度

        // 绘制坐标轴
        drawAxis(canvas);
        // 绘制刻度
        drawCoordinates(canvas);
        if (xList.size() > 0) {
            drawAction(canvas);
        }
    }

    @Override
    public void Double2Float(List<Double> xs, List<ArrayList<Double>> ys, List<ArrayList<Double>> zs) {
        for (int i = 0; i < xs.size(); i++) {
            xList.add(xs.get(i).floatValue());
        }
        for (int i = 0; i < ys.size(); i++) {
            for (int j = 0; j < ys.get(i).size(); j++) {
                yList.get(i).add(ys.get(i).get(j).floatValue());
                zList.get(i).add(zs.get(i).get(j).floatValue());
            }
        }
    }

    @Override
    public void setLineRemove(int line) {

    }

    @Override
    public void setLineRecover(int line) {

    }

    @Override
    public void cleanChart() {
        xDistance = (float) 0.2;
        yDistance = 6;

        probeDistance = (float) SystemParameter.getInstance().nChannelDistance; // 探头间距

        initPaints();   // 初始化画笔
        xList.clear();yList.clear();zList.clear();showIDs.clear();
    }

}
