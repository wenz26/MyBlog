package com.cwz.blog.defaultblog.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.cwz.blog.defaultblog.constant.OSSClientConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Date;

/**
 * @author: 陈文振
 * @date: 2020/1/5
 * @description: 阿里云OSS连接
 */
public class AliYunOSSClientUtil {

    /**
     * 阿里云OSS API的外网域名
     */
    private static String DOMAIN_NAME;

    /**
     * 阿里云API的密钥Access Key ID
     */
    private static String ACCESS_KEY_ID;

    /**
     *阿里云API的密钥Access Key Secret
     */
    private static String ACCESS_KEY_SECRET;

    /**
     * 阿里云OSS的bucket名称
     * 在阿里云上自己创建一个bucket
     */
    private static String BUCKET_NAME;

    /**
     * 阿里云OSS的文件夹名称
     * 在阿里云上自己创建一个文件夹，方便分类管理图片
     */
    private static String FOLDER;

    /**
     * 阿里云OSS的存放文章图片文件夹名称
     */
    private static String ARTICLE_IMAGE;

    /**
     * 阿里云OSS的存放用户图片文件夹名称
     */
    private static String USER_IMAGE;

    private static Logger logger = LoggerFactory.getLogger(AliYunOSSClientUtil.class);

    /**
     * 初始化属性
     */
    static {
        DOMAIN_NAME = OSSClientConstants.DOMAIN_NAME;
        ACCESS_KEY_ID = OSSClientConstants.ACCESS_KEY_ID;
        ACCESS_KEY_SECRET = OSSClientConstants.ACCESS_KEY_SECRET;
        BUCKET_NAME = OSSClientConstants.BUCKET_NAME;
        FOLDER = OSSClientConstants.FOLDER;
        ARTICLE_IMAGE = OSSClientConstants.ARTICLE_IMAGE;
        USER_IMAGE = OSSClientConstants.USER_IMAGE;
    }

    /**
     * @description: 获得阿里云OSS客户端对象
     * @author: 陈文振
     * @date: 2020/1/5
     * @return:
     */
    public static OSSClient getOSSClient() {
        return (OSSClient) new OSSClientBuilder().build(DOMAIN_NAME, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    }

    /**
     * @description: 创建存储空间
     * @author: 陈文振
     * @date: 2020/1/5
     * @param ossClient
     * @param bucketName
     * @return: java.lang.String
     */
    public static String createBucketName(OSSClient ossClient, String bucketName){
        // 存储空间
        final String bucketNames = bucketName;

        if (!ossClient.doesBucketExist(bucketName)) {
            // 创建存储空间
            Bucket bucket = ossClient.createBucket(bucketName);
            logger.info("OSS创建存储空间成功");
            return bucket.getName();
        }
        return bucketNames;
    }

    /**
     * @description: 删除存储空间
     * @author: 陈文振
     * @date: 2020/1/5
     * @param ossClient
     * @param bucketName
     * @return: void
     */
    public static void deleteBucket(OSSClient ossClient, String bucketName){
        ossClient.deleteBucket(bucketName);
        logger.info("OSS删除" + bucketName + "Bucket成功");
    }

    /**
     * @description: 创建模拟文件夹
     * @author: 陈文振
     * @date: 2020/1/5
     * @param ossClient
     * @param bucketName
     * @param folder: 模拟文件夹名如"image/"
     * @return: java.lang.String
     */
    public static String createFolder(OSSClient ossClient, String bucketName, String folder){
        // 文件夹名
        final String newFolder = folder;
        // 判断文件夹是否存在，不存在则创建
        if (!ossClient.doesObjectExist(bucketName, newFolder)) {
            // 创建文件夹
            ossClient.putObject(bucketName, newFolder, new ByteArrayInputStream(new byte[0]));
            logger.info("OSS创建文件夹成功");
            // 得到文件夹名
            OSSObject object = ossClient.getObject(bucketName, newFolder);
            String fileDir = object.getKey();
            return fileDir;
        }
        return newFolder;
    }

    /**
     * @description: 根据key删除OSS服务器上的文件
     * @author: 陈文振
     * @date: 2020/1/5
     * @param ossClient
     * @param bucketName
     * @param folder: Bucket下的路径名 如："image/user/"
     * @param key: Bucket下的文件名 如："cake.jpg"
     * @return: void
     */
    public static void deleteFile(OSSClient ossClient, String bucketName, String folder, String key){
        ossClient.deleteObject(bucketName, folder + key);
        logger.info("OSS删除" + bucketName + "下的文件" + folder + key + "成功！");
    }

    /**
     * @description: 上传图片至OSS
     * @author: 陈文振
     * @date: 2020/1/5
     * @param ossClient
     * @param file
     * @param bucketName
     * @param folder: Bucket下的路径名 如："image/user/"
     * @return: java.lang.String
     */
    public static String uploadObjectToOSS(OSSClient ossClient, File file, String bucketName, String folder) {
        String resultStr = null;

        try {
            // 以输入流的形式上传文件
            FileInputStream is = new FileInputStream(file);
            // 文件名
            String fileName = file.getName();
            // 文件大小
            long fileSize = file.length();

            // 创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            // 上传的文件的长度
            metadata.setContentLength(is.available());
            // 指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            // 指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            // 指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            // 文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。
            // 如果用户没有指定则根据Key或文件名的扩展名生成，如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(getContentType(fileName));
            // 指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");

            //上传文件   (上传文件流的形式)
            PutObjectResult putResult = ossClient.putObject(bucketName, folder + fileName, is, metadata);
            //解析结果
            resultStr = putResult.getETag();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("上传阿里云OSS服务器异常。" + e.getMessage(), e);
        }
        return resultStr;
    }

    /**
     * @description: 通过文件名判断并获取OSS服务文件上传时文件的contentType
     * @author: 陈文振
     * @date: 2020/1/5
     * @param fileName
     * @return: java.lang.String
     */
    public static String getContentType(String fileName){
        // 文件的后缀名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if(".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if(".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if(".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension)  || ".png".equalsIgnoreCase(fileExtension) ) {
            return "image/jpeg";
        }
        if(".html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if(".txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if(".vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if(".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if(".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if(".xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        //默认返回类型
        return "image/jpeg";
    }

    /**
     * @description: 获得url链接
     * @author: 陈文振
     * @date: 2020/1/5
     * @param ossClient
     * @param key
     * @return: java.lang.String
     */
    public static String getUrl(OSSClient ossClient, String key) {
        // 设置URL过期时间为10年  3600l* 1000*24*365*10
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(OSSClientConstants.BUCKET_NAME, key, expiration);
        if (url != null) {
            return url.toString();
        }
        return null;
    }

    /**
     * @description: 列举指定存储空间下的文件
     * @author: 陈文振
     * @date: 2020/1/5
     * @param ossClient
     * @param bucketName
     * @return: com.alibaba.fastjson.JSONObject
     */
    public static JSONArray getFileUrl(OSSClient ossClient, String bucketName){
        ObjectListing objectListing = ossClient.listObjects(bucketName);
        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();
        for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            jsonObject = new JSONObject();
            jsonObject.put("key", objectSummary.getKey());
            jsonObject.put("size", objectSummary.getSize());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

}
