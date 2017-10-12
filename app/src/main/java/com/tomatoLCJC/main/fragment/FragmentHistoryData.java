package com.tomatoLCJC.main.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tomatoLCJC.main.MainActivity;
import com.tomatoLCJC.main.MyApplication;
import com.tomatoLCJC.main.R;
import com.tomatoLCJC.main.spinner.DialogSpinner;
import com.tomatoLCJC.tools.dataBase.Bean.HistoryDataRecordBean;
import com.tomatoLCJC.tools.dataBase.Dao.HistoryDataRecordDao;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 *Created by zch22 on 2017/5/1.
 * 历史数据页面
 */
public class FragmentHistoryData extends Fragment {

    private ListView listview;      //历史数记录列表
    private DialogSpinner spYear;   //年
    private DialogSpinner spMonth;  //月
    private DialogSpinner spDay;    //日
    private ProgressDialog myDialog;
    private ArrayList<String> yearList = new ArrayList<String>();
    private ArrayList<String> monthList = new ArrayList<String>();
    private ArrayList<String> dayList = new ArrayList<String>();

    MyAdapter adapter;//历史纪录ListView的适配器
    private ArrayAdapter<String> adapterDay;//日期弹出框的适配器

    private String year_num = null;
    private String month_num = null;
    private String day_num = "——";

    private List<String> list = new ArrayList<>();  //所有数据
    public static ArrayList<HistoryDataRecordBean> recordList = new ArrayList<>();
    public static List<String> title = new ArrayList<String>(); //每组数据日期(标题)
    private long record_id;     //历史数据记录的ID


    //以下成员变量是异步任务时用到的
    private ArrayList<ArrayList<Double>> xList;
    private ArrayList<ArrayList<Double>> yList;
    private ArrayList<ArrayList<Double>> detectionValue;
    private ArrayList<ArrayList<Double>> gradientValue;
    private ArrayList<ArrayList<Double>> denoisingValue;
    private ArrayList<ArrayList<Double>> flawValue;
    private String recordTitle;

