package com.tomatoLCJC.main.spinner;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tomatoLCJC.main.R;

import java.util.ArrayList;

/**
 * Created by 雪 on 2017/4/18.
 */
public class ListviewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> list;
    int select_position = -1;
    public ListviewAdapter(Context context, ArrayList<String> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }
    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }
    public void setSelect_position(int select_position){
        this.select_position = select_position;
    }
    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        if(arg1 == null&&list.size() != 0){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            arg1 = inflater.inflate(R.layout.date_spinner_item, null);
            viewHolder.textView = (TextView)arg1.findViewById(R.id.date_spinner);
            arg1.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder) arg1.getTag();

        if(arg0 == select_position){
            arg1.setSelected(true);
            arg1.setPressed(true);
            arg1.setBackgroundColor(Color.parseColor("#c7c7c7"));
        }else{
            arg1.setSelected(true);
            arg1.setPressed(true);
            arg1.setBackgroundColor(Color.parseColor("#dcdcdc"));
        }
        viewHolder.textView.setText(list.get(arg0));
        return arg1;
    }
   /* @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        return getView(position,convertView,parent);
    }*/



}
