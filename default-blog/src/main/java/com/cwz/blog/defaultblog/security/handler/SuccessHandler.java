package com.cwz.blog.defaultblog.security.handler;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.mapper.UserMapper;
import com.cwz.blog.defaultblog.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author: 陈文振
 * @date: 2020/1/2
 * @description: Spring Security自定义登录成功处理器
 */
@Component("successHandler")
public class SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @LogAnnotation(module = "用户登录", operation = "登录成功")
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        logger.info("用户[{}]登录成功~~~", username);

        request.getSession().setAttribute("authentication", username);

        LocalDateTime localDateTime = LocalDateTime.now();
        userService.updateRecentlyLanded(username, localDateTime);

        request.getSession().setAttribute("userLogin", "success");

        String phone = userService.findPhoneByUsername(username);
        userMapper.updatePersistentLogins(username, phone);


        super.onAuthenticationSuccess(request, response, authentication);
    }
}
