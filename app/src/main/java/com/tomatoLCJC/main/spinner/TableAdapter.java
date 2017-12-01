package com.tomatoLCJC.main.spinner;

import android.app.Activity;
import android.widget.BaseAdapter;

/**
 * Created by liuhdme on 2017/5/12.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tomatoLCJC.main.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 ** Created by liuhdme on 2017/4/20.
 */

public class TableAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Map<String, Object>> list;
    private MyClickListener myClickListener;
    private HashMap<String,Boolean> states = new HashMap<>();
    private ViewHolder viewHolder;
    private int mSelect = -1;

    private static final String TAG = "TableAdapter";

    public interface MyClickListener {
        void clickListener(View v);
    }

    /**
     * 函数名：TableAdapter
     * 作用：构造函数
     * 参数：Activity, ArrayList<Map<String, Object>>, MyClickListener
     * */
    public TableAdapter(Activity activity, ArrayList<Map<String, Object>> list,
                        MyClickListener clickListener){
        context = activity;
        this.list = list;
        this.myClickListener = clickListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, null);//把布局 list_item 赋给 convertView
            viewHolder = new ViewHolder();
            viewHolder.radioButton = (RadioButton) convertView.findViewById(R.id.text_radioButton);
            viewHolder.objectName = (TextView) convertView.findViewById(R.id.text_objectsName);
            viewHolder.material = (TextView) convertView.findViewById(R.id.text_material);
            viewHolder.model = (TextView) convertView.findViewById(R.id.text_model);
            viewHolder.defectLevel = (TextView) convertView.findViewById(R.id.text_defectLevel);
            viewHolder.magneticGradient = (TextView) convertView.findViewById(R.id.text_magneticGradient);
            viewHolder.stressValue = (TextView) convertView.findViewById(R.id.text_stressValue);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //—————————————————————————— 给相应的控件赋值 ———————————————————————————————————————————————
        viewHolder.radioButton.setText((String)list.get(position).get("radioButton"));
        viewHolder.objectName.setText((String)list.get(position).get("name"));
        viewHolder.material.setText((String)list.get(position).get("material"));
        viewHolder.model.setText((String)list.get(position).get("model"));
        viewHolder.defectLevel.setText((String)list.get(position).get("defectLevel"));
        viewHolder.magneticGradient.setText((String) list.get(position).get("magneticGradient"));
        viewHolder.stressValue.setText((String) list.get(position).get("stressValue"));

        if (position == mSelect)
            convertView.setBackgroundResource(R.color.channelBtnBackInDialog);
        else
            convertView.setBackgroundResource(R.color.detectionBackground);

        //---------——————————————--- 只允许一个 RadioButton 被选择 ——————————————————----------------
        viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearStates(position);
                myClickListener.clickListener(v);
                changeSelector(position);
            }
        });

        boolean res;  //判断该处的 RadioButton 是否应为选中状态
        res = !(states.get(String.valueOf(position)) == null || !states.get(String.valueOf(position)));// 如果map中position对应的判断为假或不存在，则res为假，否则为真
        viewHolder.radioButton.setChecked(res);
        if (res)
            states.put(String.valueOf(position), true);
        else
            states.put(String.valueOf(position), false);

        viewHolder.radioButton.setTag(position);
        return convertView;
    }

    /**
     * 方法名：clearStates
     * 参数：position
     * 作用：用于在 activity 中重置所有的 RadioButton 的状态，并将 position 处的 RadioButton 设置为点击状态
     * */
    public void clearStates(int position) {
        // 重置，确保最多只有一项被选中
        for(String key:states.keySet()){
            states.put(key,false);
        }
        states.put(String.valueOf(position), true);
    }

    /**
     * 方法名：getStates
     * 参数：position
     * 作用：获取 position 处的RadioButton 的点击状态
     * */
    public Boolean getStates(int position){
        if (states.get(String.valueOf(position)) == null)
            return false;
        return states.get(String.valueOf(position));
    }

    public void changeSelector(int position) {
        mSelect = position;
        notifyDataSetChanged();
    }

    private static class ViewHolder{
        private RadioButton radioButton;
        private TextView objectName;
        private TextView material;
        private TextView model;
        private TextView defectLevel;
        private TextView magneticGradient;
        private TextView stressValue;
    }

}
