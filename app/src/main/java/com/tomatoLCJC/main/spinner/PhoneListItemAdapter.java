package com.tomatoLCJC.main.spinner;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tomatoLCJC.main.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mij on 2017/10/14.
 */

public class PhoneListItemAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder;
    private MyClickListener myClickLister;
    private ArrayList<Map<String, Object>> mListData;

    private HashMap<String,Boolean> states = new HashMap<>();
    private int mSelect = -1;

    public interface MyClickListener {
        void clickListener(View v);
    }

    public PhoneListItemAdapter(Activity activity, ArrayList<Map<String, Object>> listData, MyClickListener clickListener){
        context = activity;
        mListData = listData;
        this.myClickLister = clickListener;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        //return mListData.get(position);
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.calibration_data_listview_item, null);  //把mylistview_item赋值给convertView
            holder = new ViewHolder();
            holder.radioBtn = (RadioButton) convertView.findViewById(R.id.tv_radioButton);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvThickness = (TextView) convertView.findViewById(R.id.tv_thickness);
            holder.tvLiftValue = (TextView) convertView.findViewById(R.id.tv_lift);
            holder.tvMaterial = (TextView) convertView.findViewById(R.id.tv_material);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvNumProbes = (TextView) convertView.findViewById(R.id.tv_probes);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        //给相应控件赋值
        holder.radioBtn.setText((String) mListData.get(position).get("radioButton"));
        holder.tvTime.setText((String) mListData.get(position).get("time"));
        holder.tvThickness.setText((String) mListData.get(position).get("thickness"));
        holder.tvLiftValue.setText((String) mListData.get(position).get("liftOffValue"));
        holder.tvMaterial.setText((String) mListData.get(position).get("material"));
        holder.tvName.setText((String) mListData.get(position).get("deviceName"));
        holder.tvNumProbes.setText((String) mListData.get(position).get("numOfProbes"));

        if(position == mSelect){
            convertView.setBackgroundResource(R.color.channelBtnBackInDialog);
        }else{
            convertView.setBackgroundResource(R.color.detectionBackground);
        }

        //只允许一个RadioButton被选中
        holder.radioBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                clearStates(position);
                myClickLister.clickListener(view);
                changeSelector(position);
            }
        });

        boolean res;  //判断该处的 RadioButton 是否应为选中状态
        res = !(states.get(String.valueOf(position)) == null || !states.get(String.valueOf(position)));// 如果map中position对应的判断为假或不存在，则res为假，否则为真
        holder.radioBtn.setChecked(res);
        if (res)
            states.put(String.valueOf(position), true);
        else
            states.put(String.valueOf(position), false);

        holder.radioBtn.setTag(position);
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

    public void changeSelector(int position){
        mSelect = position;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        RadioButton radioBtn;
        TextView tvTime;
        TextView tvThickness;
        TextView tvLiftValue;
        TextView tvMaterial;
        TextView tvName;
        TextView tvNumProbes;
    }
}
