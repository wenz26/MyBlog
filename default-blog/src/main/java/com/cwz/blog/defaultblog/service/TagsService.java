package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.entity.Tags;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: 陈文振
 * @date: 2019/12/23
 * @description: 标签业务操作
 */
public interface TagsService {

    /**
     * @description: 获得所有的标签以及该标签的文章总数
     * @author: 陈文振
     * @date: 2019/12/24
     * @return:
     */
    DataMap findTagsInfoAndArticleCountNum(int rows, int pageNum, String tagSearch, String firstDate, String lastDate);

    /**
     * @description: 获得所有的标签名称
     * @author: 陈文振
     * @date: 2019/12/24
     * @return:
     */
    DataMap findTagsName();

    /**
     * @description: 获得标签数目
     * @author: 陈文振
     * @date: 2019/12/24
     * @return:
     */
    int countTagsNum();

    /**
     * @description: 获得所有标签
     * @author: 陈文振
     * @date: 2019/12/24
     * @return:
     */
    DataMap findAllTags();

    /**
     * @description: 更新标签
     * @author: 陈文振
     * @date: 2019/12/24
     * @param tagName: 标签名
     * @param type: 1--增加标签（标签不能修改，只能增加）   2--删除标签
     * @return:
     */
    @Transactional
    DataMap updateTags(String tagName, int type);

    /**
     * @description: 通过标签名查看标签是否存在
     * @author: 陈文振
     * @date: 2019/12/24
     * @param tagName: 标签名
     * @return:
     */
    Tags tagIsExistByTagName(String tagName);


}
