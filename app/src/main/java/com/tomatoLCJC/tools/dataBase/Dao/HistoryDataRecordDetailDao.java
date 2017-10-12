package com.tomatoLCJC.tools.dataBase.Dao;

import com.tomatoLCJC.tools.dataBase.Bean.HistoryDataRecordDetailBean;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by ycq on 2017/9/13.
 */

/**
 * 对历史数据记录明细的数据库操作类
 */
public class HistoryDataRecordDetailDao {

    //生成单例
    private static HistoryDataRecordDetailDao historyDataRecordDetailDao = null;
    private HistoryDataRecordDetailDao() {
        LitePal.getDatabase();
    }
    public static HistoryDataRecordDetailDao getInstance() {
        if (historyDataRecordDetailDao == null) {
            historyDataRecordDetailDao = new HistoryDataRecordDetailDao();
        }
        return historyDataRecordDetailDao;
    }
    /*****************************************************************
     * 方法名  :  save
     * 输入参数：HistoryDataRecordDetailBean:需要插入的HistoryDataRecordDetailBean对象的引用
     * 输出参数：无
     * 功能描述: 将HistoryDataRecordDetailBean对象存入数据库
     ******************************************************************/
    public void save(HistoryDataRecordDetailBean bean){
        bean.save();
    }

    /*****************************************************************
     * 方法名  :  queryByRecordID
     * 输入参数：recordID:选中的那一行记录的ID
     * 输出参数：历史记录编号为recordID的记录的集合
     * 功能描述: 通过记录ID在记录明细表中查询符合要求的记录
     ******************************************************************/

    public List<HistoryDataRecordDetailBean> queryByRecordID(long recordID){
        List<HistoryDataRecordDetailBean> beanList= DataSupport
                .where("recordId == ?", String.valueOf(recordID))
                .find(HistoryDataRecordDetailBean.class);
        return beanList;
    }
}
