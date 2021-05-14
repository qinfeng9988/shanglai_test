package com.qjfcc.test20210308.common;

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
}
