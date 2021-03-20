package com.qjfcc.test20210308.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

public enum TimeIntervalEnum {
    MORNING(1, "早上"),
    AFTERNOON(2, "中午"),
    EVENING(3, "晚上"),
    ;
    private Integer code;
    private String message;

    TimeIntervalEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Date getStartTime() {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            LocalDate localDate = LocalDate.now();
            String dateString = localDate.getYear() + "-" + localDate.getMonthValue() + "-" + localDate.getDayOfMonth();
            switch (this.code) {
                case 1:
                    dateString += " 10:29:45";
                    break;
                case 2:
                    dateString += " 14:29:45";
                    break;
                default:
                    dateString += " 18:29:45";
                    break;
            }
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
