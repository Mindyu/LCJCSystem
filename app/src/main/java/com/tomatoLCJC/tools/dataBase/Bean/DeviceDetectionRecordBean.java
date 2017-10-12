package com.tomatoLCJC.tools.dataBase.Bean;

import org.litepal.crud.DataSupport;

/**
 * Created by ycq on 2017/9/5.
 */

/**
 * 设备校准记录
 */
public class DeviceDetectionRecordBean extends DataSupport {
    private long id;// 校准编号
    private String detectionTime;//检测时间
    private int channelCount;// 通道数量
    private int channelWeight;// 通道分量
    private int channelDistance;// 通道间距
    private double stepDistance;// 步长
    private String steelTexture;//材质
    private double steelThickness;//厚度
    private double upliftValue;//提离值
    private double defectThreshold;//缺陷阈值
    private double defectPercent1;//缺陷百分比1
    private double defectPercent1_x;//缺陷百分比1x坐标
    private double defectPercent1_value;//缺陷百分比1去噪值
    private double defectPercent2;//缺陷百分比2
    private double defectPercent2_x;//缺陷百分比2x坐标
    private double defectPercent2_value;//缺陷百分比2去噪值
    private double defectPercent3;//缺陷百分比3
    private double defectPercent3_x;//缺陷百分比3x坐标
    private double defectPercent3_value;//缺陷百分比3去噪值
    private double defectPercent4;//缺陷百分比4
    private double defectPercent4_x;//缺陷百分比4x坐标
    private double defectPercent4_value;//缺陷百分比4去噪值
    private double value_a;    //y=ax^2+bx
    private double value_b;
    private double value_c;    //y=cx^2+dx+e
    private double value_d;
    private double value_e;
    private int status;
    private String note;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDetectionTime() {
        return detectionTime;
    }

    public void setDetectionTime(String detectionTime) {
        this.detectionTime = detectionTime;
    }

    public int getChannelCount() {
        return channelCount;
    }

    public void setChannelCount(int channelCount) {
        this.channelCount = channelCount;
    }

    public int getChannelWeight() {
        return channelWeight;
    }

    public void setChannelWeight(int channelWeight) {
        this.channelWeight = channelWeight;
    }

    public int getChannelDistance() {
        return channelDistance;
    }

    public void setChannelDistance(int channelDistance) {
        this.channelDistance = channelDistance;
    }

    public double getStepDistance() {
        return stepDistance;
    }

    public void setStepDistance(double stepDistance) {
        this.stepDistance = stepDistance;
    }

    public String getSteelTexture() {
        return steelTexture;
    }

    public void setSteelTexture(String steelTexture) {
        this.steelTexture = steelTexture;
    }

    public double getSteelThickness() {
        return steelThickness;
    }

    public void setSteelThickness(double steelThickness) {
        this.steelThickness = steelThickness;
    }

    public double getUpliftValue() {
        return upliftValue;
    }

    public void setUpliftValue(double upliftValue) {
        this.upliftValue = upliftValue;
    }

    public double getDefectThreshold() {
        return defectThreshold;
    }

    public void setDefectThreshold(double defectThreshold) {
        this.defectThreshold = defectThreshold;
    }

    public double getDefectPercent1() {
        return defectPercent1;
    }

    public void setDefectPercent1(double defectPercent1) {
        this.defectPercent1 = defectPercent1;
    }

    public double getDefectPercent1_x() {
        return defectPercent1_x;
    }

    public void setDefectPercent1_x(double defectPercent1_x) {
        this.defectPercent1_x = defectPercent1_x;
    }

    public double getDefectPercent1_value() {
        return defectPercent1_value;
    }

    public void setDefectPercent1_value(double defectPercent1_value) {
        this.defectPercent1_value = defectPercent1_value;
    }

    public double getDefectPercent2() {
        return defectPercent2;
    }

    public void setDefectPercent2(double defectPercent2) {
        this.defectPercent2 = defectPercent2;
    }

    public double getDefectPercent2_x() {
        return defectPercent2_x;
    }

    public void setDefectPercent2_x(double defectPercent2_x) {
        this.defectPercent2_x = defectPercent2_x;
    }

    public double getDefectPercent2_value() {
        return defectPercent2_value;
    }

    public void setDefectPercent2_value(double defectPercent2_value) {
        this.defectPercent2_value = defectPercent2_value;
    }

    public double getDefectPercent3() {
        return defectPercent3;
    }

    public void setDefectPercent3(double defectPercent3) {
        this.defectPercent3 = defectPercent3;
    }

    public double getDefectPercent3_x() {
        return defectPercent3_x;
    }

    public void setDefectPercent3_x(double defectPercent3_x) {
        this.defectPercent3_x = defectPercent3_x;
    }

    public double getDefectPercent3_value() {
        return defectPercent3_value;
    }

    public void setDefectPercent3_value(double defectPercent3_value) {
        this.defectPercent3_value = defectPercent3_value;
    }

    public double getDefectPercent4() {
        return defectPercent4;
    }

    public void setDefectPercent4(double defectPercent4) {
        this.defectPercent4 = defectPercent4;
    }

    public double getDefectPercent4_x() {
        return defectPercent4_x;
    }

    public void setDefectPercent4_x(double defectPercent4_x) {
        this.defectPercent4_x = defectPercent4_x;
    }

    public double getDefectPercent4_value() {
        return defectPercent4_value;
    }

    public void setDefectPercent4_value(double defectPercent4_value) {
        this.defectPercent4_value = defectPercent4_value;
    }

    public double getValue_a() {
        return value_a;
    }

    public void setValue_a(double value_a) {
        this.value_a = value_a;
    }

    public double getValue_b() {
        return value_b;
    }

    public void setValue_b(double value_b) {
        this.value_b = value_b;
    }

    public double getValue_c() {
        return value_c;
    }

    public void setValue_c(double value_c) {
        this.value_c = value_c;
    }

    public double getValue_d() {
        return value_d;
    }

    public void setValue_d(double value_d) {
        this.value_d = value_d;
    }

    public double getValue_e() {
        return value_e;
    }

    public void setValue_e(double value_e) {
        this.value_e = value_e;
    }

    public String getNote() {
        return note;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
