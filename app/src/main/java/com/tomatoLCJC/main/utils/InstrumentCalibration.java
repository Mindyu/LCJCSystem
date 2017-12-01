package com.tomatoLCJC.main.utils;

import android.util.Log;

import com.tomatoLCJC.tools.Parameter.SystemParameter;
import com.tomatoLCJC.tools.Parameter.ThreadParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YCQ on 2017/10/10.
 */

public class InstrumentCalibration {

    private static double range = 0.1;     //左右寻找20%的区域  通过修改这个确定精确度

    /*******************************************************************************************
     * 函数名称：getMaximumValue
     * 函数介绍：获取曲线的极大值, 原理是:已知输入曲线有4个峰,该算法的思想就是求出这每个峰的起始x坐标和结束x坐标（即求出峰所在的区间）
     然后在该区间中搜最大值从而求出极大值。
     * 输入参数：order 表示第几次获取极大值
     * 输出参数：无
     * 返回值  ：无
     ********************************************************************************************/
    public static List<Point> getMaximumValue(List<Double> yValue)          //获取极大值
    {
        List<Point> maxValue=new ArrayList<>();
	    double key = 10;           //设置标记值为10（即认为从某个大于60的点开始到某个小于60的点结束为峰的区间）也可以为其他值，根据需要来定
        int end = yValue.size();
        List<Integer> xValue=new ArrayList<>();        //记录所有峰的起始坐标和结束坐标（就是记录所有峰的区间）
        int flag = 0;                 //信号量   因为算法要求记录峰值的起始和结束坐标，当某一点大于标记值key以后因为峰的单调递增性会有很多点也大于可以
        //所以用flag标记当获取了第一个大于标记值key以后让程序不再记录x坐标直到条件再次满足
        for (int i = 0; i < end; ++i)     //这是搜索峰的区间部分
        {
            if (key < yValue.get(i) && 0 == flag)
            {
                xValue.add(i);
                flag = 1;
            }
            if (key > yValue.get(i) && 1 == flag)
            {
                xValue.add(i);
                flag = 0;
            }
        }
        xValue.add(end - 1);
        int length = xValue.size();
        for (int i = 0; i < length / 2; ++i)      //根据区间搜索极大值
        {
            double temp = yValue.get(xValue.get(2*i));
            int k = xValue.get(2*i) + 1;
            for (int j = k; j < xValue.get(2*i+1); ++j)
            {
                if (temp < yValue.get(j))
                {
                    temp = yValue.get(j);
                    k = j;
                }

            }
            maxValue.add(new Point(k,Float.valueOf(String.valueOf(temp))));
        }
        Collections.sort(maxValue);          //根据Y值 从大到小排序
        if (maxValue.size()<4) {
            return maxValue;    //极大值个数小于4，在后续过程会滤掉该情况，直接返回
        } else {
//            return adjustOrder(maxValue);  //第一种校准方法
            return adjustOrder2(maxValue);  //第二种校准方法
        }
    }

    //第一种调整方法，根据递增和递减顺序
    public static List<Point> adjustOrder(List<Point> value ){
        List<Point> resValue=new ArrayList<>();
        resValue.add(value.get(0));             //滤掉可能出现的错误
        double flag_x = value.get(0).getX();
        int flag_pOm = value.get(1).getX()-value.get(0).getX() > 0 ? 1:-1;    //设置正序标记
        //剔除x非正序的Point
        for (int i=1;i<value.size();i++){
            if ((value.get(i).getX()-flag_x > 0 ? 1:-1)==flag_pOm){
                resValue.add(value.get(i));
                flag_x = value.get(i).getX();
            }
        }
        resValue = new ArrayList<>(resValue.subList(0 , Math.min(4,resValue.size()))); //size>4时仅获取前四个值，
        return resValue;
    }

