package com.cwz.blog.defaultblog.security.code.sms;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author: 陈文振
 * @date: 2020/1/3
 * @description: 这是对用手机验证码登录的方式进行 Authentication提供器
 */
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * @description: 进行身份验证逻辑
     * @author: 陈文振
     * @date: 2020/1/3
     * @param authentication
     * @return: org.springframework.security.core.Authentication
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;

        // 拿到传入的手机号（手机号就是Principal）
        UserDetails user = userDetailsService.loadUserByUsername((String) authenticationToken.getPrincipal());

        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        // 传入进去 进行认证
        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(user, user.getAuthorities());

        // 把之前未认证的Token复制到已认证的Token结果里面去
        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    /**
     * @description: 在AuthenticationManager挑选一个Provider来出处理SmsCodeAuthenticationToken
     * @author: 陈文振
     * @date: 2020/1/3
     * @param aClass
     * @return: boolean
     */
    @Override
    public boolean supports(Class<?> aClass) {

        // 判定传进来的是不是SmsCodeAuthenticationToken
        return SmsCodeAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
