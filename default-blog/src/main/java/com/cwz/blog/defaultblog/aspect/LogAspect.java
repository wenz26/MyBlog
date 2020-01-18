package com.cwz.blog.defaultblog.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.entity.UserLog;
import com.cwz.blog.defaultblog.service.UserLogService;
import com.cwz.blog.defaultblog.utils.HttpContextUtils;
import com.cwz.blog.defaultblog.utils.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2019/12/26
 * @description: 日志切面类
 */
@Aspect
// 开启AspectJ 自动代理模式
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Component
public class LogAspect {

    @Autowired
    private UserLogService userLogService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("@annotation(com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation)")
    public void logPointCut(){}

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        logger.info("日志记录开始：" + beginTime);

        Object result = point.proceed();

        long endTime = System.currentTimeMillis();
        logger.info("日志记录结束：" + endTime);

        //执行时长(毫秒)
        long logTimeConsuming = endTime - beginTime;
        //logger.info("日志记录执行时长（毫秒）：" + logTimeConsuming);

        saveLong(point, logTimeConsuming, result);
        return result;
    }

    /**
     * @description: 保存日志
     * @author: 陈文振
     * @date: 2019/12/27
     * @param
     * @return: void
     */
    private void saveLong(ProceedingJoinPoint joinPoint, long logTimeConsuming, Object result) throws NoSuchMethodException {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();

        // 获取抽象方法 (获取切入点的方法签名(包括方法名和方法参数))
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //logger.info("当前的方法签名为：" + signature);

        Method method = signature.getMethod();

        UserLog userLog = new UserLog();

        // 获取被代理类的对象, getTarget():获取目标对象 （即切面用在的类，如：UserController）
        Class<?> clazz = joinPoint.getTarget().getClass();
        //logger.info("获取被代理类的对象：" + clazz);

        // 获取当前类有LogAnnotation注解的方法,获取切入点方法的LogAnnotation注解类，根据该注解类的内置对象进行下一步操作
        method = clazz.getMethod(method.getName(), method.getParameterTypes());
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
        //logger.info("当前的方法注解为：" + logAnnotation);

        userLog.setLogModule(logAnnotation.module());
        userLog.setLogOperation(logAnnotation.operation());

        // 切入点的方法名
        String className = clazz.getName();
        String methodName = signature.getName();
        userLog.setLogMethod(className + "." + methodName + "()");

        // 切入点的参数
        Object[] args = joinPoint.getArgs();
        Object[] arguments = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse
                || args[i] instanceof MultipartFile || args[i] instanceof AuthenticationException
                    || args[i] instanceof Authentication) {

                /*
                 * ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException:
                 * It is illegal to call this method if the current request is not in asynchronous
                 * mode (i.e. isAsyncStarted() returns false)
                 *
                 * ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException:
                 * getOutputStream() has already been called for this response
                 * */
                continue;
            }
            arguments[i] = args[i];
        }
        String params = JSON.toJSONString(arguments);
        //logger.info("当前方法的参数为：" + params);
        userLog.setLogParams(params);

        // 获取request 设置IP地址
        userLog.setLogIp(IpUtils.getIpAddr(request));

        // 用户名（这里从 Spring Security中获取用户名）
        // 如果从Spring Security中获取用户名为空，则写入 “访客(未登录)”
        /*String username = ((Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName();
        if (username == null) {
            userLog.setLogUsername("访客(未登录)");
        } else {
            userLog.setLogUsername(username);
        }*/
        String username = (String) request.getSession().getAttribute("authentication");
        if (username == null) {
            String cookie = request.getHeader("Cookie");
            if (!StringUtils.isBlank(cookie) && cookie.contains("remember-me")) {
                String name = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
                userLog.setLogUsername(name);
            } else {
                userLog.setLogUsername("匿名用户(未登录)");
            }
        } else {
            userLog.setLogUsername(username);
        }

        userLog.setLogTimeConsuming(logTimeConsuming);

        LocalDateTime localDateTime = LocalDateTime.now();
        userLog.setCreateDate(localDateTime);

        // 这里后面可以从 返回的 JsonResult 来设置 成功或失败
        /*if (result instanceof Exception) {
            userLog.setLogStatus("1");
        } else if (result instanceof JsonResult) {
            userLog.setLogStatus(((JSONObject) JSON.toJSON(result)).get("code").toString());
        } else if (result instanceof JSON) {
            userLog.setLogStatus(((JSONObject) JSON.toJSON(result)).get("code").toString());
        } else {
            userLog.setLogStatus("1");
        }*/
        if (result instanceof String) {
            logger.info("result 的类型为 String：" + result.getClass());
            if (((String) result).contains("\"code\":")) {
                userLog.setLogStatus(((JSONObject) JSON.parse((String) result)).get("code").toString());
            } else if (((String) result).contains("\"status\":")) {
                userLog.setLogStatus(((JSONObject) JSON.parse((String) result)).get("status").toString());
            } else {
                userLog.setLogStatus("0");
            }
        } else if (result instanceof UsernameNotFoundException) {
            userLog.setLogStatus("1");
        } else if (result instanceof Exception) {
            userLog.setLogStatus("1");
        } else {
            if (Objects.isNull(result)){
                String userLogin = (String) request.getSession().getAttribute("userLogin");
                if (Objects.equals(userLogin, "success")) {
                    logger.info("该请求为登录请求，且用户[{}]登录成功~~~", username);
                    userLog.setLogStatus("0");
                } else if (Objects.equals(userLogin, "fail")){
                    logger.info("该请求为登录请求，但用户登录失败！！！");
                    userLog.setLogStatus("1");
                } else {
                    logger.info("result 的类型为 null，请求成功");
                    userLog.setLogStatus("0");
                }

            } else {
                logger.info("result 的类型为[{}]，请求成功", result.getClass());
                userLog.setLogStatus("0");
            }
        }


        userLogService.saveUserLog(userLog);
        logger.info("记录成功");
    }
}
