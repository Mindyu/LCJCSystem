package com.tomatoLCJC.tools.dataBase.Dao;

import android.content.ContentValues;

import com.tomatoLCJC.main.utils.Point;
import com.tomatoLCJC.main.utils.Quadratic;
import com.tomatoLCJC.tools.Parameter.SystemParameter;
import com.tomatoLCJC.tools.dataBase.Bean.DeviceDetectionRecordBean;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 对通道校准记录的数据库操作类
 */
public class DeviceDetectionRecordDao {
    //存储方法对象的集合
    private List<Method> setDefectMethodList = new ArrayList<>();
    private List<Method> setDefectMethodXList =new ArrayList<>();
    private List<Method> setDefectMethodYList = new ArrayList<>();
    //生成单例
    private static DeviceDetectionRecordDao deviceDetectionRecordDao = null;
    private DeviceDetectionRecordDao() {    //构造函数，先把数据库中的数据取出来
        LitePal.getDatabase();
        try {
            Class<?> CDR_class = DeviceDetectionRecordBean.class;
            for (int i = 1; i <= 4; i++) {
                Method setDefectPercent = CDR_class.getMethod(
                        "setDefectPercent"+i, double.class);//这里注意不能写成Double.class（不支持自动拆箱）
                Method setDefectPercent_x= CDR_class.getMethod(
                        "setDefectPercent"+i+"_x", double.class);
                Method setDefectPercent_value = CDR_class.getMethod(
                        "setDefectPercent"+i+"_value", double.class);
                setDefectMethodList.add(setDefectPercent);
                setDefectMethodXList.add(setDefectPercent_x);
                setDefectMethodYList.add(setDefectPercent_value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DeviceDetectionRecordDao getInstance() {
        if (deviceDetectionRecordDao == null) {
            deviceDetectionRecordDao = new DeviceDetectionRecordDao();
        }
        return deviceDetectionRecordDao;
    }

    /*****************************************************************
     * 方法名  :  getStatusIsZeroBean
     * 功能描述: 得到状态为0的那条记录对应的对象
     ******************************************************************/
    public DeviceDetectionRecordBean getStatusIsZeroBean(){
        DeviceDetectionRecordBean bean = DataSupport
                .where("status == ?", String.valueOf(0))
                .find(DeviceDetectionRecordBean.class).get(0);   //状态为0的记录只有一条
        return bean;
    }


    /************************************************************************************
     * 方法名  :  initChannelDetectionRecordTable
     * 功能描述: 初始状态默认向通道校准记录明细表中插入一行数据(默认K值全为1，零值全为0)
     ************************************************************************************/
    public void initChannelDetectionRecordTable(){
        int rowCount = DataSupport.count(DeviceDetectionRecordBean.class);
        if (rowCount == 0) {
            DeviceDetectionRecordBean chanDeteRecordBean= new DeviceDetectionRecordBean();
            Date date = new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSS");
            String detectionTime = sdf.format(date);
            chanDeteRecordBean.setDetectionTime(detectionTime);
            chanDeteRecordBean.setChannelCount(6);
            chanDeteRecordBean.setChannelWeight(1);
            chanDeteRecordBean.setChannelDistance(1);
            chanDeteRecordBean.setStepDistance(0.942);
            chanDeteRecordBean.setSteelTexture("材质");
            chanDeteRecordBean.setSteelThickness(10.0);   //钢管厚度
            chanDeteRecordBean.setUpliftValue(9);         //提离值
            chanDeteRecordBean.setDefectThreshold(2);         //缺陷阈值
            chanDeteRecordBean.setStatus(0);
            chanDeteRecordBean.setNote("在用");
            chanDeteRecordBean.setDefectPercent1(20);
            chanDeteRecordBean.setDefectPercent1_x(100);
            chanDeteRecordBean.setDefectPercent1_value(2530);
            chanDeteRecordBean.setDefectPercent2(40);
            chanDeteRecordBean.setDefectPercent2_x(200);
            chanDeteRecordBean.setDefectPercent2_value(2570);
            chanDeteRecordBean.setDefectPercent3(60);
            chanDeteRecordBean.setDefectPercent3_x(300);
            chanDeteRecordBean.setDefectPercent3_value(2710);
            chanDeteRecordBean.setDefectPercent4(80);
            chanDeteRecordBean.setDefectPercent4_x(400);
            chanDeteRecordBean.setDefectPercent4_value(2900);
            Quadratic quadratic =new Quadratic();
            quadratic.Fitting(new Point(0,0),new Point(40,20),new Point(80,40));
            chanDeteRecordBean.setValue_a(quadratic.getA());
            chanDeteRecordBean.setValue_b(quadratic.getB());
            quadratic.Fitting(new Point(80,40),new Point(180,60),new Point(370,80));
            chanDeteRecordBean.setValue_c(quadratic.getA());
            chanDeteRecordBean.setValue_d(quadratic.getB());
            chanDeteRecordBean.setValue_e(quadratic.getC());
            chanDeteRecordBean.save();
        }
    }


    /**********************************************************************************************
     * 方法名  :  updateDetectionData_secondWay
     * 功能描述: 更新校准数据第二种方法,将状态为0的那一行数据取出，更新相应通道K值和零值,并将上一条置为历史
     **********************************************************************************************/
    public void updateDetectionData(DeviceDetectionRecordBean bean ){
        //更新上一条数据
        DeviceDetectionRecordBean cDR = getStatusIsZeroBean();
        long id =cDR.getId();
        cDR.setStatus(1);//将上一条置为历史
        cDR.setNote("历史");
        cDR.update(id);
        //插入新数据
        Date date = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSS");
        String detectionTime = sdf.format(date);
        bean.setChannelCount(SystemParameter.getInstance().nChannelNumber);         //数量
        bean.setChannelDistance(SystemParameter.getInstance().nChannelDistance);    //间距
        bean.setChannelWeight(SystemParameter.getInstance().channelWeight);    //分量
        bean.setStepDistance(SystemParameter.getInstance().disSensorStepLen);    //步长
        //缺陷阈值
        bean.setDetectionTime(detectionTime);
        bean.setStatus(0);//将当前状态置为在用
        bean.setNote("在用");
        bean.save();//插入一行新数据
    }


    /*****************************************************************
     * 方法名  :  delete
     * 功能描述: 根据ID删除一组数据(其实是将其状态置为2)
     ******************************************************************/
    public void delete(long id) {
        ContentValues cv = new ContentValues();
        cv.put("status", 2);
        DataSupport.update(DeviceDetectionRecordBean.class, cv, id);
    }

    /*****************************************************************
     * 方法名  :  queryAll
     * 功能描述: 得到所有状态的的对象
     ******************************************************************/
    public List<DeviceDetectionRecordBean> queryAll(){
        return DataSupport.findAll(DeviceDetectionRecordBean.class);
    }

}
