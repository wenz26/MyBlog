package com.cwz.blog.defaultblog.aspect.annotation;

import java.lang.annotation.*;

/**
 * @author: 陈文振
 * @date: 2019/12/26
 * @description: 日志注解
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    /**
     * 系统模块（评论，文章点赞，个人信息，系统博客等等）
     */
    String module() default "";

    /**
     * 操作类型（新增、查找、修改、删除）
     */
    String operation() default "";

}
