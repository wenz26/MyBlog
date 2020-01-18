package com.cwz.blog.defaultblog.controller;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2020/1/5
 * @description: 登录控制器
 */
@Api(tags = "登录控制器")
@RestController
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private HashRedisServiceImpl hashRedisService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * @description: 修改密码
     * @author: 陈文振
     * @date: 2020/1/5
     * @param phone
     * @return: java.lang.String
     */
    @ApiOperation(value = "修改密码")
    @LogAnnotation(module = "修改密码", operation = "修改")
    @PostMapping(value = "/changePassword", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String changePassword(@RequestParam("phone") String phone, HttpServletRequest request){
        String authCode = request.getParameter("authCode");
        String newPassword = request.getParameter("newPassword");

        SmsCode code = (SmsCode) hashRedisService.get(StringUtil.PREFIX_SMS_CODE, phone);

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

        User user = userService.findUserByPhone(phone);
        if (user == null) {
            return JsonResult.fail(CodeType.USERNAME_NOT_EXIST).toJSON();
        }

        userService.updatePasswordByPhone(phone, passwordEncoder.encode(newPassword));

        // 修改密码成功删除redis中的验证码
        hashRedisService.hashDelete(StringUtil.PREFIX_SMS_CODE, phone);

        return JsonResult.success().toJSON();
    }

    /**
     * @description: 判断验证码
     * @author: 陈文振
     * @date: 2020/1/12
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "判断验证码")
    @LogAnnotation(module = "判断验证码", operation = "验证")
    @GetMapping(value = "/checkCode", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String checkCode(@RequestParam("imageCode") String imageCode,
                            HttpServletRequest request) {
        String sessionId = (String) request.getSession().getAttribute("sessionId");

        SmsCode code = (SmsCode) hashRedisService.get(StringUtil.PREFIX_IMAGE_CODE, sessionId);

        if (StringUtils.equals(imageCode, code.getCode())) {
            return JsonResult.success().toJSON();
        } else {
            return JsonResult.build(DataMap.fail().setData(CodeType.AUTH_CODE_ERROR)).toJSON();
        }
    }
}
