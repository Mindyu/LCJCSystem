package com.tomatoLCJC.tools.Chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.tomatoLCJC.main.utils.Point;
import com.tomatoLCJC.tools.Parameter.GlobalParameter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YCQ on 2017/11/25.
 */

public class LineChart extends AbstractChartService {

    private List<Point> points;     //校准曲线的四个点（只有校准的时候会用到）

    public LineChart(Context context) {
        super(context);

        xDistance = (float) 0.2;
        yDistance = 300;

        initPaints();   // 初始化画笔
    }

    public LineChart(Context context, List<Double> xs, List<ArrayList<Double>> ys) {
        super(context);
        for(int i=0;i<ys.size();i++){
            yList.add(new ArrayList<Float>());
        }
        Double2Float(xs, ys, null);  // 将 double 转化为 float
        xStart = 0;
        if (xList.size() != 0){
            yStart = -yStart;   //y方向上的最小值
            if (xList.get(xList.size() - 1) < 0.2)
                xDistance = (float) 0.2;
            else
                xDistance = (xList.get(xList.size() - 1) - xList.get(0));
            yDistance += yStart;     ///y方向上的距离
        }else {
            yStart = 0;   //y方向上的最小值
            xDistance = (float) 0.2;
            yDistance = 300;     ///y方向上的距离
        }

        initPaints();   // 初始化画笔
    }

    public void drawView(List<Double> xs, List<ArrayList<Double>> ys){
        xList.clear();yList.clear();showIDs.clear();
        for(int i=0;i<ys.size();i++){
            showIDs.add(i);
        }
        for(int i=0;i<ys.size();i++){
            yList.add(new ArrayList<Float>());
        }
        Double2Float(xs, ys, null);  // 将 double 转化为 float
        xStart = 0;
        if (xList.size() != 0){
            yStart = -yStart;   //y方向上的最小值
            if (xList.get(xList.size() - 1) < 0.2)
                xDistance = (float) 0.2;
            else
                xDistance = (xList.get(xList.size() - 1) - xList.get(0));
            yDistance += yStart;     ///y方向上的距离
        }else {
            yStart = 0;   //y方向上的最小值
            xDistance = (float) 0.2;
            yDistance = 300;     ///y方向上的距离
        }
        initPaints();
    }

