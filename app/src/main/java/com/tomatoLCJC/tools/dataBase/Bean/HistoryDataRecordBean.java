package com.tomatoLCJC.tools.dataBase.Bean;
import org.litepal.crud.DataSupport;

/**
 *  历史数据记录
 */
public class HistoryDataRecordBean extends DataSupport {
    private long id;// 编号
    private String title;//标题
    private String detectionTime;// 检测时间
    private String detectionMan;// 检测员
    private int channelCount;// 通道数量
    private int channelWeight;// 通道分量
    private double stepDistance;// 步长
    private int stepInterval; //横向梯度步数间隔
    private int channelInterval;// 通道间距
    private long calibrationID;//校准编号
    private double maxFlawValue;//最大缺陷值
    private String note;// 备注

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetectionTime() {
        return detectionTime;
    }

    public void setDetectionTime(String detectionTime) {
        this.detectionTime = detectionTime;
    }

    public String getDetectionMan() {
        return detectionMan;
    }

    public void setDetectionMan(String detectionMan) {
        this.detectionMan = detectionMan;
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

    public double getStepDistance() {
        return stepDistance;
    }

    public void setStepDistance(double stepDistance) {
        this.stepDistance = stepDistance;
    }

    public int getStepInterval() {
        return stepInterval;
    }

    public void setStepInterval(int stepInterval) {
        this.stepInterval = stepInterval;
    }

    public int getChannelInterval() {
        return channelInterval;
    }

    public void setChannelDistance(int channelDistance) {
        this.channelInterval = channelDistance;
    }

    public long getCalibrationID() {
        return calibrationID;
    }

    public void setCalibrationID(long calibrationID) {
        this.calibrationID = calibrationID;
    }

    public double getMaxFlawValue() {
        return maxFlawValue;
    }

    public void setMaxFlawValue(double maxFlawValue) {
        this.maxFlawValue = maxFlawValue;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
