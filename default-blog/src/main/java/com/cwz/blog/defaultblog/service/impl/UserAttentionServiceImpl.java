package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.User;
import com.cwz.blog.defaultblog.entity.UserAttention;
import com.cwz.blog.defaultblog.mapper.ArticleMapper;
import com.cwz.blog.defaultblog.mapper.UserAttentionMapper;
import com.cwz.blog.defaultblog.service.UserAttentionService;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.service.common.CommonReturn;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.TimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: 陈文振
 * @date: 2020/2/5
 * @description: 用户关注业务操作实现类
 */
@Service("userAttentionService")
public class UserAttentionServiceImpl implements UserAttentionService {

    @Autowired
    private UserAttentionMapper userAttentionMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserService userService;

    @Override
    public boolean isAttention(int personId, int userId) {
        UserAttention attention = userAttentionMapper.isAttention(personId, userId);
        return attention != null;
    }

    @Override
    public DataMap insertUserAttention(String username, int attentionId) {
        LocalDateTime localDateTime = LocalDateTime.now();
        int userId = userService.findIdByUsername(username);

        if (isAttention(attentionId, userId)) {
            return DataMap.fail(CodeType.INSERT_USER_ATTENTION_FAIL);
        }

        int i = userAttentionMapper.insertUserAttention(userId, attentionId, localDateTime);

        if (i > 0) {
            return DataMap.success(CodeType.INSERT_USER_ATTENTION_SUCCESS);
        } else {
            return DataMap.fail(CodeType.INSERT_USER_ATTENTION_FAIL);
        }
    }

    @Override
    public DataMap deleteUserAttention(int attentionId) {
        userAttentionMapper.deleteUserAttention(attentionId);
        return DataMap.success();
    }

    @Override
    public DataMap getUserUserAttention(String username, String inquireName, int type, int rows, int pageNum) {
        int userId = userService.findIdByUsername(username);

        PageHelper.startPage(pageNum, rows);
        List<UserAttention> userAttentions = null;
        int count = 0;

        // 1 为获得我的关注， 2 为获得我的粉丝
        if (type == 1) {
            userAttentions = userAttentionMapper.getUserAttentionByUserId(userId, inquireName);
            count = userAttentionMapper.countUserAttentionByUserId(userId, inquireName);
        } else if (type == 2) {
            userAttentions = userAttentionMapper.getUserAttentionByAttentionId(userId, inquireName);
            count = userAttentionMapper.countUserAttentionByAttentionId(userId, inquireName);
        }

        PageInfo<UserAttention> pageInfo = new PageInfo<>(userAttentions);

        CommonReturn commonReturn = new CommonReturn();
        JSONObject resultJson = new JSONObject();
        JSONArray resultArray = new JSONArray();
        JSONObject jsonObject;

        for (UserAttention userAttention : userAttentions) {
            jsonObject = new JSONObject();
            jsonObject.put("userAttentionId", userAttention.getId());
            jsonObject.put("myUsername", username);
            jsonObject.put("attentionDate", TimeUtil.getFormatDateForSix(userAttention.getAttentionDate()));
            jsonObject.put("attentionUserId", userAttention.getUser().getId());
            jsonObject.put("attentionUsername", userAttention.getUser().getUsername());
            jsonObject.put("attentionAvatarImgUrl", userAttention.getUser().getAvatarImgUrl());
            jsonObject.put("attentionPersonalBrief", userAttention.getUser().getPersonalBrief());

            jsonObject.put("countAttention", userAttentionMapper.countAttention(userAttention.getUser().getId()));
            jsonObject.put("countFan", userAttentionMapper.countFan(userAttention.getUser().getId()));
            jsonObject.put("articleNum", articleMapper.countArticleByUserId(userAttention.getUser().getId()));

            if (type == 2) {
                jsonObject.put("isAttention", isAttention(userAttention.getUser().getId(), userId));
            }

            resultArray.add(jsonObject);
        }

        resultJson.put("result", resultArray);
        resultJson.put("count", count);
        resultJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));

        return DataMap.success().setData(resultJson);
    }

    @Override
    public DataMap getSomeOneAttention(String username, String myName, int type, int rows, int pageNum) {
        User user = userService.findUserByUsername(username);
        int userId = user.getId();

        PageHelper.startPage(pageNum, rows);
        List<UserAttention> userAttentions = null;

        // 1 为获得我的关注， 2 为获得我的粉丝
        if (type == 1) {
            userAttentions = userAttentionMapper.getUserAttentionByUserId(userId, null);
        } else if (type == 2) {
            userAttentions = userAttentionMapper.getUserAttentionByAttentionId(userId, null);
        }

        PageInfo<UserAttention> pageInfo = new PageInfo<>(userAttentions);

        CommonReturn commonReturn = new CommonReturn();
        JSONObject resultJson = new JSONObject();
        JSONArray resultArray = new JSONArray();
        JSONObject jsonObject;

        int viewUserId;
        if (StringUtils.isBlank(myName)) {
            viewUserId = 0;
        } else {
            viewUserId = userService.findIdByUsername(myName);
        }

        for (UserAttention userAttention : userAttentions) {
            jsonObject = new JSONObject();
            jsonObject.put("userAttentionId", userAttention.getId());

            jsonObject.put("countAttention", userAttentionMapper.countAttention(userAttention.getUser().getId()));
            jsonObject.put("countFan", userAttentionMapper.countFan(userAttention.getUser().getId()));
            jsonObject.put("articleNum", articleMapper.countArticleByUserId(userAttention.getUser().getId()));

            jsonObject.put("attentionUserId", userAttention.getUser().getId());
            jsonObject.put("attentionUsername", userAttention.getUser().getUsername());
            jsonObject.put("attentionAvatarImgUrl", userAttention.getUser().getAvatarImgUrl());
            jsonObject.put("attentionPersonalBrief", userAttention.getUser().getPersonalBrief());

            if (viewUserId == 0) {
                jsonObject.put("isAttention", false);

            } else {

                if (viewUserId == userId) {

                    if (type == 1) {
                        jsonObject.put("isAttention", "myself");
                    } else if (type == 2) {
                        jsonObject.put("isAttention", isAttention(userAttention.getUser().getId(), viewUserId));
                    }

                } else {
                    jsonObject.put("isAttention", isAttention(userAttention.getUser().getId(), viewUserId));
                }

            }

            resultArray.add(jsonObject);
        }

        JSONObject userJson = new JSONObject();
        userJson.put("userId", userId);
        userJson.put("username", user.getUsername());
        userJson.put("avatarImgUrl", user.getAvatarImgUrl());
        userJson.put("myUserId", viewUserId);

        userJson.put("countAttention", userAttentionMapper.countAttention(userId));
        userJson.put("countFan", userAttentionMapper.countFan(userId));
        userJson.put("articleNum", articleMapper.countArticleByUserId(userId));

        if (viewUserId == 0) {
            userJson.put("isAttention", false);
        } else {
            userJson.put("isAttention", isAttention(userId, viewUserId));
        }

        resultJson.put("result", resultArray);
        resultJson.put("user", userJson);
        resultJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));

        return DataMap.success().setData(resultJson);
    }


}
