package com.tomatoLCJC.main.utils;

import android.support.annotation.NonNull;

/**
 * Created by YCQ on 2017/9/16.
 */

public class Point implements Comparable<Point>{

    float x;
    float y;

    public Point(float x,float y){
        this.x=x;
        this.y=y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return " x = "+x+", y = "+y+"\n";
    }


    @Override
    public int compareTo(@NonNull Point o) {
        if (this.y>o.y){           //从大到小排列
            return -1;
        }
        return 1;
    }
}
