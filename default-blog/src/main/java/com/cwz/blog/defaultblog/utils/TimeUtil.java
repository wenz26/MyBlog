package com.cwz.blog.defaultblog.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author: 陈文振
 * @date: 2019/12/4
 * @description: 时间工具
 */
public class TimeUtil {

    /**
     * @description: 格式化日期
     * 使用线程安全的DateTimeFormatter
     * @author: 陈文振
     * @date: 2019/12/4
     * @return: “年-月-日”字符串
     */
    public static String getFormatDateForTwo(LocalDateTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return time.format(formatter);
    }

    /**
     * @description: 格式化日期
     * 使用线程安全的DateTimeFormatter
     * @author: 陈文振
     * @date: 2019/12/4
     * @return: “年-月-日”字符串
     */
    public static String getFormatDateForThree(LocalDateTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return time.format(formatter);
    }

    /**
     * @description: 格式化日期
     * @author: 陈文振
     * @date: 2019/12/4
     * @return: “年-月-日 时:分:秒”字符串
     */
    public static String getFormatDateForSix(LocalDateTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return time.format(formatter);
    }

    /**
     * @description: 格式化日期
     * @author: 陈文振
     * @date: 2019/12/4
     * @return: “年-月-日 时:分”字符串
     */
    public static String getFormatDateForFive(LocalDateTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return time.format(formatter);
    }

    /**
     * @description: 解析日期
     * @author: 陈文振
     * @date: 2019/12/4
     * @param date: 日期 2019-12-4
     * @return:
     */
    public static LocalDate getParseDateForThree(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

    /**
     * @description: 解析日期
     * @author: 陈文振
     * @date: 2019/12/4
     * @param date: 日期 2019-12-4 16:43:20
     * @return:
     */
    public static LocalDateTime getParseDateForSix(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    /**
     * @description: 获得当前时间的时间戳
     * @author: 陈文振
     * @date: 2019/12/4
     * @return: 时间戳
     */
    public long getLongTime(){
        Date now = new Date();
        return now.getTime()/1000;
    }

    /**
     * @description: 时间中横杆转换为年
     * 线程安全使用 StringBuffer
     * @author: 陈文振
     * @date: 2019/12/4
     * @param str: 2019-12
     * @return: 2019年12月
     */
    public String timeWhippletreeToYear(String str){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(str.substring(0, 4));
        stringBuffer.append("年");
        stringBuffer.append(str.substring(5,7));
        stringBuffer.append("月");
        return String.valueOf(stringBuffer);
    }

    /**
     * @description: 时间中的年转换为横杠
     * 线程安全使用 StringBuffer
     * @author: 陈文振
     * @date: 2019/12/4
     * @param str: 2019年12月
     * @return: 2019-12
     */
    public String timeYearToWhippletree(String str){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(str.substring(0, 4));
        stringBuffer.append("-");
        stringBuffer.append(str.substring(5,7));
        return String.valueOf(stringBuffer);
    }

}
