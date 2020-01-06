package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.entity.Categories;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: 陈文振
 * @date: 2019/12/18
 * @description: 文章分类业务操作
 */
public interface CategoriesService {

    /**
     * @description: 通过CategoryId获得Category
     * @author: 陈文振
     * @date: 2019/12/25
     * @param categoryId
     * @return: com.cwz.blog.defaultblog.entity.Categories
     */
    Categories findCategoryByCategoryId(int categoryId);

    /**
     * @description: 通过CategoryName获得Category
     * @author: 陈文振
     * @date: 2019/12/25
     * @param categoryName
     * @return: com.cwz.blog.defaultblog.entity.Categories
     */
    Categories findCategoryByCategoryName(String categoryName);

    /**
     * @description: 获得所有的分类以及该分类的文章总数
     * @author: 陈文振
     * @date: 2019/12/18
     * @return:
     */
    DataMap findCategoriesNameAndArticleCountNum();

    /**
     * @description: 获得所有的分类名称
     * @author: 陈文振
     * @date: 2019/12/18
     * @return:
     */
    DataMap findCategoriesName();

    /**
     * @description: 获得分类数目
     * @author: 陈文振
     * @date: 2019/12/18
     * @return:
     */
    int countCategoriesNum();

    /**
     * @description: 获得分类名和对应id
     * @author: 陈文振
     * @date: 2019/12/18
     * @return:
     */
    DataMap findAllCategories(int rows, int pageNum);

    /**
     * @description: 更新分类
     * @author: 陈文振
     * @date: 2019/12/18
     * @param categories: 分类主类
     * @param type: 1--增加分类   2--删除分类   3--修改分类（分类名不能修改，只能修改分类具体描述）
     * @return:
     */
    @Transactional
    DataMap updateCategory(Categories categories, int type);
}
