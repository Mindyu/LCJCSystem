package com.tomatoLCJC.main.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by mij on 2017/10/14.
 */

public class HoriScrollView extends HorizontalScrollView {
    private View mView;  //

    public HoriScrollView(Context context) {
        super(context);
    }

    public HoriScrollView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // l oldl 分别代表水平位移
        // t oldt 代表当前左上角距离Scrollview顶点的距离
        super.onScrollChanged(l, t, oldl, oldt);
        if( mView != null ){
            mView.scrollTo(l,t);//参数为偏移量
        }
    }

    public void setScrollView(View view){
        mView = view;
    }
}
