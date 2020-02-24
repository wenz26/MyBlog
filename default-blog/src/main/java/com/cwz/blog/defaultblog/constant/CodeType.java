package com.cwz.blog.defaultblog.constant;

/**
 * @author: 陈文振
 * @date: 2019/12/16
 * @description: 状态返回码
 */
public enum CodeType {
    /**
     * 状态码
     */
    SUCCESS_STATUS(0, "成功"),

    USER_NOT_LOGIN(101, "用户未登录"),
    PERMISSION_VERIFY_FAIL(102, "权限验证失败"),
    SERVER_EXCEPTION(103, "服务器异常"),

    DELETE_ARTICLE_FAIL(201, "删除文章失败"),
    READ_ARTICLE_THUMBS_UP_FAIL(202, "阅读文章点赞信息失败"),
    ARTICLE_HAS_THUMBS_UP(203, "文章已经点过赞了"),
    ARTICLE_NOT_EXIST(204, "文章不存在或文章已被删除"),
    PUBLISH_ARTICLE_NO_PERMISSION(205, "发表文章没有权限"),
    PUBLISH_ARTICLE_EXCEPTION(206, "服务器异常"),
    READ_ARTICLE_THUMBS_UP_SUCCESS(207, "阅读文章点赞信息已读"),
    READ_ARTICLE_THUMBS_UP_ALL_SUCCESS(208, "阅读文章点赞信息全部已读"),
    ARTICLE_FAVORITE_SUCCESS(209, "收藏文章成功"),
    ARTICLE_FAVORITE_FAIL(210, "收藏文章失败"),
    DELETE_ARTICLE_FAVORITE_SUCCESS(211, "取消收藏文章成功"),
    DELETE_ARTICLE_FAVORITE_FAIL(212, "取消收藏文章失败"),
    ARTICLE_IMAGE_BLANK(213, "文章首页图片不能为空"),
    ARTICLE_HAS_FAVORITE(214, "文章已经收藏过了"),

    FIND_TAGS_CLOUD(301, "获得所有标签成功"),
    FIND_ARTICLE_BY_TAG(302, "通过标签获得文章成功"),
    ADD_TAGS_SUCCESS(303, "添加标签成功"),
    DELETE_TAGS_SUCCESS(304, "删除标签成功"),
    TAGS_NOT_EXIST(305, "标签不存在"),
    TAGS_HAS_ARTICLE(306, "标签下存在文章，删除失败"),
    TAGS_HAS_EXIST(307, "标签已存在，添加失败"),
    TAGS_FORMAT_ERROR(308, "标签名长度过长或格式不正确"),

    ADD_CATEGORY_SUCCESS(401, "添加分类成功"),
    UPDATE_CATEGORY_SUCCESS(402, "修改分类成功"),
    DELETE_CATEGORY_SUCCESS(403, "删除分类成功"),
    CATEGORY_NOT_EXIST(404, "分类不存在"),
    CATEGORY_HAS_ARTICLE(405, "分类下存在文章，删除失败"),
    CATEGORY_EXIST(406, "分类已存在"),
    CATEGORY_FORMAT_ERROR(407, "分类名长度过长或格式不正确"),

    USERNAME_TOO_LANG(501, "用户名太长"),
    USERNAME_BLANK(502, "用户名为空"),
    HAS_MODIFY_USERNAME(503, "修改个人信息成功，并且修改了用户名"),
    NOT_MODIFY_USERNAME(504, "修改个人信息成功，但没有修改用户名"),
    USERNAME_EXIST(505, "用户名已存在"),
    MODIFY_HEAD_PORTRAIT_FAIL(506, "修改头像失败"),
    READ_MESSAGE_FAIL(507, "已读信息失败"),
    USERNAME_NOT_EXIST(508, "用户名不存在"),
    USERNAME_FORMAT_ERROR(509, "用户名长度过长或格式不正确"),

    ARTICLE_COLLECTION_SUCCESS(601, "文章收藏成功"),
    ARTICLE_COLLECTION_FAIL(602, "文章收藏失败"),

    USER_LOG_RECORD_SUCCESS(701, "日志记录成功"),
    USER_LOG_RECORD_FAIL(702, "日志记录失败"),

    COMMENT_BLANK(801, "评论内容为空"),
    MESSAGE_HAS_THUMBS_UP(802, "评论已经点过赞了"),

    PHONE_ERROR(901, "手机号错误"),
    AUTH_CODE_ERROR(902, "验证码错误"),
    PHONE_EXIST(903, "手机号存在"),

    COMMENT_READ_SUCCESS(1001, "评论已读成功"),
    COMMENT_READ_ALL_SUCCESS(1002, "评论全部已读成功"),
    READ_COMMENT_THUMBS_UP_SUCCESS(1003, "阅读评论点赞信息已读"),
    READ_COMMENT_THUMBS_UP_ALL_SUCCESS(1004, "阅读评论点赞信息全部已读"),
    READ_COMMENT_THUMBS_UP_FAIL(1005, "阅读评论点赞信息失败"),
    DELETE_COMMENT_FAIL(1006, "删除评论失败"),

    ROLE_FORMAT_ERROR(1101, "角色名长度过长或格式不正确"),
    ADD_ROLE_SUCCESS(1102, "添加角色成功"),
    UPDATE_ROLE_SUCCESS(1103, "修改角色成功"),
    DELETE_ROLE_SUCCESS(1104, "删除角色成功"),
    ROLE_NOT_EXIST(1105, "角色不存在"),
    ROLE_HAS_USER(1106, "角色下存在用户，删除失败"),
    ROLE_EXIST(1107, "角色已存在"),

    CODE_BLANK(1201, "验证码的值为空"),
    CODE_NOT_EXIST(1202, "验证码不存在"),
    CODE_IS_EXPIRED(1203, "验证码已过期"),
    CODE_NOT_RIGHT(1204, "验证码不匹配"),

    READ_FEEDBACK_ONE_SUCCESS(1301, "已读一条反馈信息成功"),
    READ_FEEDBACK_ALL_SUCCESS(1302, "已读全部反馈信息成功"),
    READ_FEEDBACK_FAIL(1303, "已读反馈信息失败"),

    INSERT_USER_ATTENTION_SUCCESS(1401, "关注用户成功"),
    INSERT_USER_ATTENTION_FAIL(1402, "关注用户失败"),
    ;


    private int code;
    private String message;

    CodeType(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
