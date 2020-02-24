package com.cwz.blog.defaultblog.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2020/2/19
 * @description: 根据ip获得详细地址
 */
public class AddressUtils {

    public static String getAddress(String ip) {
        String url="http://ip-api.com/json/"+ ip +"?lang=zh-CN";
        String info = HttpClientUtils.httpGet(url);

        JSONObject infoJSON = JSONObject.parseObject(info);
        String city;

        if (Objects.equals(infoJSON.get("status"), "success")) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(infoJSON.get("country"));
            stringBuffer.append(infoJSON.get("regionName"));
            stringBuffer.append(infoJSON.get("city"));
            stringBuffer.append(", 纬度: " + infoJSON.get("lat"));
            stringBuffer.append(", 经度: " + infoJSON.get("lon"));

            city = stringBuffer.toString();
        } else {
            city = "开发环境地址";
        }

        return city;
    }
}
