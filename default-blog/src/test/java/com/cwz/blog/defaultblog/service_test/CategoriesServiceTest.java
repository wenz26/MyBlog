package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.entity.Categories;
import com.cwz.blog.defaultblog.service.CategoriesService;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoriesServiceTest {

    @Autowired
    private CategoriesService categoriesService;

    @Test
    void findCategoriesNameAndArticleCountNum() {
        DataMap dataMap = categoriesService.findCategoriesNameAndArticleCountNum();
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void findCategoriesName() {
        DataMap categoriesName = categoriesService.findCategoriesName();
        System.out.println(JSON.toJSONString(categoriesName));

    }

    @Test
    void countCategoriesNum() {
        int i = categoriesService.countCategoriesNum();
        System.out.println(i);
    }

    @Test
    void findAllCategories() {
        DataMap allCategories = categoriesService.findAllCategories(5, 0, null, null, null);
        System.out.println(JSON.toJSONString(allCategories));
    }

    @Test
    void updateCategory() {
        Categories categories = new Categories();
        categories.setCategoryName("我的故事");
        categories.setDescription("asddss");

        DataMap dataMap = categoriesService.updateCategory(categories, 3);
        System.out.println(JSON.toJSONString(dataMap));
    }
}
