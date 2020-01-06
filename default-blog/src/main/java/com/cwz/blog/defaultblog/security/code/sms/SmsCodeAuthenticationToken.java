package com.cwz.blog.defaultblog.security.code.sms;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author: 陈文振
 * @date: 2020/1/3
 * @description: 封装登录信息
 * 在身份认证之前里面传的是手机号，认证之后是用户的身份信息
 */
public class SmsCodeAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    public SmsCodeAuthenticationToken(String phone) {
        super(null);

        // 没登录的时候 principal这里面放的是手机号
        this.principal = phone;
        // 是否认证: false
        setAuthenticated(false);
    }

    public SmsCodeAuthenticationToken(Object principal,
                                      Collection<? extends GrantedAuthority> authorities) {
        super(authorities);

        // 登录成功后 principal里面放用户信息
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
