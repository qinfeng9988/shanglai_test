package com.qjfcc.test20210308.common;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: qinjiangfeng
 * @Date: 2021/5/5 09:56
 * @Description:
 */
public class DateTimeUtil {

    public static long queryTimeStamp(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis() / 1000;
    }

    public static Date now() {
        return new Date();
    }

    public static Date toDate(String dateTimeString){
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
           return simpleFormat.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date addSeconds(Date date,Integer seconds){
        return new DateTime(date).plusSeconds(seconds).toDate();
    }
}
