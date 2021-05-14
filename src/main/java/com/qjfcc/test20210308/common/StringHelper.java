package com.qjfcc.test20210308.common;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class StringHelper {
    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    private static String md5Value(Map<String, String> maps) {
        List<String> valueList = new ArrayList<>();
        valueList.add("fcc07107a2848a96bef7d58ed70701ec");
        for (Map.Entry<String, String> v : maps.entrySet()) {
            if (StringUtils.isNotBlank(v.getValue())) {
                valueList.add(v.getValue());
            }
        }
        valueList.sort(String::compareTo);
        String rawValues = String.join("", valueList);
        return md5(rawValues);
    }

    public static String parameterJoin(Map<String, String> maps) {
        StringBuilder result = new StringBuilder();
        String sign = md5Value(maps);
        maps.put("app_key", sign);
        for (Map.Entry<String, String> v : maps.entrySet()) {
            if (StringUtils.isNotBlank(v.getValue())) {
                result.append("&").append(v.getKey()).append("=").append(v.getValue());
            }
        }
        if (result.length() > 1) {
            return result.substring(1, result.length());
        }
        return "";
    }

    private static String md5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");

            // 通过md5计算摘要,返回一个字节数组
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // 再将字节数编码为用a-z A-Z 0-9 / *一共64个字符表示的要存储到数据库的字符串，所以又叫BASE64编码算法
            return byteArrayToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 发生异常返回空
        return null;
    }

    private static String byteArrayToString(byte[] secretByte) {
        //将得到的字节数组变成字符串返回
        String md5Code = new BigInteger(1, secretByte).toString(16);
        StringBuilder code = new StringBuilder(md5Code);
        int count = 32 - code.length();
        for (int i = 0; i < count; i++) {
            code.insert(0, "0");
        }
        return code.toString();
    }
}
