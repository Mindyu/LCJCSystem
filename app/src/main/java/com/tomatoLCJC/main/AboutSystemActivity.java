package com.tomatoLCJC.main;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomatoLCJC.tools.Parameter.SystemParameter;

/**
 *
 * 关于系统页面
 */
public class AboutSystemActivity extends Activity {
    private TextView finish;
    private ImageView back;
    private TextView chaneel_num;
    private TextView chaneel_fenl;
    private TextView chaneel_jian;
    private TextView chaneel_step;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about_sysytem);
        initView();
        initEvent();
    }

    public void initView(){
        finish=(TextView)findViewById(R.id.btn_finish);
        back=(ImageView)findViewById(R.id.return_key);
        chaneel_num= (TextView) findViewById(R.id.chaneel_num);
        chaneel_num.setText(String.valueOf(SystemParameter.getInstance().nChannelNumber));
        chaneel_fenl= (TextView) findViewById(R.id.chaneel_fenl);
        chaneel_fenl.setText(String.valueOf(SystemParameter.getInstance().channelWeight));
        chaneel_jian= (TextView) findViewById(R.id.chaneel_jian);
        chaneel_jian.setText(String.valueOf(SystemParameter.getInstance().nChannelDistance)+"mm");
        chaneel_step= (TextView) findViewById(R.id.chaneel_step);
        chaneel_step.setText(String.valueOf(SystemParameter.getInstance().disSensorStepLen)+"mm");
    }
    public void initEvent(){
        finish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
