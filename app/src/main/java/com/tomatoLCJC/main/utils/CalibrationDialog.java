package com.tomatoLCJC.main.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tomatoLCJC.main.R;
import com.tomatoLCJC.tools.dataBase.Bean.DeviceDetectionRecordBean;
import com.tomatoLCJC.tools.dataBase.Dao.DeviceDetectionRecordDao;

import java.text.DecimalFormat;

/**
 * Created by YCQ on 2017/10/12.
 */

public class CalibrationDialog extends Dialog implements View.OnClickListener{

    private Context mContext;
    private TextView thicknessValue;
    private TextView upliftValue;
    private TextView materialValue;
    private TextView flawValue1;
    private TextView flawPercent1;
    private TextView flawValue2;
    private TextView flawPercent2;
    private TextView flawValue3;
    private TextView flawPercent3;
    private TextView flawValue4;
    private TextView flawPercent4;
    private Button btnSave;
    private Button btnCancle;

    private DeviceDetectionRecordBean bean;

    public CalibrationDialog(@NonNull Context context,DeviceDetectionRecordBean bean) {
        super(context,R.style.calibration_dialog);
        this.mContext=context;
        this.bean=bean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_calibration_dialog);
        setCanceledOnTouchOutside(false);
        initView();
        initData();
    }

    private void initView(){
        thicknessValue= (TextView) findViewById(R.id.thickness_value);
        upliftValue= (TextView) findViewById(R.id.uplift_value);
        materialValue= (TextView) findViewById(R.id.material_value);
        flawValue1= (TextView) findViewById(R.id.flaw_value_1);
        flawPercent1= (TextView) findViewById(R.id.flaw_percent_1);
        flawValue2= (TextView) findViewById(R.id.flaw_value_2);
        flawPercent2= (TextView) findViewById(R.id.flaw_percent_2);
        flawValue3= (TextView) findViewById(R.id.flaw_value_3);
        flawPercent3= (TextView) findViewById(R.id.flaw_percent_3);
        flawValue4= (TextView) findViewById(R.id.flaw_value_4);
        flawPercent4= (TextView) findViewById(R.id.flaw_percent_4);
        btnSave= (Button) findViewById(R.id.btn_save);
        btnCancle= (Button) findViewById(R.id.btn_cancle);
        btnSave.setOnClickListener(this);
        btnCancle.setOnClickListener(this);
    }

    public void initData(){
        DecimalFormat df   = new DecimalFormat("#0.000");
        thicknessValue.setText(String.valueOf(bean.getSteelThickness()));
        upliftValue.setText(String.valueOf(bean.getUpliftValue()));
        materialValue.setText(String.valueOf(bean.getSteelTexture()));
        flawValue1.setText(String.valueOf(df.format(bean.getDefectPercent1_value())));
        flawValue2.setText(String.valueOf(df.format(bean.getDefectPercent2_value())));
        flawValue3.setText(String.valueOf(df.format(bean.getDefectPercent3_value())));
        flawValue4.setText(String.valueOf(df.format(bean.getDefectPercent4_value())));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancle:   //取消

                this.dismiss();
                break;
            case R.id.btn_save:     //保存
                bean.setSteelThickness(Double.valueOf(thicknessValue.getText().toString().trim()));    //厚度
                bean.setUpliftValue(Double.valueOf(upliftValue.getText().toString().trim()));           //提离值
                bean.setSteelTexture(materialValue.getText().toString().trim());                           //材质
                bean.setDefectPercent1(Double.valueOf(flawPercent1.getText().toString().trim()));
                bean.setDefectPercent1_value(Double.valueOf(flawValue1.getText().toString().trim()));
                bean.setDefectPercent2(Double.valueOf(flawPercent2.getText().toString().trim()));
                bean.setDefectPercent2_value(Double.valueOf(flawValue2.getText().toString().trim()));
                bean.setDefectPercent3(Double.valueOf(flawPercent3.getText().toString().trim()));
                bean.setDefectPercent3_value(Double.valueOf(flawValue3.getText().toString().trim()));
                bean.setDefectPercent4(Double.valueOf(flawPercent4.getText().toString().trim()));
                bean.setDefectPercent4_value(Double.valueOf(flawValue4.getText().toString().trim()));

                Quadratic quadratic =new Quadratic();
                quadratic.Fitting(new Point(0,0),
                        new Point(Float.valueOf(flawValue1.getText().toString().trim()),Float.valueOf(flawPercent1.getText().toString().trim())),
                        new Point(Float.valueOf(flawValue2.getText().toString().trim()),Float.valueOf(flawPercent2.getText().toString().trim())));
                bean.setValue_a(quadratic.getA());
                bean.setValue_b(quadratic.getB());
                quadratic.Fitting(
                        new Point(Float.valueOf(flawValue2.getText().toString().trim()),Float.valueOf(flawPercent2.getText().toString().trim())),
                        new Point(Float.valueOf(flawValue3.getText().toString().trim()),Float.valueOf(flawPercent3.getText().toString().trim())),
                        new Point(Float.valueOf(flawValue4.getText().toString().trim()),Float.valueOf(flawPercent4.getText().toString().trim())));
                bean.setValue_c(quadratic.getA());
                bean.setValue_d(quadratic.getB());
                bean.setValue_e(quadratic.getC());

                DeviceDetectionRecordDao.getInstance().updateDetectionData(bean);

                Toast.makeText(mContext,"设备校准完成！",Toast.LENGTH_SHORT).show();
                //最终绘制折线图，四个点之间的线

                this.dismiss();
                break;
        }

    }
}
