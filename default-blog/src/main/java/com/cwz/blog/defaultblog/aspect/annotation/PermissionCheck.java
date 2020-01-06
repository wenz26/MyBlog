package com.cwz.blog.defaultblog.aspect.annotation;

import java.lang.annotation.*;

/**
 * @author: 陈文振
 * @date: 2020/1/4
 * @description: 拦截所有需要登录操作的注解类
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PermissionCheck {

    String value();
}
