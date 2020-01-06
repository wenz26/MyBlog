package com.cwz.blog.defaultblog.security.code.filter;

import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.security.code.exception.CodeException;
import com.cwz.blog.defaultblog.security.code.sms.SmsCode;
import com.cwz.blog.defaultblog.utils.StringUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: 陈文振
 * @date: 2020/1/3
 * @description: 编写图片验证码的逻辑过滤器
 */
public class ImageCodeFilter extends OncePerRequestFilter implements InitializingBean {

    private HashRedisServiceImpl hashRedisService;

    private AuthenticationFailureHandler failureHandler;

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    public ImageCodeFilter(AuthenticationFailureHandler failureHandler, HashRedisServiceImpl hashRedisService){
        this.failureHandler = failureHandler;
        this.hashRedisService = hashRedisService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        boolean action = false;
        if (pathMatcher.match(CommonCodeFilterConfig.RELEASE_URL, request.getRequestURI())) {
            action = true;
        }

        if (action) {
            try {
                String id = (String) request.getSession().getAttribute("sessionId");
                String codeValue = request.getParameter("imageCode");

                SmsCode code = (SmsCode) hashRedisService.get(StringUtil.PREFIX_IMAGE_CODE, id);

                CommonCodeFilterConfig.validate(response, code, codeValue);

                hashRedisService.hashDelete(StringUtil.PREFIX_IMAGE_CODE, id);

            } catch (CodeException e) {
                failureHandler.onAuthenticationFailure(request, response, e);

                // 处理失败后，return，不往下走
                return ;
            }
        }

        // 如果不是登录请求，就不做任何处理，直接调用后面的过滤器
        filterChain.doFilter(request, response);
    }

}
