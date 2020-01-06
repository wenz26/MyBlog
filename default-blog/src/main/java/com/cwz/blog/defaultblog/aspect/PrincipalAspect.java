package com.cwz.blog.defaultblog.aspect;

import com.cwz.blog.defaultblog.aspect.annotation.PermissionCheck;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.service.UserLogService;
import com.cwz.blog.defaultblog.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2020/1/4
 * @description: 定义切面，拦截所有需要登录操作的controller接口
 */
@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Component
public class PrincipalAspect {

    @Autowired
    private UserLogService userLogService;

    /**
     * 匿名用户
     */
    public static final String ANONYMOUS_USER = "anonymousUser";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("execution(public * com.cwz.blog.defaultblog.controller..*(..))")
    public void login(){}

    @Around("login() && @annotation(permissionCheck)")
    public Object principalAround(ProceedingJoinPoint point, PermissionCheck permissionCheck) throws Throwable {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginName = auth.getName();

        // 没有登录
        if (StringUtils.equals(loginName, ANONYMOUS_USER)) {
            return JsonResult.fail(CodeType.USER_NOT_LOGIN).toJSON();
        }

        //接口权限拦截
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String value = permissionCheck.value();
        for (GrantedAuthority authority : authorities) {
            if (StringUtils.equals(authority.getAuthority(), value)) {
                return point.proceed();
            }
        }

        logger.error("[{}]用户对方法[{}]权限不足，不能进行访问", loginName, point.getSignature().getName());
        return JsonResult.fail(CodeType.PERMISSION_VERIFY_FAIL).toJSON();
    }


}
