package com.cwz.blog.defaultblog.security.code.filter;

import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.security.code.exception.CodeException;
import com.cwz.blog.defaultblog.security.code.sms.SmsCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2020/1/3
 * @description: 验证码过滤器的共同配置
 */
public class CommonCodeFilterConfig {

    private static Logger logger = LoggerFactory.getLogger(CommonCodeFilterConfig.class);

    /**
     * 需要过滤的URL
     * 这里直接过滤登录的url，因为其它资源有SpringSecurity管着不用怕
     */
    public static final String RELEASE_URL = "/loginFrom";

    /**
     * 手机登录时需要过滤的URL
     * 这里直接过滤登录的url，因为其它资源有SpringSecurity管着不用怕
     */
    public static final String PHONE_RELEASE_URL = "/sms";

    /**
     * @description:
     * @author: 陈文振
     * @date: 2020/1/3
     * @param response
     * @param code
     * @param codeValue
     * @return: void
     */
    public static void validate(HttpServletResponse response, SmsCode code, String codeValue) {

        logger.info("前端传过来的验证码为：" + codeValue);


        if (StringUtils.isBlank(codeValue)) {
            response.setStatus(CodeType.CODE_BLANK.getCode());
            throw new CodeException("验证码的值不能为空");
        }
        if (Objects.isNull(code)) {
            response.setStatus(CodeType.CODE_NOT_EXIST.getCode());
            throw new CodeException("验证码不存在");
        }
        if (code.isExpired()) {
            response.setStatus(CodeType.CODE_IS_EXPIRED.getCode());
            throw new CodeException("验证码已过期");
        }
        if (!StringUtils.equals(code.getCode(), codeValue)) {
            response.setStatus(CodeType.CODE_NOT_RIGHT.getCode());
            throw new CodeException("验证码不匹配");
        }

    }
}
