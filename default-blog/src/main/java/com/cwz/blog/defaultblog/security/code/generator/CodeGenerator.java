package com.cwz.blog.defaultblog.security.code.generator;

import com.cwz.blog.defaultblog.security.code.sms.SmsCode;

/**
 * @author: 陈文振
 * @date: 2020/1/1
 * @description: 验证码生成器接口
 */
public interface CodeGenerator {

    /**
     * @description: 生成验证码（因为 SmsCode 是父类，所以就返回一个父类）
     * @author: 陈文振
     * @date: 2020/1/1
     * @param
     * @return: com.cwz.blog.defaultblog.security.code.sms.SmsCode
     */
    SmsCode createCode();
}