    //第二种调整方法，根据递增和递减顺序
    public static List<Point> adjustOrder2(List<Point> value ){     //x为步数，y为去噪值
        Map<Integer,Float> map = new HashMap<>();
        for (int i= 0;i<value.size();i++){
            map.put((int) value.get(i).getX(), value.get(i).getY());
        }
        List<Point> resValue=new ArrayList<>();
        for(int i=0;i<4;i++)    resValue.add(new Point(0.0f, 0.0f));  //初始化4个point
        int interval; //间隔
        for (int i=0 ; i<value.size()-4 ; i++){
            resValue.set(0,value.get(i));       //循环以此向后寻找一个最优的缺陷  80%
            for(int j=i+1;j<value.size()-3; j++){
                resValue.set(1,value.get(j));           //60%
                interval = (int) (resValue.get(1).getX()-resValue.get(0).getX());    //80与60之间的间距（步数）
                for (int k=0;k<Math.abs((int)(interval*range));k++){
                    if (map.containsKey((int)(resValue.get(1).getX()+interval+k)) && map.get((int)(resValue.get(1).getX()+interval+k)) < resValue.get(1).getY()){
                        resValue.set(2,new Point((resValue.get(1).getX()+interval+k), map.get((int)(resValue.get(1).getX()+interval+k))));
                        int interval1= (((int) (resValue.get(2).getX()-resValue.get(1).getX()))+interval)/2;
                        for (int m=0;m<Math.abs((int)(interval1*range));m++){
                            if (map.containsKey((int)(resValue.get(2).getX()+interval1+m)) && map.get((int)(resValue.get(2).getX()+interval1+m)) < resValue.get(2).getY()){
                                resValue.set(3,new Point((resValue.get(2).getX()+interval1+m), map.get((int)(resValue.get(2).getX()+interval1+m))));
                                return resValue;
                            }
                            if (map.containsKey((int)(resValue.get(2).getX()+interval1-m)) && map.get((int)(resValue.get(2).getX()+interval1-m)) < resValue.get(2).getY()){
                                resValue.set(3,new Point((resValue.get(2).getX()+interval1-m), map.get((int)(resValue.get(2).getX()+interval1-m))));
                                return resValue;
                            }
                        }
                    }
                    if (map.containsKey((int)(resValue.get(1).getX()+interval-k)) && map.get((int)(resValue.get(1).getX()+interval-k)) < resValue.get(1).getY() ){
                        resValue.set(2,new Point((resValue.get(1).getX()+interval-k), map.get((int)(resValue.get(1).getX()+interval-k))));
                        int interval1= (((int) (resValue.get(2).getX()-resValue.get(1).getX()))+interval)/2;
                        for (int m=0;m<Math.abs((int)(interval1*range));m++){
                            if (map.containsKey((int)(resValue.get(2).getX()+interval1+m)) && map.get((int)(resValue.get(2).getX()+interval1+m)) < resValue.get(2).getY()){
                                resValue.set(3,new Point((resValue.get(2).getX()+interval1+m), map.get((int)(resValue.get(2).getX()+interval1+m))));
                                return resValue;
                            }
                            if (map.containsKey((int)(resValue.get(2).getX()+interval1-m)) && map.get((int)(resValue.get(2).getX()+interval1-m)) < resValue.get(2).getY()){
                                resValue.set(3,new Point((resValue.get(2).getX()+interval1-m), map.get((int)(resValue.get(2).getX()+interval1-m))));
                                return resValue;
                            }
                        }
                    }
                }

            }
        }
        return new ArrayList<>();
    }

    //list逆序
    public static List<Point> invertedOrder(List<Point> value ){
        List<Point> resValue = new ArrayList<>();
        for (int i=1;i <= value.size();i++){       //从小到大排列，逆序填充
            resValue.add(new Point( value.get(value.size()-i).getX()*(float) SystemParameter.getInstance().disSensorStepLen/1000 , value.get(value.size()-i).getY() ));
        }
        return resValue;
    }

}
