package com.tomatoLCJC.main.utils;

import com.tomatoLCJC.tools.dataBase.Bean.DeviceDetectionRecordBean;

/**
 * Created by YCQ on 2017/9/16.
 */

public class Quadratic {

    double a,b,c;

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public Quadratic() {
        a = 1; b = 1; c = 0;
    }

    public Quadratic(double da, double db, double dc) {
        a = da; b = db; c = dc;
    }

    public Quadratic(Quadratic qd) {
        a = qd.a; b = qd.b; c = qd.c;
    }

    public Quadratic(Point v1, Point v2, Point v3) {
        double det = -(v3.x - v2.x)*(v2.x - v1.x)*(v3.x - v1.x);
        if (det==0)
        {
            a = 0; b = 0; c = 0;	//错误信息a!=0;
            return ;
        }
        double da, db, dc;
        da = (v1.y-v3.y)*v2.x + (v3.y-v2.y)*v1.x + (v2.y-v1.y)*v3.x;
        db = (v1.y - v2.y)*v3.x*v3.x + (v2.y - v3.y)*v1.x*v1.x + (v3.y - v1.y)*v2.x*v2.x;
        a = da / det;
        b = db / det;
        c = v1.y - a*v1.x*v1.x - b*v1.x;
    }

    public void setParameter(double da, double db, double dc){
        a = da; b = db; c = dc;
    }

    //已知三个点，计算abc
    public void Fitting(Point v1, Point v2, Point v3) {
        double det = -(v3.x - v2.x)*(v2.x - v1.x)*(v3.x - v1.x);
        if (det==0)
        {
            a = 0; b = 0; c = 0;	//错误信息a!=0;
            return ;
        }
        double da, db, dc;
        da = (v1.y-v3.y)*v2.x + (v3.y-v2.y)*v1.x + (v2.y-v1.y)*v3.x;
        db = (v1.y - v2.y)*v3.x*v3.x + (v2.y - v3.y)*v1.x*v1.x + (v3.y - v1.y)*v2.x*v2.x;
        a = da / det;
        b = db / det;
        c = v1.y - a*v1.x*v1.x - b*v1.x;
    }


    //已知因变量y(dependent),求自变量x（argument）
    public double depTOarg( double y) {
        double dt = b*b - 4 * a*(c-y);
        if (dt < 0){
            return -1;
        }
        double x1 = (-b + (double)Math.sqrt((double)dt)) / (2 * a);
        double x2 = (-b - (double)Math.sqrt((double)dt)) / (2 * a);	//x1>x2
        if (x1 >= 0)	return x1;
        if (x2 >= 0)	return x2;
        return -1;
    }

    //已知自变量x（argument）,求因变量y(dependent)
    public double argTOdep( double x)
    {
        return a*x*x + b*x + c;
    }

}
