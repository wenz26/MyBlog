package com.cwz.blog.defaultblog.security.code.sms;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: 陈文振
 * @date: 2020/1/1
 * @description: 手机验证码
 */
public class SmsCode implements Serializable {

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码过期时间
     */
    private LocalDateTime expireTime;

    private boolean expired;


    public SmsCode(){

    }

    /**
     * @description: 多少秒后会过期(精确到秒数)
     * @author: 陈文振
     * @date: 2020/1/1
     * @param code
     * @param expireIn
     * @return:
     */
    public SmsCode(String code, long expireIn) {
        this.code = code;
        // plusSeconds(long seconds)方法返回此时间的副本，并添加指定的秒数
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public SmsCode(String code, LocalDateTime expireTime) {
        this.code = code;
        this.expireTime = expireTime;
    }

    /**
     * @description: 验证码是否已经过期
     * @author: 陈文振
     * @date: 2020/1/1
     * @param
     * @return: boolean
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
