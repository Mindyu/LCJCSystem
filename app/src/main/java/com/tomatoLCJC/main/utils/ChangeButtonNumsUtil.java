package com.tomatoLCJC.main.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tomatoLCJC.main.R;
import com.tomatoLCJC.tools.chart.ChartService;


/**
 * Created by zch22 on 2017/5/15.
 * //这个工具类用于每隔Fragment中动态添加按钮组，并为其设置点击事件
 */
public class ChangeButtonNumsUtil {
    private ChartService chartService;
    private ChartService chartService1;
    private int channleCount;   //通道数
    private Context context;    //当前activity
    private LinearLayout ChannelButtonLayout;//相应Fragment中的通道按钮布局
    //设置成成员变量是为了在Fragment与Dialog间状态能保留下来
    private boolean[] ifClicked;//设置channleCount个布尔值，每个用来表示每个通道按钮选中与未选中两种状态
    private boolean[][] ifSelected;//每个flag用来表示每个通道按钮选中与未选中两种状态
    private boolean ifChooseAllSeleted;//是否选了全选

    //小图片，只有c1-c7和弹出框按钮
    private final int smallNormalDrawable[] = {R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4, R.drawable.c5, R.drawable.c6, R.drawable.c7, R.drawable.omission};//在Fragment点击后的小图片资源
    private final int smallNoColorDrawable[] = {R.drawable.c1_normal, R.drawable.c2_normal, R.drawable.c3_normal, R.drawable.c4_normal, R.drawable.c5_normal, R.drawable.c6_normal, R.drawable.c7_normal};//在Fragment未点击的小图片资源
    //大图片 c1-c15 这里的c1-c7全都要改为大的
    private final int[][] bigNormalDrawable = {{R.drawable.b_c1, R.drawable.b_c2, R.drawable.b_c3, R.drawable.b_c4, R.drawable.b_c5},
            {R.drawable.b_c6, R.drawable.b_c7, R.drawable.b_c8, R.drawable.b_c9, R.drawable.b_c10},
            {R.drawable.b_c11, R.drawable.b_c12, R.drawable.b_c13, R.drawable.b_c14, R.drawable.b_c15}};
    private final int[][] bigNoColorDrawable = {{R.drawable.b_c1_normal, R.drawable.b_c2_normal, R.drawable.b_c3_normal, R.drawable.b_c4_normal, R.drawable.b_c5_normal},
            {R.drawable.b_c6_normal, R.drawable.b_c7_normal, R.drawable.b_c8_normal, R.drawable.b_c9_normal, R.drawable.b_c10_normal},
            {R.drawable.b_c11_normal, R.drawable.b_c12_normal, R.drawable.b_c13_normal, R.drawable.b_c14_normal, R.drawable.b_c15_normal}};
    private final int[][] id = {{0, 1, 2, 3, 4}, {5, 6, 7, 8, 9}, {10, 11, 12, 13, 14}};//id一样的所以可以同时改变


