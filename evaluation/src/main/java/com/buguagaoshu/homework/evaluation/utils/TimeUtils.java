package com.buguagaoshu.homework.evaluation.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-04 23:47
 */
@Slf4j
public class TimeUtils {

    public final static long MINUTE = 60000;

    public final static long HOUR = 3600000;

    public final static String FORMAT_STRING1 = "yyyy-MM-dd";

    public final static String FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

    public final static String FORMAT_STRING2 = "EEE MMM dd yyyy HH:mm:ss z";

    public final static String[] REPLACE_STRING = new String[]{"GMT+0800", "GMT+08:00"};

    public final static String SPLIT_STRING = "(中国标准时间)";

    /**
     * 格式化时间
     * */
    public static String formatTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_STRING);
        return simpleDateFormat.format(time);
    }

    /**
     * 解析接收到的时间字符
     * */
    public static Long parseTimeZone(String dateString) {
        try {
            dateString = dateString.split(Pattern.quote(SPLIT_STRING))[0].replace(REPLACE_STRING[0], REPLACE_STRING[1]);
            //转换为date
            SimpleDateFormat sf1 = new SimpleDateFormat(FORMAT_STRING2, Locale.ENGLISH);
            Date date = sf1.parse(dateString);
            return date.getTime();
        } catch (Exception e) {
            throw new RuntimeException("时间转化格式错误" + "[dateString=" + dateString + "]" + "[FORMAT_STRING=" + FORMAT_STRING + "]");
        }
    }

    /**
     * 解析 yyyy-MM-dd HH:mm:ss 格式时间
     * */
    public static Long parseTime(String dateSting) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_STRING);
            Date date = simpleDateFormat.parse(dateSting);
            return date.getTime();
        } catch (Exception e) {
            log.warn("时间转化格式错误:  {}" + dateSting);
            throw new RuntimeException("时间转化格式错误: " + dateSting);
        }
    }

    /**
     * 解析 yyyy-MM-dd 格式时间
     * */
    public static Long parseTimeNoHour(String dateSting) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_STRING1);
            Date date = simpleDateFormat.parse(dateSting);
            return date.getTime();
        } catch (Exception e) {
            log.warn("时间转化格式错误:  {}" + dateSting);
            throw new RuntimeException("时间转化格式错误: " + dateSting);
        }
    }
}
