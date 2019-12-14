package com.cwz.blog.defaultblog.component;

/**
 * @author: 陈文振
 * @date: 2019/12/7
 * @description: 字符串与字符串数组之间的转换
 */
public class StringAndArray {

    /**
     * @description: 字符串转换成字符串数组
     * @author: 陈文振
     * @date: 2019/12/7
     * @param str: 字符串
     * @return: 转换后的字符串数组
     */
    public static String[] stringToArray(String str){
        String[] array = str.split(",");
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }
        return array;
    }

    /**
     * @description: 字符串数组拼接成字符串
     * 线程安全用 StringBuffer
     * @author: 陈文振
     * @date: 2019/12/7
     * @param articleTags: 字符串数组
     * @return: 拼接后的字符串
     */
    public static String arrayToString(String[] articleTags) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String articleTag : articleTags) {
            if (stringBuffer.length() == 0) {
                stringBuffer.append(articleTag.trim());
            } else {
                stringBuffer.append(",").append(articleTag.trim());
            }
        }
        return stringBuffer.toString();
    }
}
