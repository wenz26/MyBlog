package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.FeedBack;
import com.cwz.blog.defaultblog.mapper.FeedBackMapper;
import com.cwz.blog.defaultblog.redis.StringRedisServiceImpl;
import com.cwz.blog.defaultblog.service.FeedBackService;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.service.common.CommonReturn;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.StringUtil;
import com.cwz.blog.defaultblog.utils.TimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    private StringRedisServiceImpl stringRedisService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void submitFeedback(FeedBack feedBack) {
        LocalDateTime localDateTime = LocalDateTime.now();
        feedBack.setFeedbackDate(localDateTime);
        feedBackMapper.insert(feedBack);
    }

    @Override
    public DataMap getAllFeedback(int rows, int pageNum, Integer isRead, String firstDate, String lastDate) {
        CommonReturn commonReturn = new CommonReturn();

        PageHelper.startPage(pageNum, rows);

        System.out.println(isRead);
        List<FeedBack> feedBacks = feedBackMapper.selectAllFeedBackToXML(isRead, firstDate, lastDate);
        PageInfo<FeedBack> pageInfo = new PageInfo<>(feedBacks);

        JSONObject returnJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject feedbackJson;

        for (FeedBack feedBack : feedBacks) {
            feedbackJson = new JSONObject();
            feedbackJson.put("id", feedBack.getId());
            feedbackJson.put("feedbackContent", feedBack.getFeedbackContent());
            feedbackJson.put("person", userService.findUsernameById(feedBack.getUserId()));
            feedbackJson.put("feedbackDate", TimeUtil.getFormatDateForSix(feedBack.getFeedbackDate()));

            if (feedBack.getContactInfo() == null) {
                feedbackJson.put("contactInfo", StringUtil.BLANK);
            } else {
                feedbackJson.put("contactInfo", feedBack.getContactInfo());
            }

            feedbackJson.put("isRead", feedBack.getIsRead());
            jsonArray.add(feedbackJson);
        }
        returnJson.put("msg", "获取反馈信息");
        returnJson.put("result", jsonArray);
        returnJson.put("total", feedBackMapper.countAllFeedBack());
        returnJson.put("feedbackNotRead", feedBackMapper.countAllFeedBackNotRead());
        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        return DataMap.success().setData(returnJson);
    }

    @Override
    public DataMap readOneFeedBackRecord(int id) {
        try {
            int i = feedBackMapper.readOneFeedBackRecord(id);

            if (i > 0) {
                stringRedisService.stringIncrement(StringUtil.FEEDBACK_MSG, -1);

                int feedBackMsg = (int) stringRedisService.get(StringUtil.FEEDBACK_MSG);
                if (feedBackMsg == 0) {
                    stringRedisService.remove(StringUtil.FEEDBACK_MSG);
                    return DataMap.success(CodeType.READ_FEEDBACK_ALL_SUCCESS);
                }

                return DataMap.success(CodeType.READ_FEEDBACK_ONE_SUCCESS);
            }
            return DataMap.fail(CodeType.READ_FEEDBACK_FAIL);
        } catch (Exception e) {
            logger.error("阅读反馈信息失败：" + e);
            return DataMap.fail(CodeType.READ_FEEDBACK_FAIL);
        }
    }

    @Override
    public DataMap readAllFeedBack() {
        try {
            int i = feedBackMapper.readAllFeedBack();

            if (i > 0) {
                stringRedisService.remove(StringUtil.FEEDBACK_MSG);
                return DataMap.success(CodeType.READ_FEEDBACK_ALL_SUCCESS);
            }
            return DataMap.success(CodeType.READ_FEEDBACK_FAIL);

        } catch (Exception e) {
            logger.error("阅读反馈信息失败：" + e);
            return DataMap.fail(CodeType.READ_FEEDBACK_FAIL);
        }
    }
}
