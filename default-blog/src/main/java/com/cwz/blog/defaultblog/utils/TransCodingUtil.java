package com.cwz.blog.defaultblog.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author: 陈文振
 * @date: 2019/12/6
 * @description: 转码工具
 * 解决 网页数据传输时 中文乱码的问题
 */
public class TransCodingUtil {

    /**
     * @description: 中文转unicode编码
     * @author: 陈文振
     * @date: 2019/12/6
     * @param gbString 汉字
     * @return: unicode编码
     */
    public static String stringToUnicode(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            // 将一个整型转成一个十六进制数
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    /**
     * @description: unicode编码转中文
     * 线程安全用 StringBuffer
     * @author: 陈文振
     * @date: 2019/12/7
     * @param dataStr: unicode编码
     * @return: 中文
     */
    public static String unicodeToString(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            // 用 序列2 开始找第一个以 "\\u" 结尾的地方
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            // 16进制parse整形字符串
            char letter = (char) Integer.parseInt(charStr, 16);
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    /**
     * @description: 将 utf-8 展开的16进制数转换成 utf-8汉字
     * @author: 陈文振
     * @date: 2019/12/7
     * @param strUtf16: 16进制数
     * @return:
     */
    public static String utf16ToUtf8(String strUtf16) throws UnsupportedEncodingException {
        // URLDecoder：HTML 格式解码的实用工具类
        // decode 使用指定的编码机制对 application/x-www-form-urlencoded 字符串解码
        String strUtf8 = URLDecoder.decode(strUtf16, "UTF-8");
        return  strUtf8;
    }

    /**
     * @description: 判断是否为汉字
     * @author: 陈文振
     * @date: 2019/12/7
     * @param str:
     * @return:
     */
    public static boolean isChinese(String str) {
        for (int i =0; i < str.length(); i++) {
            int charToNum = str.charAt(i);
            // 汉字范围
            if (charToNum >= 19968 && charToNum <= 171941) {
                return true;
            }
        }
        return false;
    }

}
