package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.FeedBack;
import com.cwz.blog.defaultblog.mapper.FeedBackMapper;
import com.cwz.blog.defaultblog.service.FeedBackService;
import com.cwz.blog.defaultblog.service.UserService;
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

/**
 * @author: 陈文振
 * @date: 2019/12/22
 * @description: 反馈业务操作实现类
 */
@Service("feedBackService")
public class FeedBackServiceImpl implements FeedBackService {

    @Autowired
    private FeedBackMapper feedBackMapper;

    @Autowired
    private UserService userService;

    @Override
    public void submitFeedback(FeedBack feedBack) {
        LocalDateTime localDateTime = LocalDateTime.now();
        feedBack.setFeedbackDate(localDateTime);
        feedBackMapper.insert(feedBack);
    }

    @Override
    public DataMap getAllFeedback(int rows, int pageNum) {
        CommonReturn commonReturn = new CommonReturn();

        PageHelper.startPage(pageNum, rows);

        Example example = new Example(FeedBack.class);
        example.orderBy("id").desc();
        List<FeedBack> feedBacks = feedBackMapper.selectByExample(example);
        PageInfo<FeedBack> pageInfo = new PageInfo<>(feedBacks);

        JSONObject returnJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject feedbackJson;

        for (FeedBack feedBack : feedBacks) {
            feedbackJson = new JSONObject();
            feedbackJson.put("feedbackContent", feedBack.getFeedbackContent());
            feedbackJson.put("person", userService.findUsernameById(feedBack.getUserId()));
            feedbackJson.put("feedbackDate", TimeUtil.getFormatDateForSix(feedBack.getFeedbackDate()));

            if (feedBack.getContactInfo() == null) {
                feedbackJson.put("contactInfo", StringUtil.BLANK);
            } else {
                feedbackJson.put("contactInfo", feedBack.getContactInfo());
            }
            jsonArray.add(feedbackJson);
        }
        returnJson.put("msg", "获取反馈信息");
        returnJson.put("result", jsonArray);
        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        return DataMap.success().setData(returnJson);
    }
}
