package com.cwz.blog.defaultblog.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author: 陈文振
 * @date: 2019/12/15
 * @description: 用户操作日志记录表
 */
@Table(name = "user_log")
public class UserLog {

    /**
     * 日志id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户名
     */
    private String logUsername;

    /**
     * 系统模块
     */
    private String logModule;

    /**
     * 操作类型（新增、查找、修改、删除）
     */
    private String logOperation;

    /**
     * 请求的方法
     */
    private String logMethod;

    /**
     * 请求参数
     */
    private String logParams;

    /**
     * 用户ip
     */
    private String logIp;

    /**
     * 操作状态（成功、失败）
     */
    private String logStatus;

    /**
     * 日志记录执行时长（毫秒）
     */
    private Long logTimeConsuming;

    /**
     * 记录时间
     */
    private LocalDateTime createDate;

    public Integer getId() {
        return id;
    }

    public String getLogUsername() {
        return logUsername;
    }

    public void setLogUsername(String logUsername) {
        this.logUsername = logUsername;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogModule() {
        return logModule;
    }

    public void setLogModule(String logModule) {
        this.logModule = logModule;
    }

    public String getLogOperation() {
        return logOperation;
    }

    public void setLogOperation(String logOperation) {
        this.logOperation = logOperation;
    }

    public String getLogMethod() {
        return logMethod;
    }

    public void setLogMethod(String logMethod) {
        this.logMethod = logMethod;
    }

    public String getLogParams() {
        return logParams;
    }

    public void setLogParams(String logParams) {
        this.logParams = logParams;
    }

    public String getLogIp() {
        return logIp;
    }

    public Long getLogTimeConsuming() {
        return logTimeConsuming;
    }

    public void setLogTimeConsuming(Long logTimeConsuming) {
        this.logTimeConsuming = logTimeConsuming;
    }

    public void setLogIp(String logIp) {
        this.logIp = logIp;
    }

    public String getLogStatus() {
        return logStatus;
    }

    public void setLogStatus(String logStatus) {
        this.logStatus = logStatus;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
