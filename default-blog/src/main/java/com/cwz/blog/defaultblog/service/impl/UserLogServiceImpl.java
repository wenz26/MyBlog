package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.UserLog;
import com.cwz.blog.defaultblog.mapper.UserLogMapper;
import com.cwz.blog.defaultblog.service.UserLogService;
import com.cwz.blog.defaultblog.service.common.CommonReturn;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.StringUtil;
import com.cwz.blog.defaultblog.utils.TimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2019/12/27
 * @description: 用户日志业务操作实现类
 */
@Service("userLogService")
public class UserLogServiceImpl implements UserLogService {

    @Autowired
    private UserLogMapper userLogMapper;

    @Override
    public int saveUserLog(UserLog userLog) {
        return userLogMapper.insert(userLog);
    }

    @Override
    public DataMap getAllUserLogByExample(int rows, int pageNum, UserLog userLogExample, String createDate) {
        PageHelper.startPage(pageNum, rows);

        Example example = new Example(UserLog.class);
        example.orderBy("createDate").desc();
        Example.Criteria criteria = example.createCriteria();
        if (!Objects.isNull(userLogExample.getLogModule()) && !Objects.equals(userLogExample.getLogModule(), StringUtil.BLANK)) {
            criteria.andLike("logModule", "%" + userLogExample.getLogModule() + "%");
        }
        if (!Objects.isNull(userLogExample.getLogOperation()) && !Objects.equals(userLogExample.getLogOperation(), StringUtil.BLANK)) {
            criteria.andLike("logOperation", "%" + userLogExample.getLogOperation() + "%");
        }
        if (!Objects.isNull(userLogExample.getLogUsername()) && !Objects.equals(userLogExample.getLogUsername(), StringUtil.BLANK)) {
            criteria.andLike("logUsername", "%" + userLogExample.getLogUsername() + "%");
        }
        if (!Objects.isNull(createDate) && !Objects.equals(createDate, StringUtil.BLANK)) {
            criteria.andLike("createDate", "%" + createDate + "%");
        }
        if (!Objects.isNull(userLogExample.getLogStatus()) && !Objects.equals(userLogExample.getLogStatus(), StringUtil.BLANK)) {
            criteria.andEqualTo("logStatus",  userLogExample.getLogStatus());
        }

        List<UserLog> userLogs = userLogMapper.selectByExample(example);
        PageInfo<UserLog> pageInfo = new PageInfo<>(userLogs);

        JSONObject logJson;
        JSONArray logJsonArray = new JSONArray();
        JSONObject resultJson = new JSONObject();
        CommonReturn commonReturn = new CommonReturn();

        for (UserLog userLog : userLogs) {
            logJson = getLogJSONObject(userLog);
            logJsonArray.add(logJson);
        }

        resultJson.put("result", logJsonArray);
        resultJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        resultJson.put("msg", "分页获得所有的日志记录信息");
        return DataMap.success().setData(resultJson);
    }

    /**
     * @description: 对 用户日志 返回数据进行封装
     * @author: 陈文振
     * @date: 2019/12/27
     * @param userLog
     * @return: com.alibaba.fastjson.JSONObject
     */
    private JSONObject getLogJSONObject(UserLog userLog){
        JSONObject logJson = new JSONObject();
        logJson.put("id", userLog.getId());
        logJson.put("logModule", userLog.getLogModule());
        logJson.put("logOperation", userLog.getLogOperation());
        logJson.put("logMethod", userLog.getLogMethod());
        logJson.put("logParams", userLog.getLogParams());
        logJson.put("logIp", userLog.getLogIp());
        logJson.put("logStatus", userLog.getLogStatus());
        logJson.put("logTimeConsuming", userLog.getLogTimeConsuming());
        logJson.put("createDate", TimeUtil.getFormatDateForSix(userLog.getCreateDate()));
        logJson.put("logUsername", userLog.getLogUsername());

        return logJson;
    }

}
