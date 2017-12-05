package com.tomatoLCJC.main;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Created by 雪 on 2017/4/9.
 */
public class MyApplication extends Application {
    public static Context CONTEXT;

    public void setContext(Context context){
        this.CONTEXT = context;
    }
    public Context getContext(){
        return CONTEXT;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        CONTEXT = getApplicationContext();
    }
}
