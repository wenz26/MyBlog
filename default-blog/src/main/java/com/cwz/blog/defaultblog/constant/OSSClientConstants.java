package com.cwz.blog.defaultblog.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: 陈文振
 * @date: 2020/1/1
 * @description: 阿里云OSS的一些配置常量
 */
@Component
public class OSSClientConstants {

    /**
     * 阿里云OSS API的外网域名
     */
    public static final String DOMAIN_NAME = "https://oss-cn-shanghai.aliyuncs.com";

    /**
     * 阿里云API的密钥Access Key ID
     */
    public static String ACCESS_KEY_ID;

    /**
     *阿里云API的密钥Access Key Secret
     */
    public static String ACCESS_KEY_SECRET;

    /**
     * 阿里云OSS的bucket名称
     * 在阿里云上自己创建一个bucket
     */
    public static final String BUCKET_NAME = "cwz-blog";

    /**
     * 阿里云OSS的文件夹名称
     * 在阿里云上自己创建一个文件夹，方便分类管理图片
     */
    public static final String FOLDER="blog/image/";

    /**
     * 阿里云OSS的存放文章图片文件夹名称
     */
    public static final String ARTICLE_IMAGE = "article/";

    /**
     * 阿里云OSS的存放用户图片文件夹名称
     */
    public static final String USER_IMAGE = "user/";

    @Value("${ali-yun.accessKeyId}")
    public void setAccessKeyId(String accessKeyId) {
        ACCESS_KEY_ID = accessKeyId;
    }

    @Value("${ali-yun.accessKeySecret}")
    public void setAccessKeySecret(String accessKeySecret) {
        ACCESS_KEY_SECRET = accessKeySecret;
    }

}
