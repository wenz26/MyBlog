package com.cwz.blog.defaultblog.utils;

import com.aliyun.oss.OSSClient;
import com.cwz.blog.defaultblog.constant.OSSClientConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Base64;

/**
 * @author: 陈文振
 * @date: 2020/1/5
 * @description: 文件上传工具
 */
public class FileUtil {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @description: 上传文件到阿里云OSS
     * @author: 陈文振
     * @date: 2020/1/5
     * @param file
     * @param subCatalog
     * @return: java.lang.String
     */
    public String uploadFile(File file, String subCatalog){

        // 初始化OSSClient
        OSSClient ossClient = AliYunOSSClientUtil.getOSSClient();

        String fileKey = AliYunOSSClientUtil.uploadObjectToOSS(ossClient, file, OSSClientConstants.BUCKET_NAME,
                OSSClientConstants.FOLDER + subCatalog);

        String url = AliYunOSSClientUtil.getUrl(ossClient, fileKey);
        logger.info("上传到OSS生成的url为：" + url);

        String picUrl = "https://oss.czodly.top/" + OSSClientConstants.FOLDER + subCatalog +
                file.getName() + "?x-oss-process=style/default";
        logger.info("上传到OSS自己组装的url为：" + picUrl);

        //删除临时生成的文件
        File deleteFile = new File(file.toURI());
        deleteFile.delete();

        ossClient.shutdown();
        return picUrl;
    }

    /**
     * @description: base64字符转换成file
     * @author: 陈文振
     * @date: 2020/1/5
     * @param destPath: 保存的文件路径
     * @param base64: 图片字符串
     * @param fileName: 保存的文件名
     * @return: java.io.File
     */
    public File base64ToFile(String destPath, String base64, String fileName) {
        File file = null;

        // 创建文件目录
        String filePath = destPath;
        File dir = new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }

        BufferedOutputStream bos = null;
        FileOutputStream fos = null;

        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            file = new File(filePath + "/" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * @description: 将file转换成base64字符串
     * @author: 陈文振
     * @date: 2020/1/5
     * @param path
     * @return: java.lang.String
     */
    public String fileToBase64(String path) {
        String base64 = null;
        InputStream in = null;
        try {
            File file = new File(path);
            in = new FileInputStream(file);
            byte[] bytes=new byte[(int)file.length()];
            in.read(bytes);
            base64 = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }

    /**
     * @description: MultipartFile类型文件转File
     * @author: 陈文振
     * @date: 2020/1/5
     * @param multipartFile
     * @param filePath
     * @param fileName
     * @return: java.io.File
     */
    public File multipartFileToFile(MultipartFile multipartFile, String filePath, String fileName){
        File f = null;
        File dir = new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        if(StringUtil.BLANK.equals(multipartFile) || multipartFile.getSize() <= 0){
            multipartFile = null;
        } else {
            try {
                InputStream ins = multipartFile.getInputStream();
                f = new File(filePath + "/" + fileName);
                OutputStream os = new FileOutputStream(f);
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = ins.read(buffer, 0, 8192)) != -1){
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }

}
