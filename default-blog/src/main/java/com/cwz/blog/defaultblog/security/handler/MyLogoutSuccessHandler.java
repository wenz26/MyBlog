package com.cwz.blog.defaultblog.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.UserLog;
import com.cwz.blog.defaultblog.service.UserLogService;
import com.cwz.blog.defaultblog.utils.AddressUtils;
import com.cwz.blog.defaultblog.utils.HttpClientUtils;
import com.cwz.blog.defaultblog.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author: 陈文振
 * @date: 2020/1/2
 * @description: 用户退出成功处理器
 */
@Component("myLogoutSuccessHandler")
public class MyLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Autowired
    private UserLogService userLogService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        LocalDateTime localDateTime = LocalDateTime.now();

        UserLog userLog = new UserLog();
        userLog.setLogUsername(username);
        userLog.setLogModule("用户注销");
        userLog.setLogOperation("注销成功");
        userLog.setLogMethod("com.cwz.blog.defaultblog.security.handler.MyLogoutSuccessHandler.onLogoutSuccess()");
        userLog.setLogParams("[null,null,null]");

        String ipAddr = IpUtils.getIpAddr(request);
        userLog.setLogIp(ipAddr);

        // 获取设置用户实际地址
        String address = AddressUtils.getAddress(ipAddr);
        //System.out.println(address);
        userLog.setLogAddress(address);

        userLog.setLogStatus("0");
        userLog.setLogTimeConsuming(0L);
        userLog.setCreateDate(localDateTime);
        userLogService.saveUserLog(userLog);

        logger.info("用户[{}]注销成功~~~", username);

        super.onLogoutSuccess(request, response, authentication);
    }
}
