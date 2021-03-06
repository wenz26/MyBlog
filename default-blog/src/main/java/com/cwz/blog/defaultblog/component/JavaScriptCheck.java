package com.cwz.blog.defaultblog.component;

/**
 * @author: 陈文振
 * @date: 2020/1/4
 * @description: 对前端传过来的内容进行 js检查（防止xss攻击，js恶意脚本攻击）
 */
public class JavaScriptCheck {

    public static String javaScriptCheck(String comment) {

        comment = textCheck(comment, "script");
        comment = textCheck(comment, "iframe");

        return comment;
    }

    private static String textCheck(String comment, String sign) {
        String signFir = "<" + sign;
        String signSec = "</" + sign + ">";
        int begin,end,theEnd;
        String newStr = "";
        begin = comment.indexOf(signFir);
        end = comment.indexOf(signSec);
        if (begin == -1) {
            return comment;
        }
        while (begin != -1) {
            theEnd = comment.indexOf(">");
            newStr += comment.substring(0, begin);
            newStr += "[removed]" + comment.substring(theEnd + 1, end) + "[removed]";

            comment = comment.substring(end + 9);

            begin = comment.indexOf(signFir);
            end = comment.indexOf(signSec);
        }
        return newStr;
    }
}
