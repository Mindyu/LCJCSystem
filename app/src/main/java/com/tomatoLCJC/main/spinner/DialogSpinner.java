package com.tomatoLCJC.main.spinner;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.tomatoLCJC.main.MainActivity;
import com.tomatoLCJC.main.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by 雪 on 2017/4/17.
 */
@SuppressLint("AppCompatCustomView")
public class DialogSpinner extends Spinner {

    Calendar cal = Calendar.getInstance();
    public static Dialog dialog = null;
    private ArrayList<String> list;     //ArrayList<String> list存储所要显示的数据
    public String text = "";
    public String spinnername;
    public static String year_number;
    public static String month_number;
    public static String day_number;
    Paint mPaint;
//    DataRecord1Operation recordDataOper;
    static int select_position_year = -1;
    static int select_position_month = -1;
    static int select_position_day = -1;

    public DialogSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(55);
        mPaint.setColor(Color.parseColor("#c97979"));
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        float stringwidth = mPaint.measureText(text);
        float x = (getWidth()-stringwidth)/2;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float y = getHeight()/2+(Math.abs(fontMetrics.ascent)-fontMetrics.descent)/2;
        canvas.drawText(text,x,y,mPaint);
    }

    //如果视图定义了OnClickListener监听器，调用此方法来执行
    @Override
    public boolean performClick() {
        Context context = getContext();
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.date_spinner, null);
       // select_position = -1;
        final ListView listview = (ListView) view.findViewById(R.id.datespinner_list);
        final ListviewAdapter adapters = new ListviewAdapter(context, getList());
        listview.setAdapter(adapters);
        if(spinnername == "year"){
            listview.setSelection(select_position_year-2);
            adapters.setSelect_position(select_position_year);
        }else if(spinnername == "month") {
            listview.setSelection(select_position_month-2);
            adapters.setSelect_position(select_position_month);
        }
        else if(spinnername == "day") {
            listview.setSelection(select_position_day-2);
            adapters.setSelect_position(select_position_day);
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View itemView, int position,
                                    long id) {
                String str = list.get(position);
                if(spinnername == "year"){
                    year_number = list.get(position);
                    select_position_year = position;

                }else if(spinnername == "month") {
                    month_number = list.get(position);
                    select_position_month = position;
                }
                else if(spinnername == "day") {
                    day_number = list.get(position);
                    select_position_day = position;
                }
                //刷新历史数据的listview
                if(year_number != null && month_number != null) {
                    MainActivity ParentActivity = (MainActivity) getContext();
                    ParentActivity.refreshHistoryData(year_number, month_number, day_number);
                }
                setSelection(position);
                adapters.notifyDataSetChanged();
                setText(str);
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
       // recordDataOper = new DataRecord1Operation(MyApplication.CONTEXT);

        dialog = new Dialog(this.getContext(), R.style.dialog);
        Window win = dialog.getWindow();
        LayoutParams params = new LayoutParams(320, LayoutParams.MATCH_PARENT);
        dialog.setContentView(view,params);
       // dialog.setContentView(view);
        WindowManager.LayoutParams lp = win.getAttributes();
        if(spinnername == "year")
        {
            lp.horizontalMargin = 0.05F;
            win.setGravity(Gravity.LEFT);
        }
        else if(spinnername == "month") {
            win.setGravity(Gravity.CENTER);
        }
        else if(spinnername == "day") {
            lp.horizontalMargin = 0.05F;
            win.setGravity(Gravity.RIGHT);
        }
       // lp.y = 350;
        lp.verticalMargin = 0.25F;
        win.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return true;
    }

    public String getSpinnername() {
        return spinnername;
    }

    public void setSpinnername(String spinnername) {
        this.spinnername = spinnername;
    }
    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setYear_number(String year_number) {
        this.year_number = year_number;
    }
    public String getYear_number() {
        return year_number;
    }
    public String getMonth_number() {
        return month_number;
    }
    public void setMonth_number(String month_number) {
        this.month_number = month_number;
    }
    public void setDay_number(String day_number) {
        this.day_number = day_number;
    }
    public String getDay_number() {
        return day_number;
    }
}
