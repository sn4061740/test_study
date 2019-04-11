package com.xcore.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static String Y_M_D_H_M_S="yyyy-MM-dd HH:mm:ss";
    public static String Y_M_D="yyyy-MM-dd";
    public static String H_M_S="HH:mm:ss";
    public static String M_D_H_M="MMdd-hhmm";//月日时分


    /**
     * 得到几分、几小时、几天前
     * @param rTime
     * @return
     */
    public static String getShortTime(long rTime) {//1547327225000
//        rTime=1547536649211L;
        String shortString = "";
        long now =Calendar.getInstance().getTimeInMillis();
        Date date =new Date();
        date.setTime(rTime);
//        try {
//            String dateStr = "2019-01-13 11:18:31";
//            //注意format的格式要与日期String的格式相匹配
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date date1 = sdf.parse(dateStr);
//            Log.e("TAG",date1.getTime()+"");
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }

        long delTime = (now - date.getTime()) / 1000;
        long mouth=30*24 * 60 * 60;
        if (delTime > 365 * 24 * 60 * 60) {
            shortString = (int) (delTime / (365 * 24 * 60 * 60)) + "年前";
        }else if(delTime>mouth){
            shortString=(int)(delTime/mouth)+"月前";
        } else if (delTime > 24 * 60 * 60) {
            shortString = (int) (delTime / (24 * 60 * 60)) + "天前";
        } else if (delTime > 60 * 60) {
            shortString = (int) (delTime / (60 * 60)) + "小时前";
        } else if (delTime > 60) {
            shortString = (int) (delTime / (60)) + "分前";
        } else if (delTime > 1) {
            shortString = delTime + "秒前";
        } else {
            shortString = "1秒前";
        }
        return shortString;
    }

    //时间转换
    public static String getH_M_S(long totalTime) {
        long hour = 0;
        long minute = 0;
        long second = totalTime / 1000;
        if (totalTime <= 1000 && totalTime > 0) {
            second = 1;
        }
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        // 转换时分秒 00:00:00
        String str="";
        if(hour>=10){
            str=hour+":";
        }else{
            str="0"+hour+":";
        }
        if(minute>=10){
            str+=minute+":";
        }else{
            str+="0"+minute+":";
        }
        if(second>=10){
            str+=second+"";
        }else{
            str+="0"+second+"";
        }
        return str;
    }

    /**
     * 当前时间戳得到
     * @param
     * @return
     */
    public static String getDate(String format){
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        String sd = sdf.format(new Date(System.currentTimeMillis()));      // 时间戳转换成时间
        return sd;
    }

    /**
     * 时间戳转成时间
     * @param
     * @return
     */
    public static String getDate(String format,long date){
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        String sd = sdf.format(new Date(date));// 时间戳转换成时间
        return sd;
    }

    /**
     * 得到系统当前日期的前或者后几天
     *
     * @param iDate
     *                如果要获得前几天日期，该参数为负数； 如果要获得后几天日期，该参数为正数
     * @see java.util.Calendar#add(int, int)
     * @return Date 返回系统当前日期的前或者后几天
     */
    public static Date getDateBeforeOrAfter(int iDate) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, iDate);
        return cal.getTime();
    }

    /**
     * 得到日期的前或者后几天
     *
     * @param iDate
     *     如果要获得前几天日期，该参数为负数； 如果要获得后几天日期，该参数为正数
     * @see java.util.Calendar#add(int, int)
     * @return Date 返回参数<code>curDate</code>定义日期的前或者后几天
     */
    public static Date getDateBeforeOrAfter(Date curDate, int iDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DAY_OF_MONTH, iDate);
        return cal.getTime();
    }

    /**
     * 把时间戳转成 分钟.秒
     * @param time
     * @return
     */
    public static String getM_S(long time){
        long s=time%60;//得到剩余的秒数
        int m=(int)time/60;//得到分钟数
        return m+"分钟"+s+"秒";
    }

    /**
     * 把时间戳转成分钟
     * @param time
     * @return
     */
    public static String getMiss(long time){
        double m=time*1.0/1000/60;//
        String value=String.format("%.1f", m);
        return  value+"分钟";
    }

}
