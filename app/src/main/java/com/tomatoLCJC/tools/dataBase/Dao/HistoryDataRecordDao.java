package com.tomatoLCJC.tools.dataBase.Dao;


import android.database.sqlite.SQLiteDatabase;

import com.tomatoLCJC.tools.Parameter.SystemParameter;
import com.tomatoLCJC.tools.dataBase.Bean.HistoryDataRecordBean;
import com.tomatoLCJC.tools.dataBase.Bean.HistoryDataRecordDetailBean;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对历史数据记录的数据库操作类
 */
public class HistoryDataRecordDao {

    private static HistoryDataRecordDao historyDataRecordDao = null;
    private HistoryDataRecordDao() {
        LitePal.getDatabase();
    }
    public static HistoryDataRecordDao getInstance() {
        if (historyDataRecordDao == null) {
            historyDataRecordDao = new HistoryDataRecordDao();
        }
        return historyDataRecordDao;
    }

    /*****************************************************************
     * 方法名  :  insert
     * 输入参数：HistoryDataRecord:需要插入的HistoryDataRecord对象；
     * ******** channelID:通道id（从0开始）
     ********** xList：需要插入的横坐标的集合
     ********** yList：二维图的纵坐标（与通道id对应）的集合
     * ********detectionValue：检测值的集合
     * ********denoisingValue：去噪值得集合
     * ********flawValue：缺陷值的集合
     * 输出参数：无
     * 功能描述: 将HistoryDataRecord对象存入数据库
     ******************************************************************/
    public void insert(HistoryDataRecordBean historyDataRecordBean, List<Double> xList, List<ArrayList<Double>> yList ,
                       List<ArrayList<Double>> detectionValue,List<ArrayList<Double>> denoisingValue,List<ArrayList<Double>> flawValue) {
        historyDataRecordBean.save();
        long recordId = historyDataRecordBean.getId();
        int channelCount = SystemParameter.getInstance().nChannelNumber;//插入这里取系统的通道数
        SQLiteDatabase database = LitePal.getDatabase();
        try {
            database.beginTransaction();   //开始事务
            for (int i = 0,length= xList.size()*yList.size();i <length; i++) {
                HistoryDataRecordDetailBean historyDataRecordDetailBean = new HistoryDataRecordDetailBean();
                historyDataRecordDetailBean.setRecordId(recordId);
                historyDataRecordDetailBean.setId(i+1);
                historyDataRecordDetailBean.setChaneelID(i%channelCount);
                historyDataRecordDetailBean.setValue_x(xList.get(i/channelCount));
                historyDataRecordDetailBean.setValue_y(yList.get(i%channelCount).get(i/channelCount));
                historyDataRecordDetailBean.setDetectionTime(new Date().toString());
                historyDataRecordDetailBean.setDetectionValue(detectionValue.get(i%channelCount).get(i/channelCount));
                historyDataRecordDetailBean.setDenoisingValue(denoisingValue.get(i%channelCount).get(i/channelCount));
                historyDataRecordDetailBean.setFlawValue(flawValue.get(i%channelCount).get(i/channelCount));
                HistoryDataRecordDetailDao.getInstance().save(historyDataRecordDetailBean);//保存到数据库
            }
            database.setTransactionSuccessful();//提交事务
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.endTransaction();
                database.close();
            }
        }
    }

    /*****************************************************************
     * 方法名  :  delete
     * 输入参数：id:需要删除的记录的ID
     * 功能描述:  删除编号为id的历史记录
     ******************************************************************/
    public void delete(long id) {
        DataSupport.deleteAll(HistoryDataRecordBean.class, "id == ?", String.valueOf(id));
    }

    /*****************************************************************
     * 方法名  :  queryIdByTitle
     * 功能描述:  按标题查询与标题对应的记录ID，（标题与ID应当是一对一的关系）
     ******************************************************************/
    public long queryIdByTitle(String title){
        List<HistoryDataRecordBean> recordBeenlist= DataSupport
                .where("title == ?", title)
                .find(HistoryDataRecordBean.class);
        if(recordBeenlist.size()==1) {
            return recordBeenlist.get(0).getId();
        }else{
            System.out.println("不存在与标题对应的记录");
        }
        return  0;
    }

    /*****************************************************************
     * 方法名  :  queryChannelNumByID
     * 功能描述:根据历史记录的ID查询历史纪录的通道数量（一个id对应一个通道数）
     ******************************************************************/
    public int queryChannelNumByID(long id){
        List<HistoryDataRecordBean> recordList= DataSupport
                .where("id == ?", String.valueOf(id))
                .find(HistoryDataRecordBean.class);
        if(recordList.size()==1) {
            return recordList.get(0).getChannelCount();
        }else{
            System.out.println("不存在与ID对应的通道数");
        }
        return 0;
    }

    /*****************************************************************
     * 方法名  :  queryChannelNumByID
     * 功能描述:根据历史记录的ID查询历史纪录的通道数量（一个id对应一个通道数）
     ******************************************************************/
    public HistoryDataRecordBean queryBeanByID(long id){
        List<HistoryDataRecordBean> recordList= DataSupport
                .where("id == ?", String.valueOf(id))
                .find(HistoryDataRecordBean.class);
        if(recordList.size()==1) {
            return recordList.get(0);
        }else{
            System.out.println("不存在与ID对应");
        }
        return null;
    }
    /*****************************************************************
     * 方法名  :  queryByDate
     * 功能描述:  按时间查询符合要求的历史数据记录
     ******************************************************************/
    public ArrayList<HistoryDataRecordBean> queryByDate(String year,String month,String day){
        ArrayList<HistoryDataRecordBean> result =new ArrayList<>();
        List<HistoryDataRecordBean> recordBeanlist= DataSupport.findAll(HistoryDataRecordBean.class);
        String date = year+month+day;
        for (int i = 0,length= recordBeanlist.size(); i <length; i++) {
            if(day == "——") {
                String detecctionTime =recordBeanlist.get(i).getDetectionTime();
                if(detecctionTime!=null) {
                    if (date.substring(0, 6).equals(detecctionTime.substring(0, 6)))
                        result.add(recordBeanlist.get(i));
                }
            }else {
                if (date.equals(recordBeanlist.get(i).getDetectionTime()))
                    result.add(recordBeanlist.get(i));
            }
        }
        return  result;
    }

    /*****************************************************************
     * 方法名  :  getTitleList
     * 功能描述:  按时间查询符合要求的历史数据记录
     ******************************************************************/
    public List<String> getTitleList(ArrayList<HistoryDataRecordBean> list,String day ) {
        ArrayList<String> titleList = new ArrayList<>();
        String date;
        if(list.size() == 0){
            titleList.add("");
            return titleList;
        }
        if(day == "——"){
            for (int i = 0,length=list.size(); i <length ; i++) {
                date = list.get(i).getDetectionTime();
                titleList.add(date.substring(0, 4) + "年" + date.substring(4, 6) + "月");
            }
        }else {
            for (int i = 0,length= list.size(); i <length; i++) {
                date = list.get(i).getDetectionTime();
                titleList.add(date.substring(0, 4) + "年" + date.substring(4, 6) + "月" + date.substring(6, 8) + "日");
            }
        }
        return titleList;
    }


    /*****************************************************************
     * 方法名  :  queryRecordDetailByRecordID
     * 输入参数：recordID:选中的那一行记录的ID ;channelNum:记录对应的通道数
     * 输出参数：返回存储横纵坐标的Map
     * 功能描述:  根据历史记录的ID查询在历史纪录明细表中的数据，返回原始数据的横坐标和纵坐标
     ******************************************************************/
    public Map<String, ArrayList<ArrayList<Double>>> queryRecordDetailByRecordID(long recordID,int channelNum){
        Map<String, ArrayList<ArrayList<Double>>> map = new HashMap<>();
        ArrayList<ArrayList<Double>>  xList = new ArrayList<ArrayList<Double>>();//存储横坐标
        ArrayList<ArrayList<Double>>  yList = new ArrayList<ArrayList<Double>>();//存储纵坐标
        ArrayList<ArrayList<Double>>  detectionValue = new ArrayList<ArrayList<Double>>();//存储横坐标
        ArrayList<ArrayList<Double>>  denoisingValue = new ArrayList<ArrayList<Double>>();//存储纵坐标
        ArrayList<ArrayList<Double>>  flawValue = new ArrayList<ArrayList<Double>>();//存储横坐标
        //初始化xList和yList
        xList.add(new ArrayList<Double>());
        for(int i=0;i<channelNum;i++){
            yList.add(new ArrayList<Double>());
            detectionValue.add(new ArrayList<Double>());
            denoisingValue.add(new ArrayList<Double>());
            flawValue.add(new ArrayList<Double>());
        }
        //由recordID查询记录获得结果集
        List<HistoryDataRecordDetailBean> detailBeenList=HistoryDataRecordDetailDao.getInstance().queryByRecordID(recordID);
        try {
            for (int i = 0, length = detailBeenList.size(); i < length; i++) {
                HistoryDataRecordDetailBean bean = detailBeenList.get(i);
                if (i%channelNum==0){
                    xList.get(0).add(bean.getValue_x());//保存横坐标
                }
                yList.get(i%channelNum).add(bean.getValue_y());
                detectionValue.get(i%channelNum).add(bean.getDetectionValue());
                denoisingValue.get(i%channelNum).add(bean.getDenoisingValue());
                flawValue.get(i%channelNum).add(bean.getFlawValue());
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        map.put("X",xList);//存储横坐标
        map.put("Y",yList);//存储纵坐标
        map.put("DV",detectionValue);//存储检测值
        map.put("DSV",denoisingValue);//存储去噪值
        map.put("FV",flawValue);//存储缺陷值
        return map;
    }

}

