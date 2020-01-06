package com.cwz.blog.defaultblog.security.code.sms;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2020/1/3
 * @description: 这是对用手机验证码登录的方式  手机Authentication过滤器
 */
public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String CWZ_FORM_MOBILE_KEY = "mobile";

    /**
     * 在请求中 携带手机号的参数 (就是input中的name属性)
     */
    private String mobileParameter = CWZ_FORM_MOBILE_KEY;

    // 当前过滤器只处理post请求
    private boolean postOnly = true;

    public void setMobileParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.mobileParameter = usernameParameter;
    }

    public final String getMobileParameter() {
        return mobileParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }


    public SmsCodeAuthenticationFilter() {

        // 当前过滤器要处理的请求是什么 AntPathRequestMatcher: 请求的匹配器
        super(new AntPathRequestMatcher("/sms", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        // 当前请求不是post请求就抛异常
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("认证方法不支持：" + request.getMethod());
        }

        // 获取手机号
        String phone = request.getParameter(mobileParameter);

        if (Objects.isNull(phone)) {
            phone = "";
        }

        phone = phone.trim();

        // 实例化SmsCodeAuthenticationToken
        SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(phone);

        // Allow subclasses to set the "details" property
        // 把请求的信息设到Token(SmsCodeAuthenticationToken)里去
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

        // 实际上是把SmsCodeAuthenticationToken传到AuthenticationManager里去
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
