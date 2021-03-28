package com.qjfcc.test20210308.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

public enum TimeIntervalEnum {
    MORNING(1, "早上", 1, "10:29:45", "10:19:45"),
    AFTERNOON(2, "中午", 2, "14:29:45", "14:19:45"),
    EVENING(3, "晚上", 18, "18:29:45", "18:19:45"),
    ;
    private Integer code;
    private String message;
    private String time;
    private String vipTime;

    public Integer getTid() {
        return tid;
    }

    private Integer tid;

    TimeIntervalEnum(Integer code, String message, Integer tid, String time, String vipTime) {
        this.code = code;
        this.message = message;
        this.tid = tid;
        this.time = time;
        this.vipTime = vipTime;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Date getStartTime() {
        return getTime(false);
    }

    public Date getVipStartTime() {
        return getTime(true);
    }

    private Date getTime(boolean isVip) {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            LocalDate localDate = LocalDate.now();
            String dateString = localDate.getYear() + "-" + localDate.getMonthValue() + "-" + localDate.getDayOfMonth() + " ";
            dateString += isVip ? this.vipTime : this.time;
            return simpleFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TimeIntervalEnum convert(Integer code) {
        return Arrays.stream(values()).filter(t -> t.getCode().equals(code)).findFirst().orElse(TimeIntervalEnum.MORNING);
    }
}
