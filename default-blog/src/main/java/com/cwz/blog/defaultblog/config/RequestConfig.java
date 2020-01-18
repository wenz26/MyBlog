package com.cwz.blog.defaultblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

/**
 * @author: 陈文振
 * @date: 2020/1/10
 * @description: request的一个配置类
 */
@Configuration
public class RequestConfig {

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
}
