package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.Tags;
import com.cwz.blog.defaultblog.mapper.ArticleMapper;
import com.cwz.blog.defaultblog.mapper.TagsMapper;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.service.TagsService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2019/12/23
 * @description: 标签业务操作实现类
 */
@Service("tagsService")
public class TagsServiceImpl implements TagsService {

    @Autowired
    private TagsMapper tagsMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleService articleService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public DataMap findTagsInfoAndArticleCountNum(int rows, int pageNum) {
        PageHelper.startPage(pageNum, rows);
        List<Tags> tags = tagsMapper.selectAllTags();
        PageInfo<Tags> pageInfo = new PageInfo<>(tags);

        CommonReturn commonReturn = new CommonReturn();
        JSONArray tagJsonArray = new JSONArray();
        JSONObject returnJson = new JSONObject();
        JSONObject tagJson;

        for (Tags tag : tags) {
            tagJson = new JSONObject();

            tagJson.put("id", tag.getId());
            tagJson.put("tagName", tag.getTagName());
            tagJson.put("createDate", TimeUtil.getFormatDateForSix(tag.getCreateDate()));

            // 获得标签对应 最新一篇文章 的图片
            List<Object> articleIds = articleMapper.findArticleByTag(tag.getId());
            if (!Objects.isNull(articleIds) && articleIds.size() != 0) {
                Integer articleId = (Integer) articleIds.get(0);
                tagJson.put("imageUrl", articleService.getImageUrl(articleId));
            } else {
                tagJson.put("imageUrl", "该标签无对应的文章");
            }

            tagJson.put("tagArticleCountNum", tagsMapper.tagArticleCountNum(tag.getId()));
            tagJsonArray.add(tagJson);
        }
        returnJson.put("msg", "获得所有的标签信息以及该标签的文章总数");
        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        returnJson.put("result", tagJsonArray);
        return DataMap.success().setData(returnJson);
    }

    @Override
    public DataMap findTagsName() {
        List<Tags> tags = tagsMapper.selectAllTags();

        List<String> tagName = new ArrayList<>();
        for (Tags tag : tags) {
            tagName.add(tag.getTagName());
        }
        return DataMap.success().setData(tagName);
    }

    @Override
    public int countTagsNum() {
        return tagsMapper.selectCount(null);
    }

    @Override
    public DataMap findAllTags() {
        List<Tags> tags = tagsMapper.selectAllTags();

        JSONObject returnJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;

        for (Tags tag : tags) {
            jsonObject = new JSONObject();
            jsonObject.put("id", tag.getId());
            jsonObject.put("tagName", tag.getTagName());
            jsonObject.put("createDate", TimeUtil.getFormatDateForSix(tag.getCreateDate()));

            // 获得标签对应 最新一篇文章 的图片
            List<Object> articleIds = articleMapper.findArticleByTag(tag.getId());
            if (!Objects.isNull(articleIds) && articleIds.size() != 0) {
                Integer articleId = (Integer) articleIds.get(0);
                jsonObject.put("imageUrl", articleService.getImageUrl(articleId));
            } else {
                jsonObject.put("imageUrl", "该标签无对应的文章");
            }

            jsonObject.put("articleNum", tagsMapper.tagArticleCountNum(tag.getId()));

            jsonArray.add(jsonObject);
        }
        returnJson.put("msg", "获得所有的标签信息");
        returnJson.put("result", jsonArray);
        return DataMap.success(CodeType.FIND_TAGS_CLOUD).setData(returnJson);
    }

    @Override
    public DataMap updateTags(String tagName, int type) {
        tagName = tagName.trim();
        if (tagName.length() > StringUtil.TAG_NANE_MAX_LENGTH || Objects.equals(tagName, StringUtil.BLANK) ){
            return DataMap.fail(CodeType.TAGS_FORMAT_ERROR);
        }

        Tags isExistByTag = tagIsExistByTagName(tagName);
        logger.info("标签库中是否存在该标签：" + JSON.toJSONString(isExistByTag));
        LocalDateTime localDateTime = LocalDateTime.now();

        if (type == 1) {
            if (isExistByTag == null) {
                Tags tags = new Tags();
                tags.setTagName(tagName);
                tags.setCreateDate(localDateTime);
                tagsMapper.insert(tags);
                // 把插入的文章分类 id返回
                return DataMap.success(CodeType.ADD_TAGS_SUCCESS).setData(tags.getId());
            } else {
                return DataMap.fail(CodeType.TAGS_HAS_EXIST).setData(isExistByTag.getId());
            }
        } else {
            if (isExistByTag != null) {
                // 找出标签对应的id
                int articleNum = tagsMapper.tagArticleCountNum(isExistByTag.getId());
                if (articleNum > 0) {
                    return DataMap.fail(CodeType.TAGS_HAS_ARTICLE);
                }

                Example example = new Example(Tags.class);
                example.createCriteria().andEqualTo("tagName", tagName);
                tagsMapper.deleteByExample(example);

                return DataMap.success(CodeType.DELETE_TAGS_SUCCESS);
            } else {
                return DataMap.fail(CodeType.TAGS_NOT_EXIST);
            }
        }
    }

    @Override
    public Tags tagIsExistByTagName(String tagName) {
        Example example = new Example(Tags.class);
        example.createCriteria().andEqualTo("tagName", tagName);
        return tagsMapper.selectOneByExample(example);
    }
}
