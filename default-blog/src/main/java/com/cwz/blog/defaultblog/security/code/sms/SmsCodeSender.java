package com.cwz.blog.defaultblog.security.code.sms;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.redis.StringRedisServiceImpl;
import com.cwz.blog.defaultblog.security.code.generator.CodeGenerator;
import com.cwz.blog.defaultblog.security.code.generator.impl.SmsCodeGenerator;
import com.cwz.blog.defaultblog.utils.HttpContextUtils;
import com.cwz.blog.defaultblog.utils.JsonResult;
import com.cwz.blog.defaultblog.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2020/1/1
 * @description: 短信验证码的发送
 */
public class SmsCodeSender {

    @Autowired
    private HashRedisServiceImpl hashRedisService;
    @Autowired
    private CodeGenerator smsCodeGenerator;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 阿里云 accessKeyId
     */
    @Value("${ali-yun.accessKeyId}")
    private String accessKeyId;

    /**
     * 阿里云 accessKeySecret
     */
    @Value("${ali-yun.accessKeySecret}")
    private String accessKeySecret;

    /**
     * 阿里云短信发送模板
     */
    private static final String SIGN_NAME = "城院校园轻博客";

    public synchronized String getAuthCode(String phone, String sign) {

        SmsCode smsCode = smsCodeGenerator.createCode();

        if (!Objects.isNull(phone) && !Objects.equals(phone, StringUtil.BLANK)) {
            // 将短信验证码存入redis，对于博客的过期时间后面再用
            hashRedisService.put(StringUtil.PREFIX_SMS_CODE, phone, smsCode);
        }


        String templateCode = null;
        if (Objects.equals(sign, "login")) {
            templateCode = "SMS_181860648";
        } else if (Objects.equals(sign, "register")) {
            templateCode = "SMS_178766691";
        } else if (Objects.equals(sign, "changePassword")) {
            templateCode = "SMS_181855663";
        } else if (Objects.equals(sign, "forgetPassword")) {
            templateCode = "SMS_181861402";
        }

        return sendSmsResponse(phone, smsCode.getCode(), templateCode);
    }

    private String sendSmsResponse(String phoneNumber, String code, String templateCode) {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");


        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        // 填写接收方的手机号码
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        // 此处填写已申请的短信签名
        request.putQueryParameter("SignName", SIGN_NAME);
        // 此处填写获得的短信模版CODE
        request.putQueryParameter("TemplateCode", templateCode);
        // 填写验证码
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");

        try {
            CommonResponse response = client.getCommonResponse(request);
            //logger.info("发送手机验证码时返回的结果为：" + JSON.toJSONString(response));
        } catch (ServerException e) {
            logger.error("[{}] 发送手机验证码时出现错误：", phoneNumber, e);
            e.printStackTrace();
            return JsonResult.fail().toJSON();
        } catch (ClientException e) {
            logger.error("[{}] 发送手机验证码时出现错误：", phoneNumber, e);
            e.printStackTrace();
            return JsonResult.fail().toJSON();
        }
        return JsonResult.success().toJSON();
    }

}
