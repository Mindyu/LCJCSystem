package com.tomatoLCJC.main.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tomatoLCJC.main.R;

/**
 * Created by YCQ on 2017/11/4.
 */

public class MainDialog extends Dialog implements View.OnClickListener{

    private Context mContext;
    private ImageButton goonCalibration;
    private ImageButton finishCalibration;
    private ImageButton cancleCalibration;
    private TextView textView;
    private String text;

    private mainDialgClickListener listener;

    public interface mainDialgClickListener{
        void goonClick();
        void finishClick();
        void cancleClick();
    }

    //接口回调机制，实现组件的事件
    public void setMainDialgClickListener(mainDialgClickListener listener){
        this.listener=listener;
    }

    public MainDialog(@NonNull Context context, String text) {
        super(context, R.style.common_dialog);
        this.mContext = context;
        this.text = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dialog);
        setCanceledOnTouchOutside(false);
        initView();
    }

    public void initView(){
        goonCalibration = (ImageButton) findViewById(R.id.goon_calibration);
        finishCalibration = (ImageButton) findViewById(R.id.finish_calibration);
        cancleCalibration = (ImageButton) findViewById(R.id.cancle_calibration);
        textView = (TextView) findViewById(R.id.dialog_text);
        textView.setText(text);
        goonCalibration.setOnClickListener(this);
        finishCalibration.setOnClickListener(this);
        cancleCalibration.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.goon_calibration:
                listener.goonClick();
                break;
            case R.id.finish_calibration:
                listener.finishClick();
                break;
            case R.id.cancle_calibration:
                listener.cancleClick();
                break;
        }
        this.dismiss();
    }
}
