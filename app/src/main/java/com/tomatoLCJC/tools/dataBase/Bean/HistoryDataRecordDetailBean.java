package com.tomatoLCJC.tools.dataBase.Bean;

import org.litepal.crud.DataSupport;

/**
 * Created by ycq on 2017/9/5.
 */

/**
 * 历史数据记录明细
 */
public class HistoryDataRecordDetailBean extends DataSupport {
    private long id;// 编号
    private long recordId;//历史记录编号
    private long chaneelID;//通道ID
    private String detectionTime;//检测时间
    private double value_x;
    private double value_y;
    private double detectionValue;//检测值
    private double denoisingValue;//去噪值
    private double flawValue;//缺陷值

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public long getChaneelID() {
        return chaneelID;
    }

    public void setChaneelID(long chaneelID) {
        this.chaneelID = chaneelID;
    }

    public String getDetectionTime() {
        return detectionTime;
    }

    public void setDetectionTime(String detectionTime) {
        this.detectionTime = detectionTime;
    }

    public double getValue_x() {
        return value_x;
    }

    public void setValue_x(double value_x) {
        this.value_x = value_x;
    }

    public double getValue_y() {
        return value_y;
    }

    public void setValue_y(double value_y) {
        this.value_y = value_y;
    }

    public double getDetectionValue() {
        return detectionValue;
    }

    public void setDetectionValue(double detectionValue) {
        this.detectionValue = detectionValue;
    }

    public double getDenoisingValue() {
        return denoisingValue;
    }

    public void setDenoisingValue(double denoisingValue) {
        this.denoisingValue = denoisingValue;
    }

    public double getFlawValue() {
        return flawValue;
    }

    public void setFlawValue(double flawValue) {
        this.flawValue = flawValue;
    }
}
