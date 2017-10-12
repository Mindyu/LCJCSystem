package com.tomatoLCJC.main.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by YCQ on 2017/10/10.
 */

public class InstrumentCalibration {




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
        Collections.sort(maxValue);
        return maxValue;
    }
}
