package com.cwz.blog.defaultblog.exception;

/**
 * @author: 陈文振
 * @date: 2019/12/3
 * @description: 自定义错误抛出
 */
public class MyException extends Exception {

    public MyException(String message){
        super(message);
    }
}
