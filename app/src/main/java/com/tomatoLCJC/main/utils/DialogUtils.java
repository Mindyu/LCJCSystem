package com.tomatoLCJC.main.utils;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomatoLCJC.main.R;
import com.tomatoLCJC.tools.Parameter.SystemParameter;

/**
 * Created by YCQ on 2017/9/26.
 */

public class DialogUtils {

    private Context context;
    private Dialog dialog;
    private boolean allChaneel;
    private TextView selectChaneel;
    private ImageView imageView;
    private boolean[][] chaneelSelect;

    public DialogUtils(Context context,TextView selectChaneel,ImageView imageView,boolean[][] chaneelSelect){
        this.context=context;
        this.selectChaneel=selectChaneel;
        this.imageView=imageView;
        this.chaneelSelect=chaneelSelect;
        this.allChaneel=false;
    }

    public void showDialog(){
        dialog = new Dialog(context, R.style.common_dialog);
        //对话框布局
        View inflate = LayoutInflater.from(context).inflate(R.layout.select_channel_dialog, null);
        //初始化控件
        final ImageButton allChaneelButton= (ImageButton) inflate.findViewById(R.id.all_chaneel);
        final ImageButton chaneel_1= (ImageButton) inflate.findViewById(R.id.chaneel_1);
        final ImageButton chaneel_2= (ImageButton) inflate.findViewById(R.id.chaneel_2);
        final ImageButton chaneel_3= (ImageButton) inflate.findViewById(R.id.chaneel_3);
        final ImageButton chaneel_4= (ImageButton) inflate.findViewById(R.id.chaneel_4);
        final ImageButton chaneel_5= (ImageButton) inflate.findViewById(R.id.chaneel_5);
        final ImageButton chaneel_6= (ImageButton) inflate.findViewById(R.id.chaneel_6);
        final ImageButton chaneel_7= (ImageButton) inflate.findViewById(R.id.chaneel_7);
        final ImageButton chaneel_8= (ImageButton) inflate.findViewById(R.id.chaneel_8);
        final ImageButton chaneel_9= (ImageButton) inflate.findViewById(R.id.chaneel_9);
        final ImageButton chaneel_10= (ImageButton) inflate.findViewById(R.id.chaneel_10);
        final ImageButton chaneel_11= (ImageButton) inflate.findViewById(R.id.chaneel_11);
        final ImageButton chaneel_12= (ImageButton) inflate.findViewById(R.id.chaneel_12);
        final ImageButton chaneel_13= (ImageButton) inflate.findViewById(R.id.chaneel_13);
        final ImageButton chaneel_14= (ImageButton) inflate.findViewById(R.id.chaneel_14);
        final ImageButton chaneel_15= (ImageButton) inflate.findViewById(R.id.chaneel_15);
        //根据通道数初始化显示的按钮数
        switch (SystemParameter.getInstance().nChannelNumber){
            case 15:
                chaneel_15.setVisibility(View.VISIBLE);
            case 14:
                chaneel_14.setVisibility(View.VISIBLE);
            case 13:
                chaneel_13.setVisibility(View.VISIBLE);
            case 12:
                chaneel_12.setVisibility(View.VISIBLE);
            case 11:
                chaneel_11.setVisibility(View.VISIBLE);
            case 10:
                chaneel_10.setVisibility(View.VISIBLE);
            case 9:
                chaneel_9.setVisibility(View.VISIBLE);
            case 8:
                chaneel_8.setVisibility(View.VISIBLE);
            case 7:
                chaneel_7.setVisibility(View.VISIBLE);
            case 6:
                chaneel_6.setVisibility(View.VISIBLE);
            case 5:
                chaneel_5.setVisibility(View.VISIBLE);
            case 4:
                chaneel_4.setVisibility(View.VISIBLE);
            case 3:
                chaneel_3.setVisibility(View.VISIBLE);
            case 2:
                chaneel_2.setVisibility(View.VISIBLE);
            case 1:
                chaneel_1.setVisibility(View.VISIBLE);
        }
        //将布局设置给 dialog
        dialog.setContentView(inflate);
        //获取当前 Activity 所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置 dialog 从底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        dialog.show();
        for (int i=0;i<15;i++){
            chaneelSelect[i/5][i%5]=false;
            allChaneel=false;
        }
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                imageView.setImageResource(R.drawable.strigon1);
                boolean flag=false;
                //未选择通道
                for (int i=0;i<SystemParameter.getInstance().nChannelNumber;i++){
                    if (chaneelSelect[i/5][i%5]) {
                        flag=true;
                    }
                }
                if (!flag){   //未选择通道，并且原文本为空
                    selectChaneel.setText("");
                }else if(allChaneel){
                    selectChaneel.setText("全部通道");
                }else{
                    selectChaneel.setText("");
                    String chaneelStr="通道";
                    for (int i=0;i<SystemParameter.getInstance().nChannelNumber;i++){
                        if (chaneelSelect[i/5][i%5]) {
                            chaneelStr +=(Integer.toString(i+1)+",");
                        }
                    }
                    chaneelStr=chaneelStr.substring(0,chaneelStr.length()-1);
                    selectChaneel.setText(chaneelStr);
                }
            }
        });

        allChaneelButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (!allChaneel){
                    allChaneel=true;
                    allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel_selected));
                    for (int i=0;i<SystemParameter.getInstance().nChannelNumber;i++){
                        chaneelSelect[i/5][i%5]=true;
                    }
                    switch (SystemParameter.getInstance().nChannelNumber){
                        case 15:
                            chaneel_15.setImageDrawable(context.getDrawable(R.drawable.channel_15_selected));
                        case 14:
                            chaneel_14.setImageDrawable(context.getDrawable(R.drawable.channel_14_selected));
                        case 13:
                            chaneel_13.setImageDrawable(context.getDrawable(R.drawable.channel_13_selected));
                        case 12:
                            chaneel_12.setImageDrawable(context.getDrawable(R.drawable.channel_12_selected));
                        case 11:
                            chaneel_11.setImageDrawable(context.getDrawable(R.drawable.channel_11_selected));
                        case 10:
                            chaneel_10.setImageDrawable(context.getDrawable(R.drawable.channel_10_selected));
                        case 9:
                            chaneel_9.setImageDrawable(context.getDrawable(R.drawable.channel_9_selected));
                        case 8:
                            chaneel_8.setImageDrawable(context.getDrawable(R.drawable.channel_8_selected));
                        case 7:
                            chaneel_7.setImageDrawable(context.getDrawable(R.drawable.channel_7_selected));
                        case 6:
                            chaneel_6.setImageDrawable(context.getDrawable(R.drawable.channel_6_selected));
                        case 5:
                            chaneel_5.setImageDrawable(context.getDrawable(R.drawable.channel_5_selected));
                        case 4:
                            chaneel_4.setImageDrawable(context.getDrawable(R.drawable.channel_4_selected));
                        case 3:
                            chaneel_3.setImageDrawable(context.getDrawable(R.drawable.channel_3_selected));
                        case 2:
                            chaneel_2.setImageDrawable(context.getDrawable(R.drawable.channel_2_selected));
                        case 1:
                            chaneel_1.setImageDrawable(context.getDrawable(R.drawable.channel_1_selected));
                    }

                }else{
                    allChaneel=false;
                    allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    for (int i=0;i<SystemParameter.getInstance().nChannelNumber;i++){
                        chaneelSelect[i/5][i%5]=false;
                    }
                    switch (SystemParameter.getInstance().nChannelNumber){
                        case 15:
                            chaneel_15.setImageDrawable(context.getDrawable(R.drawable.channel_15));
                        case 14:
                            chaneel_14.setImageDrawable(context.getDrawable(R.drawable.channel_14));
                        case 13:
                            chaneel_13.setImageDrawable(context.getDrawable(R.drawable.channel_13));
                        case 12:
                            chaneel_12.setImageDrawable(context.getDrawable(R.drawable.channel_12));
                        case 11:
                            chaneel_11.setImageDrawable(context.getDrawable(R.drawable.channel_11));
                        case 10:
                            chaneel_10.setImageDrawable(context.getDrawable(R.drawable.channel_10));
                        case 9:
                            chaneel_9.setImageDrawable(context.getDrawable(R.drawable.channel_9));
                        case 8:
                            chaneel_8.setImageDrawable(context.getDrawable(R.drawable.channel_8));
                        case 7:
                            chaneel_7.setImageDrawable(context.getDrawable(R.drawable.channel_7));
                        case 6:
                            chaneel_6.setImageDrawable(context.getDrawable(R.drawable.channel_6));
                        case 5:
                            chaneel_5.setImageDrawable(context.getDrawable(R.drawable.channel_5));
                        case 4:
                            chaneel_4.setImageDrawable(context.getDrawable(R.drawable.channel_4));
                        case 3:
                            chaneel_3.setImageDrawable(context.getDrawable(R.drawable.channel_3));
                        case 2:
                            chaneel_2.setImageDrawable(context.getDrawable(R.drawable.channel_2));
                        case 1:
                            chaneel_1.setImageDrawable(context.getDrawable(R.drawable.channel_1));
                    }
                }
            }
        });

        chaneel_1.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[0][0]){
                    chaneelSelect[0][0]=true;
                    chaneel_1.setImageDrawable(context.getDrawable(R.drawable.channel_1_selected));
                }else {
                    chaneelSelect[0][0]=false;
                    chaneel_1.setImageDrawable(context.getDrawable(R.drawable.channel_1));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_2.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[0][1]){
                    chaneelSelect[0][1]=true;
                    chaneel_2.setImageDrawable(context.getDrawable(R.drawable.channel_2_selected));
                }else {
                    chaneelSelect[0][1]=false;
                    chaneel_2.setImageDrawable(context.getDrawable(R.drawable.channel_2));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_3.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[0][2]){
                    chaneelSelect[0][2]=true;
                    chaneel_3.setImageDrawable(context.getDrawable(R.drawable.channel_3_selected));
                }else {
                    chaneelSelect[0][2]=false;
                    chaneel_3.setImageDrawable(context.getDrawable(R.drawable.channel_3));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_4.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[0][3]){
                    chaneelSelect[0][3]=true;
                    chaneel_4.setImageDrawable(context.getDrawable(R.drawable.channel_4_selected));
                }else {
                    chaneelSelect[0][3]=false;
                    chaneel_4.setImageDrawable(context.getDrawable(R.drawable.channel_4));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_5.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[0][4]){
                    chaneelSelect[0][4]=true;
                    chaneel_5.setImageDrawable(context.getDrawable(R.drawable.channel_5_selected));
                }else {
                    chaneelSelect[0][4]=false;
                    chaneel_5.setImageDrawable(context.getDrawable(R.drawable.channel_5));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_6.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[1][0]){
                    chaneelSelect[1][0]=true;
                    chaneel_6.setImageDrawable(context.getDrawable(R.drawable.channel_6_selected));
                }else {
                    chaneelSelect[1][0]=false;
                    chaneel_6.setImageDrawable(context.getDrawable(R.drawable.channel_6));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_7.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[1][1]){
                    chaneelSelect[1][1]=true;
                    chaneel_7.setImageDrawable(context.getDrawable(R.drawable.channel_7_selected));
                }else {
                    chaneelSelect[1][1]=false;
                    chaneel_7.setImageDrawable(context.getDrawable(R.drawable.channel_7));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_8.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[1][2]){
                    chaneelSelect[1][2]=true;
                    chaneel_8.setImageDrawable(context.getDrawable(R.drawable.channel_8_selected));
                }else {
                    chaneelSelect[1][2]=false;
                    chaneel_8.setImageDrawable(context.getDrawable(R.drawable.channel_8));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_9.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[1][3]){
                    chaneelSelect[1][3]=true;
                    chaneel_9.setImageDrawable(context.getDrawable(R.drawable.channel_9_selected));
                }else {
                    chaneelSelect[1][3]=false;
                    chaneel_9.setImageDrawable(context.getDrawable(R.drawable.channel_9));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_10.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[1][4]){
                    chaneelSelect[1][4]=true;
                    chaneel_10.setImageDrawable(context.getDrawable(R.drawable.channel_10_selected));
                }else {
                    chaneelSelect[1][4]=false;
                    chaneel_10.setImageDrawable(context.getDrawable(R.drawable.channel_10));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_11.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[2][0]){
                    chaneelSelect[2][0]=true;
                    chaneel_11.setImageDrawable(context.getDrawable(R.drawable.channel_11_selected));
                }else {
                    chaneelSelect[2][0]=false;
                    chaneel_11.setImageDrawable(context.getDrawable(R.drawable.channel_11));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_12.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[2][1]){
                    chaneelSelect[2][1]=true;
                    chaneel_12.setImageDrawable(context.getDrawable(R.drawable.channel_12_selected));
                }else {
                    chaneelSelect[2][1]=false;
                    chaneel_12.setImageDrawable(context.getDrawable(R.drawable.channel_12));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_13.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[2][2]){
                    chaneelSelect[2][2]=true;
                    chaneel_13.setImageDrawable(context.getDrawable(R.drawable.channel_13_selected));
                }else {
                    chaneelSelect[2][2]=false;
                    chaneel_13.setImageDrawable(context.getDrawable(R.drawable.channel_13));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_14.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[2][3]){
                    chaneelSelect[2][3]=true;
                    chaneel_14.setImageDrawable(context.getDrawable(R.drawable.channel_14_selected));
                }else {
                    chaneelSelect[2][3]=false;
                    chaneel_14.setImageDrawable(context.getDrawable(R.drawable.channel_14));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });

        chaneel_15.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(!chaneelSelect[2][4]){
                    chaneelSelect[2][4]=true;
                    chaneel_15.setImageDrawable(context.getDrawable(R.drawable.channel_15_selected));
                }else {
                    chaneelSelect[2][4]=false;
                    chaneel_15.setImageDrawable(context.getDrawable(R.drawable.channel_15));
                    if (allChaneel){
                        allChaneel=false;
                        allChaneelButton.setImageDrawable(context.getDrawable(R.drawable.all_channel));
                    }
                }
            }
        });
    }

}
