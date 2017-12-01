package com.tomatoLCJC.tools.Parameter;

import android.graphics.Color;

import com.tomatoLCJC.main.MyApplication;
import com.tomatoLCJC.main.R;

/**
 * Created by YCQ on 2017/11/25.
 */

public class GlobalParameter {

    private String[] lineColors;
    private int[] colors = new int[16];
    private static GlobalParameter globalParameter;

    public GlobalParameter(){
        lineColors= MyApplication.CONTEXT.getResources().getStringArray(R.array.lineColors);
        for (int i=0;i<16;i++){
            colors[i] = Color.parseColor(lineColors[i]);
        }
    }

    public static GlobalParameter getInstance(){
        if (globalParameter==null){
            globalParameter = new GlobalParameter();
        }
        return globalParameter;
    }


    public int[] getColors(){
        return colors;
    }

}
