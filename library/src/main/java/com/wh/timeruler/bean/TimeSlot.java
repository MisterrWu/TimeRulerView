package com.wh.timeruler.bean;

/**
 * 
 * @author wuhan
 * @date 2018/11/22 19:17
 */
public class TimeSlot {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_ALARM = 1;
    /**
     * 开始时间
     */
    private long startTimeSecond;

    private long len;
    /**
     * 结束时间
     */
    private long endTimeSecond;

    private int type;

    private String showTime;

    public int getType() {
        return type;
    }

    public int getColor() {
        /*if (type == 0) {
            return 0x335FA7FE;
        }*/
        return 0x335FA7FE;
    }

    public void setStartTimeSecond(long startTimeSecond) {
        this.startTimeSecond = startTimeSecond;
        endTimeSecond = startTimeSecond + len;
    }

    public void setLen(long len) {
        this.len = len;
        endTimeSecond = startTimeSecond + len;
    }

    public long getLen() {
        return len;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 获取开始时间.
     *
     * @return
     */
    public long getStartTimeSecond() {
        return startTimeSecond;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getShowTime() {
        return showTime;
    }

    /**
     * 获取结束时间
     *
     * @return
     */
    public long getEndTimeSecond() {
        return endTimeSecond;
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "startTime = " + getStartTimeSecond() +
                ", endTime = " + getEndTimeSecond() +
                ", type = " +getType()+
                '}';
    }
}
