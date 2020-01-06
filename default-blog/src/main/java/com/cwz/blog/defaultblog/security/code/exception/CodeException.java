package com.cwz.blog.defaultblog.security.code.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author: 陈文振
 * @date: 2020/1/3
 * @description: 验证码的异常处理
 */
public class CodeException extends AuthenticationException {

    public CodeException(String msg){
        super(msg);
        System.out.println(msg);
    }
}