    public void drawCalibrationView(List<Double> xs, List<ArrayList<Double>> ys, List<Point> points){
        drawView(xs, ys);
        this.points = points;
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
        canvas.translate(xTranslate, 0);                           // 使坐标跟着图形一起平移

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
        canvas.save();  // 保存画布状态
        canvas.clipRect(-110f, -canvasHeight, 0, 0);                        // 切割画布，使坐标显示在一定范围内
        canvas.translate(0, -(1 - yScale) / 2 * canvasHeight);              // 缩放时平移使得与折线图一致
        canvas.translate(0, (yTranslate/yScale-yStart/yDistance*canvasHeight)*yScale);      // 使坐标跟着图形一起平移
        interval = measureInterval(yDistance/yScale);                       //每组值得间隔
//        Log.d("Chart","interval="+interval);
        n = (int) Math.ceil((yDistance/yScale)/interval);                   //一组有几个值
//        Log.d("Chart","n="+n);
        first = -(int) Math.floor((yStart+yDistance/yScale/2*(1-yScale)-yTranslate/yScale/canvasHeight*yDistance)/interval);
//        Log.d("Chart","first="+first);
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
            canvas.drawLine(-10f, -(canvasHeight * (first+i) * interval / yDistance * yScale),
                    0f, -(canvasHeight * (first+i) * interval / yDistance * yScale),paint);
            canvas.drawText(
                    number,
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
        canvas.translate(xTranslate/xScale, yTranslate/yScale-yStart/yDistance*canvasHeight);
        float px = (canvasWidth / 2 - xTranslate/xScale);
        float py = (canvasHeight / 2 + yTranslate/yScale-yStart/yDistance*canvasHeight);
        canvas.scale(xScale, yScale, px, -py);               //以图的中心点缩放

        paint.setDither(true);                           //设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        paint.setFilterBitmap(true);                     //如果该项设置为true，则图像在动画进行中会滤掉对Bitmap图像的优化操作，加快显示速度，本设置项依赖于dither和xfermode的设置
        paint.setStyle(Paint.Style.FILL_AND_STROKE);     //设置画笔的样式，Style.FILL: 实心   STROKE:空心   FILL_OR_STROKE:同时实心与空心
        paint.setStrokeJoin(Paint.Join.ROUND);           //设置绘制时各图形的结合方式，如平滑效果等  BEVEL斜角
        //paint.setStrokeWidth(4/xScale);                  //当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的粗细度
        int[] colors = GlobalParameter.getInstance().getColors();
        // 绘制图形
        for (int i=0; i<showIDs.size(); i++) {           //通道数
            for (int j=0; j< yList.get(showIDs.get(i)).size()-1; j++) {
                paint.setColor(colors[showIDs.get(i)]);

                if ((yList.get(showIDs.get(i)).get(j+1)-yList.get(showIDs.get(i)).get(j))<10&&(yList.get(showIDs.get(i)).get(j+1)-yList.get(showIDs.get(i)).get(j))>-10){
                    paint.setStrokeWidth(4/yScale);
                }else {
                    paint.setStrokeWidth(3/xScale);
                }
                canvas.drawLine(        //xDistance x方向的总距离
                        xList.get(j) / xDistance * canvasWidth,
                        -yList.get(showIDs.get(i)).get(j) / yDistance * canvasHeight,
                        xList.get(j+1) / xDistance * canvasWidth,
                        -yList.get(showIDs.get(i)).get(j+1) / yDistance * canvasHeight,
                        paint
                );
            }
        }
        if (points!=null){
            paint.setColor(Color.RED);
            for (int i=0;i<points.size()-1;i++){
                canvas.drawLine(        //xDistance x方向的总距离
                        points.get(i).getX() / xDistance * canvasWidth,
                        -points.get(i).getY() / yDistance * canvasHeight,
                        points.get(i+1).getX() / xDistance * canvasWidth,
                        -points.get(i+1).getY() / yDistance * canvasHeight,
                        paint
                );
            }
        }
        initPaints();   //画笔reset
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.d("Scanning","执行onDraw");

        canvasWidth = canvas.getWidth();    // 得到画布宽度
        canvasHeight = canvas.getHeight();  // 得到画布高度
        Log.d("Chart","canvasWidth="+canvasWidth+"   canvasHeight="+canvasHeight);

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
        yStart = 10000f; yDistance = -1000f;
        for (int i = 0; i < xs.size(); i++) {
            xList.add(xs.get(i).floatValue());
        }
        for (int i = 0; i < ys.size(); i++) {
            for (int j = 0; j < ys.get(i).size(); j++) {
                yList.get(i).add(ys.get(i).get(j).floatValue());
                yStart = ys.get(i).get(j).floatValue()<yStart ? ys.get(i).get(j).floatValue():yStart;
                yDistance = ys.get(i).get(j).floatValue()>yDistance ? ys.get(i).get(j).floatValue():yDistance;
            }
        }
    }

    @Override
    public void setLineRemove(int line) {
        for (int i = 0;i<showIDs.size();i++){
            if (line==showIDs.get(i)){
                showIDs.remove(i);
                invalidate();  //重绘
            }
        }
    }

    @Override
    public void setLineRecover(int line) {
        if (showIDs.size()<1) return;
        if (showIDs.get(showIDs.size()-1)<line){
            showIDs.add(line);
        }else if(showIDs.get(0)>line){
            showIDs.add(0,line);
        }else {
            for (int i = 0;i<showIDs.size()-1;i++){
                if (showIDs.get(i)<line&&showIDs.get(i+1)>line){
                    showIDs.add(i+1,line);
                }
            }
        }
        invalidate();  //重绘
    }

    @Override
    public void cleanChart() {
        xDistance = (float) 0.2;
        yDistance = 300;

        initPaints();   // 初始化画笔
        xList.clear();yList.clear();showIDs.clear();
    }

}
