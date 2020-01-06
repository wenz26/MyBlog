package com.cwz.blog.defaultblog.security.code.image;

import com.cwz.blog.defaultblog.security.code.sms.SmsCode;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

/**
 * @author: 陈文振
 * @date: 2020/1/1
 * @description: 登录时的图片验证码
 */
public class ImageCode extends SmsCode {

    /**
     * 验证码生成的图片
     */
    private BufferedImage image;

    public ImageCode(BufferedImage image, String code, long expireIn) {
        super(code, expireIn);
        this.image = image;
    }

    public ImageCode(BufferedImage image, String code, LocalDateTime expireTime) {
        super(code, expireTime);
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