    //更新图表
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle =msg.getData();
            int channelNum=bundle.getInt("num");
            float waringValue =bundle.getFloat("warn");
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.showChart(xList.get(0),yList, detectionValue, gradientValue, denoisingValue,flawValue, recordTitle, channelNum );//datameasure页面图表更新，这里应当重新初始化按钮数量和图表
            mainActivity.jumpToFirstFragment();  //跳转至FragmentDateMeasure页面
            ImageButton btnClean = (ImageButton) getActivity().findViewById(R.id.btnClean);
            btnClean.setEnabled(true);          //设置清屏可点击
            btnClean.setImageResource(R.drawable.clean_normal);
            xList = null;
            yList = null;
            detectionValue = null;
            gradientValue= null;
            denoisingValue =null;
            flawValue = null;
        }
    };

    public FragmentHistoryData(){}

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historydata, container, false);
        listview = (ListView) view.findViewById(R.id.list);
        spYear = (DialogSpinner)view.findViewById(R.id.sp_year);
        spMonth = (DialogSpinner)view.findViewById(R.id.sp_month);
        spDay = (DialogSpinner)view.findViewById(R.id.sp_day);
        initDate(year_num,month_num,day_num);
        adapter = new MyAdapter();
        listview.setAdapter(adapter);
        
        //为历史数据列表的每行数据添加点击事件
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                recordTitle = (String) ((TextView) view.findViewById(R.id.record_Id)).getText();//获得点击那行记录的标题
                if((recordTitle.substring(4,5)).equals("年")) {
                    //如果是年月日的标题则不响应点击事件
                } else{
                    String title = recordTitle.replaceAll(" |-|:", "");
                    Log.d(TAG, "onItemClick: " + title);
                    if (!title.equals("没有数据！")) {
                        record_id = HistoryDataRecordDao.getInstance().queryIdByTitle(title);
                        Log.d(TAG, "onItemClick: record_id" + record_id);//通过title查询记录编号
                        queryAndProcessData();//从数据库取出并处理数据，然后通知UI线程画图
                    }
                }
            }
        });

        return view;
    }

    //初始化日期同时根据日期历史纪录列表进行过滤
    public void initDate(String year_num,String month_num,String day_num) {
        //日期,年份从当年起后40年
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 40; i++) {
            yearList.add(("" + (cal.get(Calendar.YEAR)-10 + i)));
        }
        spYear.setList(yearList);
        spYear.setSpinnername("year");
        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(this.getActivity(), R.layout.simple_date_spinner_item,yearList);
        spYear.setAdapter(adapterYear);
        //12个月
        for (int i = 1; i <= 12; i++) {
            monthList.add("" + (i < 10 ? "0" + i : i));
        }
        spMonth.setList(monthList);
        spMonth.setSpinnername("month");
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(this.getActivity(), R.layout.simple_date_spinner_item,monthList);
        spMonth.setAdapter(adapterMonth);

        String month = String.valueOf(cal.get(Calendar.MONTH)+1);
        if(cal.get(Calendar.MONTH) < 9)
            month = "0"+month;
        spMonth.setOnItemSelectedListener(new DateOnItemClickListener());

        spDay.setList(dayList);
        spDay.setSpinnername("day");
        adapterDay = new ArrayAdapter<>(this.getActivity(), R.layout.simple_date_spinner_item,dayList);
        spDay.setAdapter(adapterDay);
        spDay.setText(day_num);
        if(year_num == null) {
            year_num = ""+cal.get(Calendar.YEAR);
        }
        if(month_num == null) {
            month_num = month;
        }
        initData(year_num,month_num,day_num);//由日期初始化数据（2017，06，05）
        spYear.setYear_number(year_num);
        spYear.setText(year_num);
        spMonth.setMonth_number(month_num);
        spMonth.setText(month_num);
        spDay.setDay_number(day_num);
        spDay.setText(day_num);
    }


    public void initData(String year,String month,String day){
        list.clear();
        //初始化数据列表
        recordList=HistoryDataRecordDao.getInstance().queryByDate(year,month,day);
        title = HistoryDataRecordDao.getInstance().getTitleList(recordList,day);
        String recordTitle;
        String displayTitle;
        List<String> tempList = new ArrayList<>();
        if(recordList.size()==0){
            list.add("没有数据！");
        }else {
            for (int i = 0; i < recordList.size(); i++) {
                if(!list.contains(title.get(i))) {
                    list.add(title.get(i));//防止标题重复插入(这里的标题形式为2017年6月（5日）)
                }
                recordTitle = recordList.get(i).getTitle();
                displayTitle = recordTitle.substring(0, 4)+"-"+recordTitle.substring(4,6)+"-"+recordTitle.substring(6, 8) +" "+ recordTitle.substring(8, 10)+":" + recordTitle.substring(10, 12) +":"+ recordTitle.substring(12, 14) +":"+ recordTitle.substring(14, 16);
                tempList.add(displayTitle);
            }
            int size=tempList.size();
            String[]strs = new String[size];
            for(int i=0;i<size;i++){
                strs[i]=tempList.get(i);
            }
            Arrays.sort(strs);//根据标题（时间）排序
            for(int i=size-1;i>=0;i--){
                list.add(strs[i]);
            }
        }
    }
    //根据月份对天数进行选择
    private class DateOnItemClickListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            dayList.clear();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.valueOf(spYear.getSelectedItem().toString()));
            cal.set(Calendar.MONTH, position);
            int dayofm = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            dayList.add("——");
            for (int i = 1; i <= dayofm; i++) {
                dayList.add("" + (i < 10 ? "0" + i : i));
            }
            adapterDay.notifyDataSetChanged();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    }

    //通过年月日时间信息来刷新历史纪录列表
    public void refreshListView(String year,String month,String day)
    {
        list.clear();
        initDate(year,month,day);
        if(list.size()!=0) {
            adapter.notifyDataSetChanged();
        }
    }

    //历史数据列表适配器
    private class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=convertView;
            if(title.contains(getItem(position))){
                view = LayoutInflater.from(MyApplication.CONTEXT).inflate(R.layout.list1_item_tag,null);
            }else {
                view = LayoutInflater.from(MyApplication.CONTEXT).inflate(R.layout.datarecordlist1, null);
            }
            TextView text=(TextView) view.findViewById(R.id.record_Id);
            text.setText((CharSequence) getItem(position));
            return view;
        }
    }


    //从数据库取出数据并处理数据
    private void queryAndProcessData(){
            drawHistoryChartTask task = new drawHistoryChartTask();
            task.execute();
    }

    //三个参数：第一个是传入参数，第二个代表进度，第三个代表返回值
    class drawHistoryChartTask extends AsyncTask<Void,Integer,Integer> {
        // 运行在UI线程中，在调用doInBackground()之前执行
        @Override
        protected void onPreExecute() {
            myDialog= ProgressDialog.show(getActivity(),"请稍后","正在处理中...",true);
        }
        // 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
        @Override
        protected Integer doInBackground(Void... params) {
            HistoryDataRecordBean hisBean = HistoryDataRecordDao.getInstance().queryBeanByID(record_id);
            int channelNum = hisBean.getChannelCount();//获得记录的通道数量
            int stepInterval = 1;          //=hisBean.ge();//获得记录的横向步数间隔
            double stepDistance=hisBean.getStepDistance();//获得记录的步长
            int distance =hisBean.getChannelInterval();//获得记录的通道间距
            if(channelNum == 0){
                System.out.println("查询失败");
                return -1;
            }
            Map<String, ArrayList<ArrayList<Double>>> map = HistoryDataRecordDao.getInstance().queryRecordDetailByRecordID(record_id,channelNum);//由记录编号查询明细表中的数据
            xList = map.get("X");
            yList = map.get("Y");
            detectionValue = map.get("DV");     //检测数据
            gradientValue= new ArrayList<>();   //横向梯度
            denoisingValue =map.get("DSV");   //去噪值
            flawValue = map.get("FV"); //缺陷值
            for(int i=0;i<channelNum;i++){
                gradientValue.add(new ArrayList<Double>());
            }
            //通过原始数据纵坐标得到横向梯度纵坐标
            for(int i=0,length=detectionValue.size();i<length;i++){
                int size = detectionValue.get(i).size();
                double temp=0;
                for(int j=0;j<size-stepInterval;j++){
                    double value =Math.abs(detectionValue.get(i).get(j+stepInterval)-detectionValue.get(i).get(j))/stepDistance/stepInterval;
                    gradientValue.get(i).add(value);
                }
                if(size - stepInterval > 0) {
                    for (int k = size - stepInterval; k < size - 1; k++) {
                        double value = Math.abs(detectionValue.get(i).get(k + 1) - detectionValue.get(i).get(k))/stepDistance/stepInterval;
                        gradientValue.get(i).add(value);
                        temp = value;
                    }
                    gradientValue.get(i).add(temp);//gradientValue比xList少一个，在每组横向梯度最后多加一组数据（复制的前一个的数据）
                }else{
                    for(int k=0;k < size-1 ;k++){
                        double value = Math.abs(detectionValue.get(i).get(k + 1) - detectionValue.get(i).get(k))/stepDistance/stepInterval;
                        gradientValue.get(i).add(value);
                        temp = value;
                    }
                    gradientValue.get(i).add(temp);//取前一组的数据
                }
            }

            Message msg =new Message();
            Bundle bundle=new Bundle();
            bundle.putInt("num",channelNum);
//            bundle.putFloat("warn",gradient);
            msg.setData(bundle);
            handler.sendMessage(msg);
            return null;
        }
        // 运行在ui线程中，在doInBackground()执行完毕后执行
        @Override
        protected void onPostExecute(Integer integer) {
            myDialog.dismiss();
        }
    }



}

