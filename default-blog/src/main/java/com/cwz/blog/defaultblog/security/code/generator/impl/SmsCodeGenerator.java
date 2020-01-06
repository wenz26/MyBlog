package com.cwz.blog.defaultblog.security.code.generator.impl;

import com.cwz.blog.defaultblog.security.code.generator.CodeGenerator;
import com.cwz.blog.defaultblog.security.code.sms.SmsCode;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author: 陈文振
 * @date: 2020/1/1
 * @description: 短信验证码生成器实现类
 */
public class SmsCodeGenerator implements CodeGenerator {

    @Override
    public SmsCode createCode() {
        // 生成 6位长的短信验证码
        String code = RandomStringUtils.randomNumeric(6);

        // 我在这里设置验证码120秒后过期
        return new SmsCode(code, 120);
    }
}
