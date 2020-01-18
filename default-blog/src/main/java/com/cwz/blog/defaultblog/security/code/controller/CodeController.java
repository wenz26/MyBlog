package com.cwz.blog.defaultblog.security.code.controller;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.redis.StringRedisServiceImpl;
import com.cwz.blog.defaultblog.security.code.generator.CodeGenerator;
import com.cwz.blog.defaultblog.security.code.image.ImageCode;
import com.cwz.blog.defaultblog.security.code.sms.SmsCode;
import com.cwz.blog.defaultblog.security.code.sms.SmsCodeSender;
import com.cwz.blog.defaultblog.utils.JsonResult;
import com.cwz.blog.defaultblog.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author: 陈文振
 * @date: 2020/1/1
 * @description: 验证码控制类
 */
@Api(tags = "验证码控制层")
@RestController
@RequestMapping("/code")
public class CodeController {

    @Autowired
    private CodeGenerator imageCodeGenerator;
    @Autowired
    private SmsCodeSender smsCodeSender;
    @Autowired
    private HashRedisServiceImpl hashRedisService;

    /**
     * @description: 图片验证码
     * @author: 陈文振
     * @date: 2020/1/1
     * @param request
     * @param response
     * @return: java.lang.String
     */
    @ApiOperation(value = "图片验证码")
    @GetMapping("/image")
    public synchronized void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode imageCode = (ImageCode) imageCodeGenerator.createCode();

        // 因为图片 不能存入redis的，所以我这里就换成 短信验证码的形式来存储
        SmsCode code = new SmsCode(imageCode.getCode(), imageCode.getExpireTime());

        HttpSession session = request.getSession();

        String id = session.getId();

        // 将图片验证码存入redis，对于博客的过期时间后面再用
        hashRedisService.put(StringUtil.PREFIX_IMAGE_CODE, id, code);
        session.setAttribute("sessionId", id);

        // 在将生成的图片写到接口的响应中
        // response.getOutputStream() 响应的输出流
        ImageIO.write(imageCode.getImage(), "JPEG", response.getOutputStream());
    }

    /**
     * @description: 短信验证
     * @author: 陈文振
     * @date: 2020/1/1
     * @param request
     * @return: java.lang.String
     */
    @ApiOperation(value = "手机验证码")
    @LogAnnotation(module = "获取手机验证码", operation = "GET获取请求")
    @GetMapping("/sms")
    public String createSmsCode(HttpServletRequest request) {
        String phone = request.getParameter("mobile");
        String sign = request.getParameter("sign");

        HttpSession session = request.getSession();
        session.setAttribute("phone", phone);

        return smsCodeSender.getAuthCode(phone, sign);
    }
}
