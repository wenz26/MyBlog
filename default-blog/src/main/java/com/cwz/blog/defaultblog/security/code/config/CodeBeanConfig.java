package com.cwz.blog.defaultblog.security.code.config;

import com.cwz.blog.defaultblog.security.code.generator.CodeGenerator;
import com.cwz.blog.defaultblog.security.code.generator.impl.ImageCodeGenerator;
import com.cwz.blog.defaultblog.security.code.generator.impl.SmsCodeGenerator;
import com.cwz.blog.defaultblog.security.code.sms.SmsCodeSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: 陈文振
 * @date: 2020/1/1
 * @description: 验证码配置类
 */
@Configuration
public class CodeBeanConfig {

    @Bean("imageCodeGenerator")
    public CodeGenerator imageCodeGenerator() {
        return new ImageCodeGenerator();
    }

    @Bean("smsCodeGenerator")
    public CodeGenerator smsCodeGenerator() {
        return new SmsCodeGenerator();
    }

    @Bean("smsCodeSender")
    public SmsCodeSender smsCodeSender() {
        return new SmsCodeSender();
    }

}
