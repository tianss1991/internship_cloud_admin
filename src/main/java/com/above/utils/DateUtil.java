package com.above.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description: 日期工具类
 * @Author: LZH
 * @Date: 2022/2/9 10:15
 */
public class DateUtil {

    /**
     * @Description: 获取当月第一一天
     * @Author: LZH
     * @Date: 2022/2/9 10:18
     */
    public static Date getFirstDayOfMonth(Date month) throws ParseException {
        //输出格式
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //日历工具
        Calendar cal = Calendar.getInstance();
        cal.setTime(month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return newFormat.parse(newFormat.format(cal.getTime()));
    }

    /**
     * @Description: 获取当月最后一天
     * @Author: LZH
     * @Date: 2022/2/9 10:18
     */
    public static Date getMaxDayOfMonth(Date month) throws ParseException {
        //输出格式
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //日历工具
        Calendar cal = Calendar.getInstance();
        cal.setTime(month);
        cal.set(Calendar.DATE, 1);
        //主要就是这个roll方法
        cal.roll(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return newFormat.parse(newFormat.format(cal.getTime()));
    }


    /**
     * @Description: 获取次日
     * @Author: LZH
     * @Date: 2022/2/15 13:58
     */
    public static Date getNextDay(Date now){
        Calendar time = Calendar.getInstance();

        time.setTime(now);

        time.add(Calendar.DATE, 1);

        time.set(Calendar.HOUR_OF_DAY, 0);

        time.set(Calendar.MINUTE, 0);

        time.set(Calendar.SECOND, 0);

        time.set(Calendar.MILLISECOND, 0);

        return time.getTime();
    }
    /**
     * @Description: 两周后的时间
     * @Author: LZH
     * @Date: 2022/2/15 13:58
     */
    public static Date getTwoWekkAfter(Date now){
        Calendar time = Calendar.getInstance();

        time.setTime(now);

        time.add(Calendar.DATE, 15);

        time.set(Calendar.HOUR_OF_DAY, 0);

        time.set(Calendar.MINUTE, 0);

        time.set(Calendar.SECOND, 0);

        time.set(Calendar.MILLISECOND, 0);

        return time.getTime();
    }

    /**
     * @Description: 获取当天第一秒
     * @Author: LZH
     * @Date: 2022/2/19 10:20
     */
    public static Date getTodayFirstSecond(Date today){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * @Description: 获取当天12点
     * @Author: LZH
     * @Date: 2022/2/19 10:20
     */
    public static Date getTodaynoon(Date today){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * @Description: 获取当天最后一秒
     * @Author: LZH
     * @Date: 2022/2/19 10:21
     */
    public static Date getTodayLastSecond(Date today){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * @Description: 获取三天前的时间
     * @Author: LZH
     * @Date: 2022/2/19 10:21
     */
    public static Date getThreeDayAgo(Date today){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, -3);
        return calendar.getTime();
    }
    /**
     * @Description: 获取四天前的时间
     * @Author: LZH
     * @Date: 2022/2/19 10:21
     */
    public static Date getFourDayAgo(Date today){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, -4);
        return calendar.getTime();
    }
    /**
     * @Description: 获取三天后的时间
     * @Author: LZH
     * @Date: 2022/2/19 10:21
     */
    public static Date getThreeDayAfter(Date today){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, 3);
        return calendar.getTime();
    }

    /**
     * @Description: 获取几年后的今天
     * @Author: LZH
     * @Date: 2022/4/15 14:12
     */
    public static Date getafterYear(Date today,Integer year){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * @Description: 获取今天是星期几 星期一为 1 以此类推
     * @Author: LZH
     * @Date: 2022/4/15 14:12
     */
    public static int getWeek(Date today){
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(today);

        return calendar.get(Calendar.DAY_OF_WEEK)-1;
    }
    /**
    *@author: GG
    *@data: 2022/7/7 11:34
    *@function:获取时间戳
     * @param s
    */
    public static Long dateToStamp(Date s) throws ParseException {
        long ts = s.getTime();
        return ts;
    }

}
