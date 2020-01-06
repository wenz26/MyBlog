package com.cwz.blog.defaultblog.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2019/12/27
 * @description: IP工具类
 */
public class IpUtils {

    private static Logger logger = LoggerFactory.getLogger(IpUtils.class);

    public static String getIpAddr(HttpServletRequest request) {
        String ip = null, unknown = "unknown", separator = ",";
        int maxLength = 15;

        try {
            ip = request.getHeader("X-Requested-For");

            /*
             * equalsIgnoreCase将此String与另一个String进行比较，不考虑大小写。如果两个字符串的长度相等，并且两个字
             * 符串中的相应字符都相等（忽略大小写），则认为这两个字符串是相等的。
             */
            if (Objects.isNull(ip) || unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("X-Forwarded-For");
            }
            if (Objects.isNull(ip) || unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (Objects.isNull(ip) || ip.length() == 0 ||unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (Objects.isNull(ip) || unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (Objects.isNull(ip) || unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (Objects.isNull(ip) || unknown.equalsIgnoreCase(ip)){
                ip = request.getRemoteAddr();
            }

        } catch (Exception e) {
            logger.error("IP的地址获取出现错误：" + e);
        }

        // 如果是本机
        if (Objects.equals("0:0:0:0:0:0:0:1", ip)) {
            ip = "127.0.0.1";
        }

        // 使用代理，则获取第一个IP地址
        if (!Objects.isNull(ip) && ip.length() > maxLength) {

            int idx = ip.indexOf(separator);
            if (idx > 0) {
                ip = ip.substring(0, idx);
            }
        }
        return ip;
    }
}
