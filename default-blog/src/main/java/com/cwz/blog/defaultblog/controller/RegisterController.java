package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.aspect.PrincipalAspect;
import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.User;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.security.code.sms.SmsCode;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.JsonResult;
import com.cwz.blog.defaultblog.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2020/1/5
 * @description: 注册控制器
 */
@Api(tags = "注册控制器")
@RestController
public class RegisterController {

    @Autowired
    private UserService userService;
    @Autowired
    private HashRedisServiceImpl hashRedisService;

    /**
     * @description: 注册用户
     * @author: 陈文振
     * @date: 2020/1/5
     * @param user
     * @param request
     * @return: java.lang.String
     */
    @ApiOperation(value = "注册用户")
    @LogAnnotation(module = "注册用户", operation = "注册新增")
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String register(User user,
                           HttpServletRequest request){

        String authCode = request.getParameter("authCode");

        SmsCode code = (SmsCode) hashRedisService.get(StringUtil.PREFIX_SMS_CODE, user.getPhone());

        // 判断获得的手机号是否是发送验证码的手机号
        if (Objects.isNull(code)) {
            return JsonResult.fail(CodeType.PHONE_ERROR).toJSON();
        }

        // 判断验证码是否正确
        if (!StringUtils.equals(authCode, code.getCode())) {
            return JsonResult.fail(CodeType.AUTH_CODE_ERROR).toJSON();
        }

        // 判断验证码是否过期
        if (code.isExpired()) {
            return JsonResult.fail(CodeType.CODE_IS_EXPIRED).toJSON();
        }

        // 判断手机号是否存在
        if (userService.findUserByPhone(user.getPhone()) != null) {
            return JsonResult.fail(CodeType.PHONE_EXIST).toJSON();
        }

        // 判断用户名是否存在
        if (userService.usernameIsExist(user.getUsername()) ||
                StringUtils.equals(user.getUsername(), PrincipalAspect.ANONYMOUS_USER)) {
            return JsonResult.fail(CodeType.USERNAME_EXIST).toJSON();
        }

        // 注册结果
        DataMap dataMap = userService.insert(user);
        if (dataMap.getCode() == 0) {
            // 注册成功删除redis中的验证码
            hashRedisService.hashDelete(StringUtil.PREFIX_SMS_CODE, user.getPhone());
        }

        return JsonResult.build(dataMap).toJSON();

    }
}