    public ChangeButtonNumsUtil(int channleCount,  LinearLayout channelGroupLayout, Context context, ChartService chartService, ChartService chartService1) {
        this.chartService = chartService;
        this.chartService1 = chartService1;
        this.channleCount = channleCount;
        this.ChannelButtonLayout = channelGroupLayout;
        this.context = context;
        ifClicked = new boolean[channleCount];
        for (int i = 0; i < channleCount; i++) {
            ifClicked[i] = false;
        }
        //标志全都初始化为false
        ifChooseAllSeleted = false;
        ifSelected = new boolean[3][5];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 5; j++) {
                ifSelected[i][j] = false;
            }
    }

    public void setLineToTransparent(int index) {
        chartService.setLineTransparent(index);
        if (chartService1!=null) {
            chartService1.setLineTransparent(index);
        }
    }


    public void setLineResume(int index) {
        chartService.setLineRecover(index);
        if (chartService1!=null) {
            chartService1.setLineRecover(index);
        }
    }

    public int getChannleSelectCount() {
        int rs = 0;
        for (int i = 0; i < channleCount; i++) {
            if (!ifSelected[i / 5][i % 5]) {
                rs++;
            }
        }
        return rs;
    }

    /*
     * 根据通道数来改变显示通道按钮的数目并为每个按钮添加事件监听
     */
    public void initChannelButtons() {
        int channleCountTemp = 0;
        if (channleCount > 7) {
            channleCountTemp = 8;
        } else {
            channleCountTemp = channleCount;
        }
        ChannelButtonLayout.removeAllViews();

        for (int i = 0; i < channleCountTemp; i++) {
            final ImageButton imageButton = new ImageButton(context);
            imageButton.setId(i);
            imageButton.setImageResource(smallNormalDrawable[i]);
            imageButton.setBackgroundColor(context.getResources().getColor(R.color.channelBtnBack));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            imageButton.setLayoutParams(lp); //设置每个按钮权重为1
            ChannelButtonLayout.addView(imageButton);
            //为每个按钮设置点击事件，主要是改变背景图片，将对应通道的那条线变为背景色与还原
            imageButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int flag = v.getId();
                    if (flag < 7) {
                        ImageButton imageButton = (ImageButton) ChannelButtonLayout.findViewById(flag);
                        if (ifClicked[flag]) {
                            ifClicked[flag] = false;
                            ifSelected[flag / 5][flag % 5] = false;
                            imageButton.setImageResource(smallNormalDrawable[flag]);
                            setLineResume(flag);
                        } else {
                            if (getChannleSelectCount() <= 1) {
                                Toast.makeText(context, "请至少选择一个通道", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            ifClicked[flag] = true;
                            ifSelected[flag / 5][flag % 5] = true;
                            imageButton.setImageResource(smallNoColorDrawable[flag]);
                            setLineToTransparent(flag);
                        }
                    } else {
                        showDialog();
                    }
                }
            });
        }
    }


    //显示Dialog弹出框
    private void showDialog() {
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_view, null);
        final Dialog dialog = new Dialog(context, R.style.common_dialog);  // 设置style 控制默认dialog带来的边距问题

        ImageButton btnSure = (ImageButton) view.findViewById(R.id.btn_sure);
        final ImageView btnChooseAll = (ImageView) view.findViewById(R.id.btn_chooseall);
        LinearLayout chooseall_layout = (LinearLayout) view.findViewById(R.id.chooseall_layout);
        //由于最多15个按钮这里只用了三个布局
        final LinearLayout[] linearLayouts = {(LinearLayout) view.findViewById(R.id.channelButtonGroupLayout1), (LinearLayout) view.findViewById(R.id.channelButtonGroupLayout2), (LinearLayout) view.findViewById(R.id.channelButtonGroupLayout3)};

        int layoutNum = 0;//需要用到的布局数
        int[] buttons = new int[3];//每个布局里的按钮数目
        if (channleCount <= 5) {
            layoutNum = 1;
            buttons[0] = channleCount;
        } else if (channleCount <= 10) {
            layoutNum = 2;
            buttons[0] = 5;
            buttons[1] = channleCount - 5;
        } else {
            layoutNum = 3;
            buttons[0] = 5;
            buttons[1] = 5;
            buttons[2] = channleCount - 10;
        }
        //向Dialog中添加按钮并添加点击事件
        for (int i = 0; i < layoutNum; i++) {
            for (int j = 0; j < buttons[i]; j++) {
                final ImageButton imageButton = new ImageButton(context);
                imageButton.setId(id[i][j]);//为按钮设置id
                if (ifSelected[i][j]) {//为true说明在Fragment中是点击状态（默认是有颜色的）
                    imageButton.setImageResource(bigNoColorDrawable[i][j]);//背景设为白色
                    btnChooseAll.setImageResource(R.drawable.select_box);//初始有一个没选，全选按钮就为空白背景
                    ifChooseAllSeleted = true;
                } else {
                    imageButton.setImageResource(bigNormalDrawable[i][j]);//背景设为彩色
                }
                imageButton.setBackgroundColor(context.getResources().getColor(R.color.channelBtnBackInDialog));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                imageButton.setLayoutParams(lp); //设置每个按钮权重为1
                linearLayouts[i].addView(imageButton);
                //为每个按钮设置点击事件，主要是改变背景图片和点击状态，将对应通道的那条线变为背景色与还原
                imageButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int flag=v.getId();
                        ImageButton imageButton00 = (ImageButton) linearLayouts[flag/5].findViewById(flag);//获取Dialog中的按钮
                        if (ifSelected[flag / 5][flag % 5]) {
                            if (flag<7){
                                ifClicked[flag] = false;
                                ImageButton imageButton0 = (ImageButton) ChannelButtonLayout.findViewById(flag);
                                imageButton0.setImageResource(smallNormalDrawable[flag]);
                            }
                            ifSelected[flag / 5][flag % 5] = false;
                            imageButton00.setImageResource(bigNormalDrawable[flag/5][flag%5]);
                            setLineResume(flag);
                        } else {
                            if (getChannleSelectCount() <= 1) {
                                Toast.makeText(context, "请至少选择一个通道", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (flag<7){
                                ifClicked[flag] = true;
                                ImageButton imageButton0 = (ImageButton) ChannelButtonLayout.findViewById(flag);
                                imageButton0.setImageResource(smallNoColorDrawable[flag]);
                            }
                            ifSelected[flag / 5][flag % 5] = true;
                            imageButton00.setImageResource(bigNoColorDrawable[flag/5][flag%5]);
                            btnChooseAll.setImageResource(R.drawable.select_box);
                            ifChooseAllSeleted = true;
                            setLineToTransparent(flag);
                        }
                    }
                });
            }
        }
        //如果当前布局中按钮不足5个，添加新按钮（不可点击）将其补充到5个按钮方便布局对齐
        for (int c = 0; c < 3; c++) {
            if (buttons[c] < 5) {
                int a = 5 - buttons[c];
                for (int j = 0; j < a; j++) {
                    ImageButton imageButton = new ImageButton(context);
                    imageButton.setBackgroundColor(context.getResources().getColor(R.color.channelBtnBackInDialog));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                    imageButton.setLayoutParams(lp); //设置每个按钮权重为1
                    imageButton.setClickable(false);
                    linearLayouts[c].addView(imageButton);
                }
                break;
            }
        }
        //为确定和全选按钮定义监听器
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_sure:
                        dialog.dismiss();
                        break;
                    case R.id.btn_chooseall:
                    case R.id.text_chooseall:
                    case R.id.chooseall_layout:
                        if (ifChooseAllSeleted) {
                            //Toast.makeText(context, "全选", Toast.LENGTH_LONG).show();
                            //点击全选，将所有按钮背景变为彩色,flag置为false
                            btnChooseAll.setImageResource(R.drawable.select_box_selected);
                            for (int i = 0; i < channleCount; i++) {
                                ImageButton imageButton = (ImageButton) view.findViewById(i);
                                if (i <= 4) {
                                    imageButton.setImageResource(bigNormalDrawable[i / 5][i]);
                                    imageButton = (ImageButton) ChannelButtonLayout.findViewById(i);
                                    imageButton.setImageResource(smallNormalDrawable[i]);
                                    ifClicked[i] = false;
                                    ifSelected[i / 5][i] = false;
                                } else if (i <= 9) {
                                    imageButton.setImageResource(bigNormalDrawable[i / 5][i - 5]);
                                    if (i < 7) {
                                        imageButton = (ImageButton) ChannelButtonLayout.findViewById(i);
                                        imageButton.setImageResource(smallNormalDrawable[i]);
                                    }
                                    ifClicked[i] = false;
                                    ifSelected[i / 5][i - 5] = false;
                                } else {
                                    imageButton.setImageResource(bigNormalDrawable[i / 5][i - 10]);
                                    ifClicked[i] = false;
                                    ifSelected[i / 5][i - 10] = false;
                                }
                            }
                            ifChooseAllSeleted = false;
                        } else {
                            //Toast.makeText(context, "取消全选", Toast.LENGTH_LONG).show();
                            //取消全选，将所有按钮背景变为白色，记得将所有的flag置为true,这里只有效果，点了确定之后才会生效
                            btnChooseAll.setImageResource(R.drawable.select_box);
                            for (int i = 0; i < channleCount; i++) {
                                ImageButton imageButton = (ImageButton) view.findViewById(i);
                                if (i <= 4) {
                                    imageButton.setImageResource(bigNoColorDrawable[i / 5][i]);
                                    imageButton = (ImageButton) ChannelButtonLayout.findViewById(i);
                                    imageButton.setImageResource(smallNoColorDrawable[i]);
                                    ifClicked[i] = true;
                                    ifSelected[i / 5][i] = true;
                                } else if (i <= 9) {
                                    imageButton.setImageResource(bigNoColorDrawable[i / 5][i - 5]);
                                    if (i < 7) {
                                        imageButton = (ImageButton) ChannelButtonLayout.findViewById(i);
                                        imageButton.setImageResource(smallNoColorDrawable[i]);
                                    }
                                    ifClicked[i] = true;
                                    ifSelected[i / 5][i - 5] = true;
                                } else {
                                    imageButton.setImageResource(bigNoColorDrawable[i / 5][i - 10]);
                                    ifClicked[i] = true;
                                    ifSelected[i / 5][i - 10] = true;
                                }
                            }
                            ifChooseAllSeleted = true;
                        }
                        break;
                }
            }
        };
        btnSure.setOnClickListener(listener);//为确定按钮添加监听器
        chooseall_layout.setOnClickListener(listener);//为全选按钮添加监听器

        dialog.setContentView(view);
        dialog.show();
        // 设置相关位置，一定要在 show()之后
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;        //确定弹出框的位置
        window.setAttributes(params);

    }
}
