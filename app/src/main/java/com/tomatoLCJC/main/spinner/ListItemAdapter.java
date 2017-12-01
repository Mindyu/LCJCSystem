package com.tomatoLCJC.main.spinner;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
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
 * Created by M continuous on 2017/10/14.
 */

public class ListItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Map<String,Object>> list;
    private MyClickListener myClickListener;
    private HashMap<String,Boolean> states = new HashMap<>();
    private ViewHolder viewHolder;
    private int mSelect = -1;

    public interface MyClickListener{
        void clickListener(View v);
    }

    public ListItemAdapter(Activity activity, ArrayList<Map<String, Object>> list, MyClickListener clickListener) {
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

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.calibration_data_listview_item_tab, null);          //把布局 list_item 赋给 convertView
            viewHolder = new ViewHolder();
            viewHolder.radioButton = (RadioButton) convertView.findViewById(R.id.text_radioButton);
            viewHolder.textTime = (TextView) convertView.findViewById(R.id.text_time);
            viewHolder.textThickness = (TextView) convertView.findViewById(R.id.text_thickness);
            viewHolder.textLiftOffValue = (TextView) convertView.findViewById(R.id.text_liftOffValue);
            viewHolder.textMaterial = (TextView) convertView.findViewById(R.id.text_material);
            viewHolder.textDeviceName = (TextView) convertView.findViewById(R.id.text_deviceName);
            viewHolder.textNumOfProbes = (TextView) convertView.findViewById(R.id.text_numOfProbes);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //给相应控件赋值
        viewHolder.radioButton.setText((String)list.get(position).get("radioButton"));
        viewHolder.textTime.setText((String)list.get(position).get("time"));
        viewHolder.textThickness.setText((String)list.get(position).get("thickness"));
        viewHolder.textLiftOffValue.setText((String)list.get(position).get("liftOffValue"));
        viewHolder.textMaterial.setText((String)list.get(position).get("material"));
        viewHolder.textDeviceName.setText((String)list.get(position).get("deviceName"));
        viewHolder.textNumOfProbes.setText((String)list.get(position).get("numOfProbes"));

        if(position == mSelect){
            convertView.setBackgroundResource(R.color.channelBtnBackInDialog);
        }else {
            convertView.setBackgroundResource(R.color.detectionBackground);
        }

        //只允许一个RadioButton被选择
        viewHolder.radioButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                clearStates(position);
                myClickListener.clickListener(view);
                changeSelector(position);
            }
        });

        boolean res;//判断该处的RadioButton是否为选中状态
        res = !( states.get(String.valueOf(position)) == null || !states.get(String.valueOf(position)) );
        viewHolder.radioButton.setChecked(res);
        if(res){
            states.put(String.valueOf(position),true);
        }else {
            states.put(String.valueOf(position),false);
        }

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
        private TextView textTime;
        private TextView textThickness;
        private TextView textLiftOffValue;
        private TextView textMaterial;
        private TextView textDeviceName;
        private TextView textNumOfProbes;
    }
}
