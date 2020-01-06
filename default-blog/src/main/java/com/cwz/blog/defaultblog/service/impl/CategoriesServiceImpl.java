package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.Categories;
import com.cwz.blog.defaultblog.mapper.CategoriesMapper;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.service.CategoriesService;
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
 * @date: 2019/12/18
 * @description: 文章分类业务操作实现类
 */
@Service("categoriesService")
public class CategoriesServiceImpl implements CategoriesService {

    @Autowired
    private CategoriesMapper categoriesMapper;

    @Autowired
    private ArticleService articleService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Categories findCategoryByCategoryId(int categoryId) {
        Example example = new Example(Categories.class);
        example.createCriteria().andEqualTo("id", categoryId);
        return categoriesMapper.selectOneByExample(example);
    }

    @Override
    public Categories findCategoryByCategoryName(String categoryName) {
        Example example = new Example(Categories.class);
        example.createCriteria().andEqualTo("categoryName", categoryName);
        return categoriesMapper.selectOneByExample(example);
    }

    @Override
    public DataMap findCategoriesNameAndArticleCountNum() {
        List<Categories> categories = categoriesMapper.selectAll();

        JSONObject categoryJson;
        JSONArray categoryJsonArray = new JSONArray();
        JSONObject returnJson = new JSONObject();
        for (Categories category : categories) {
            categoryJson = new JSONObject();
            categoryJson.put("categoryName", category.getCategoryName());
            categoryJson.put("categoryArticleCountNum", articleService.countArticleCategoryByCategory(category.getId()));
            categoryJsonArray.add(categoryJson);
        }
        returnJson.put("msg", "获得所有的分类名称以及该分类的文章总数");
        returnJson.put("result", categoryJsonArray);
        return DataMap.success().setData(returnJson);
    }

    @Override
    public DataMap findCategoriesName() {
        List<Categories> categories = categoriesMapper.selectAll();

        List<String> categoryNames = new ArrayList<>();
        for (Categories category : categories) {
            categoryNames.add(category.getCategoryName());
        }
        return DataMap.success().setData(categoryNames);
    }

    @Override
    public int countCategoriesNum() {
        return categoriesMapper.selectCount(null);
    }

    @Override
    public DataMap findAllCategories(int rows, int pageNum) {
        PageHelper.startPage(pageNum, rows);
        List<Categories> categories = categoriesMapper.selectAll();
        PageInfo<Categories> pageInfo = new PageInfo<>(categories);

        CommonReturn commonReturn = new CommonReturn();
        JSONObject returnJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;

        for (Categories category : categories) {
            jsonObject = new JSONObject();
            jsonObject.put("id", category.getId());
            jsonObject.put("categoryName", category.getCategoryName());
            jsonObject.put("createDate", TimeUtil.getFormatDateForSix(category.getCreateDate()));
            jsonObject.put("description", category.getDescription());
            jsonObject.put("articleNum", articleService.countArticleCategoryByCategory(category.getId()));
            jsonArray.add(jsonObject);
        }

        returnJson.put("msg", "获得所有的文章分类信息");
        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        returnJson.put("result", jsonArray);
        return DataMap.success().setData(returnJson);
    }

    @Override
    public DataMap updateCategory(Categories categories, int type) {
        categories.setCategoryName(categories.getCategoryName().trim());
        if (categories.getCategoryName().length() > StringUtil.CATEGORY_NANE_MAX_LENGTH ||
                Objects.equals(categories.getCategoryName(), StringUtil.BLANK)){
            return DataMap.fail(CodeType.CATEGORY_FORMAT_ERROR);
        }

        int isExistCategory = categoriesMapper.findIsExistByCategoryName(categories.getCategoryName());
        LocalDateTime localDateTime = LocalDateTime.now();
        logger.info("分类库中是否存在该分类（大于等于1为存在，0为不存在）：" + isExistCategory);

        Example example = new Example(Categories.class);
        example.selectProperties("id");
        example.createCriteria().andEqualTo("categoryName", categories.getCategoryName());

        if (type == 1) {
            if (isExistCategory == 0) {
                categories.setCreateDate(localDateTime);
                categoriesMapper.insert(categories);
                // 把插入的文章分类 id返回
                return DataMap.success(CodeType.ADD_CATEGORY_SUCCESS).setData(categories.getId());
            } else {
                return DataMap.fail(CodeType.CATEGORY_EXIST);
            }
        } else if (type == 2){
            if (isExistCategory != 0) {
                // 找出分类对应的id
                Categories category = categoriesMapper.selectOneByExample(example);

                int articleNum = articleService.countArticleCategoryByCategory(category.getId());
                if (articleNum > 0) {
                    return DataMap.fail(CodeType.CATEGORY_HAS_ARTICLE);
                }

                categoriesMapper.deleteByExample(example);
                return DataMap.success(CodeType.DELETE_CATEGORY_SUCCESS);
            } else {
                return DataMap.fail(CodeType.CATEGORY_NOT_EXIST);
            }
        } else if (type == 3) {
            if (isExistCategory != 0) {
                categoriesMapper.updateByExampleSelective(categories, example);
                return DataMap.success(CodeType.UPDATE_CATEGORY_SUCCESS);
            } else {
                return DataMap.fail(CodeType.CATEGORY_NOT_EXIST);
            }
        }
        return null;
    }
}
