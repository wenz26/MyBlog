package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.entity.FeedBack;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: 陈文振
 * @date: 2019/12/22
 * @description: 反馈业务操作
 */
public interface FeedBackService {

    /**
     * @description: 保存反馈信息
     * @author: 陈文振
     * @date: 2019/12/22
     * @param feedBack: 反馈类
     * @return:
     */
    @Transactional
    void submitFeedback(FeedBack feedBack);

    /**
     * @description: 获得所有的反馈
     * @author: 陈文振
     * @date: 2019/12/22
     * @return:
     */
    DataMap getAllFeedback(int rows, int pageNum, Integer isRead, String firstDate, String lastDate);

    /**
     * @description: 已读一条反馈信息
     * @author: 陈文振
     * @date: 2020/2/1
     * @param id
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap readOneFeedBackRecord(int id);

    /**
     * @description: 已读全部反馈信息
     * @author: 陈文振
     * @date: 2020/2/1
     * @param
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap readAllFeedBack();

}
