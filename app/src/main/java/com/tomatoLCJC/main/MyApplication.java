package com.tomatoLCJC.main;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Created by 雪 on 2017/4/9.
 */
public class MyApplication extends Application {
    //public static String MagName;
    public static Context CONTEXT;
    /*public void setMagID(String magID){
        this.MagName = magID;
    }
    public String getMagID(){
        return MagName;
    }*/
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
        //TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        //MagName = tm.getDeviceId();
    }
}
