package com.cwz.blog.defaultblog.utils;

import com.cwz.blog.defaultblog.entity.Result;

/**
 * @author: 陈文振
 * @date: 2019/12/3
 * @description: 返回统一格式封装工具
 * 1.返回码 2.提示信息 3.具体内容
 */
public class ResultUtil {

    public static Result success(String msg) {
        return success(null, msg);
    }

    public static Result success(Object object, String msg) {
        Result result = new Result();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(object);
        return result;
    }

    public static Result success(Integer code, Object object, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(object);
        return result;
    }

    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static Result error(Integer code, Object object, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(object);
        return result;
    }
}
