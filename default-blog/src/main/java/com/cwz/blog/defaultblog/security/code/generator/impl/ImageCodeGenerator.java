package com.cwz.blog.defaultblog.security.code.generator.impl;

import com.cwz.blog.defaultblog.security.code.generator.CodeGenerator;
import com.cwz.blog.defaultblog.security.code.image.ImageCode;
import com.cwz.blog.defaultblog.security.code.sms.SmsCode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author: 陈文振
 * @date: 2020/1/1
 * @description: 图片验证码生成器实现类
 */
public class ImageCodeGenerator implements CodeGenerator {

    @Override
    public SmsCode createCode() {
        int width = 67;
        int height = 23;

        // 生成23高，67宽的图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        Random random = new Random();

        // 生成条纹
        graphics.setColor(getRandColor(200, 250));
        graphics.fillRect(0, 0, width, height);
        graphics.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        graphics.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            graphics.drawLine(x, y, x + xl, y + yl);
        }

        // 生成4位随机数
        String sRand = "";
        for (int i = 0; i < 4; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
            graphics.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110),
                    20 + random.nextInt(110)));
            graphics.drawString(rand, 13 * i + 6, 16);
        }

        graphics.dispose();

        // 我在这里设置验证码120秒后过期
        return new ImageCode(image, sRand, 120);
    }

    /**
     * @description: 生成随机背景条纹
     * @author: 陈文振
     * @date: 2020/1/1
     * @param fc
     * @param bc
     * @return: java.awt.Color
     */
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();

        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

}
