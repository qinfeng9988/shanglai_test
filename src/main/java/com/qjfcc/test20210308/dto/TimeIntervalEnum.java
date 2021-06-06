package com.qjfcc.test20210308.dto;

import com.qjfcc.test20210308.common.DateTimeUtil;
import org.joda.time.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public enum TimeIntervalEnum {
    MORNING(1, "早上", 1, "10:30:00", "10:19:30", "lb"),
    AFTERNOON(2, "中午", 2, "14:30:00", "14:19:30", "lb"),
    EVENING(3, "晚上", 18, "18:30:00", "18:19:30", "qg"),
    ;
    private Integer code;
    private String message;
    private String time;
    private String vipTime;
    private Integer tid;

    public String getVisit() {
        return visit;
    }

    private String visit;

    TimeIntervalEnum(Integer code, String message, Integer tid, String time, String vipTime, String visit) {
        this.code = code;
        this.message = message;
        this.tid = tid;
        this.time = time;
        this.vipTime = vipTime;
        this.visit = visit;
    }

    public Integer getCode() {
        return code;
    }

    public Integer getTid() {
        return tid;
    }

    public String getMessage() {
        return message;
    }

    public Date getStartTime() {
        return getTime(false, LocalDate.now());
    }

    public Date getVipStartTime(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return getTime(true, date);
    }

    public Date getStartTime(Integer seconds) {
        Date normalTime = getTime(false,LocalDate.now());
        return DateTimeUtil.addSeconds(normalTime, seconds);
    }

    private Date getTime(boolean isVip, LocalDate date) {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            String dateString = date.year().getAsString() + "-" + date.monthOfYear().getAsString() + "-" + date.getDayOfMonth() + " ";
            dateString += isVip ? this.vipTime : this.time;
            return simpleFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TimeIntervalEnum convert(Integer code) {
        return Arrays.stream(values()).filter(t -> t.getCode().equals(code)).findFirst().orElse(null);
    }
}
