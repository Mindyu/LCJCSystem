package com.tomatoLCJC.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by zch22 on 2017/4/19.
 * 菜单页面
 */
public class MenuActivity extends Activity {
    private LinearLayout system_parameter;
    private LinearLayout magnetic_calibration;
    private LinearLayout about_system;
    ImageView menu_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_menu);
        initView();                 //初始化组件
        initEvent();                //为组件添加点击事件监听器
    }

    public void initView(){
        system_parameter=(LinearLayout)findViewById(R.id.system_parameter);
        magnetic_calibration=(LinearLayout)findViewById(R.id.Magnetic_calibration);
        about_system=(LinearLayout)findViewById(R.id.about_system);
        menu_back=(ImageView)findViewById(R.id.menu_back);
    }

    public void initEvent(){
        system_parameter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SystemParameterActivity.class);
                startActivity(intent);
            }
        });
        magnetic_calibration.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, DeviceCalibrationActivity.class);
                startActivity(intent);

            }
        });
        about_system.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AboutSystemActivity.class);
                startActivity(intent);
            }
        });

        menu_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
