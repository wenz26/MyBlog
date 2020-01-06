package com.cwz.blog.defaultblog.security.handler;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: 陈文振
 * @date: 2020/1/2
 * @description: Spring Security自定义登录失败处理器
 */
@Component("failHandler")
public class FailHandler extends SimpleUrlAuthenticationFailureHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @LogAnnotation(module = "用户登录", operation = "登录失败")
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        logger.info("验证码或手机或密码错误，用户登录失败，请重新登录！！！");

        request.getSession().setAttribute("userLogin", "fail");

        super.onAuthenticationFailure(request, response, exception);
    }
}
