package com.wh.timeruler.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author wuhan
 * @date 2018/11/23 10:24
 */
public class DateUtils {

    private static final SimpleDateFormat sdf;

    static {
        sdf = new SimpleDateFormat("", Locale.getDefault());
    }
    /**
     * 获取当前时间的起点小时
     * @param currentTime
     * @return
     */
    public static long getTimeHour(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime * 1000));
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis()/1000;
    }

    public static String formatHourMinute(long time){
      return format("HH:mm",time);
    }

    public static String format(String pattern,long millis){
        sdf.applyPattern(pattern);
        if(millis < 8 * 60 * 60 * 1000){
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        }else {
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        }
        return sdf.format(millis);
    }

    public static String format(String pattern,Date date){
        sdf.applyPattern(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        return sdf.format(date);
    }
}
