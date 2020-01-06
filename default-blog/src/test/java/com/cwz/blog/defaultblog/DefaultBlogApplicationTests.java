package com.cwz.blog.defaultblog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.Article;
import com.cwz.blog.defaultblog.entity.Categories;
import com.cwz.blog.defaultblog.entity.Tags;
import com.cwz.blog.defaultblog.mapper.ArticleMapper;
import com.cwz.blog.defaultblog.mapper.CategoriesMapper;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.utils.StringUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import javax.xml.crypto.Data;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootTest
class DefaultBlogApplicationTests {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CategoriesMapper categoriesMapper;

    @Test
    void contextLoads() {
        /*Date date = new Date();
        System.out.println(date.getTime());

        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();
        Categories categories = new Categories();
        categories.setCategoryName("你的我的");
        categories.setCreateDate(time);
        categories.setDescription("啦啦");
        categoriesMapper.insert(categories);*/

        /*List<Categories> categories = categoriesMapper.selectAll();
        Categories categorie = categories.get(0);
        System.out.println(JSONObject.toJSONString(categorie));
        Date createDate = categorie.getCreateDate();*/

        /*LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        Categories categories = new Categories();
        categories.setCategoryName("你的我的");
        categories.setCreateDate(now);
        categories.setDescription("啦啦");
        categoriesMapper.insert(categories);*/

        List<Categories> categories1 = categoriesMapper.selectAll();
        Categories categories2 = categories1.get(0);
        System.out.println(categories2.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


    }

    @Test
    void stringToUniCode(){
        String gbString = "陈文振";
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            System.out.println(hexB);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        System.out.println(unicodeBytes);
    }

    @Test
    void stringTest(){
        List<String> strings = new ArrayList<>();
        strings.add("aaa");
        strings.add("bbb");
        strings.add("ccc");
        strings.add("ddd");
        strings.add("eee");

        System.out.println("strings的值为：" + strings.toString());
        System.out.println("strings的长度为：" + strings.size());

        List<List<String>> lists = Collections.singletonList(strings);
        System.out.println("lists的值为：" + lists.toString());
        System.out.println("lists的长度为：" + lists.size());

        // Collections.singletonList返回长度为 1 的List列表
        JSONArray jsonArray = new JSONArray(Collections.singletonList(strings));
        System.out.println("jsonArray的值为：" + jsonArray.toString());

        JSONArray jsonArray1 = JSONArray.parseArray(JSONObject.toJSONString(strings));
        System.out.println("jsonArray1的值为：" + jsonArray1.toString());
    }

    @Test
    void sqlTest(){
        System.out.println(articleMapper.selectCount(null));

        Article article = new Article();
        article.setLikes(3);

        System.out.println(articleMapper.selectCount(article));
    }

    @Test
    void listTest(){
        List<String> lists = new ArrayList<>();
        System.out.println(lists.size());

        lists.add("123");
        lists.add("我的");
        lists.add("2913-87");
        lists.add("294年5月");
        addList(lists, "wdnmd");

        System.out.println(lists.size());
        System.out.println(lists.indexOf("294年5"));
    }

    private void addList(List list, Object value) {
        list.add(value);
    }

    @Test
    void listToJSONArray() {
        List<String> lists = new ArrayList<>();
        lists.add("123");
        lists.add("我的");
        lists.add("2913-87");
        lists.add("294年5月");

        System.out.println(lists.get(2));

        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(lists));
        System.out.println(jsonArray);
        System.out.println(jsonArray.getClass());
    }

    @Test
    void reflectionTest() throws Exception {
        Tags tags = new Tags();
        tags.setId(1);
        tags.setTagName("lalala");
        System.out.println("修饰符：" + tags.getClass().getModifiers());
        System.out.println("类型：" + tags.getClass().getTypeName());

        Field[] fields = Tags.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            System.out.println(field.getName());
            System.out.println(field.get(tags));
        }

        Tags t = (Tags) Class.forName("com.cwz.blog.defaultblog.entity.Tags").newInstance();
        t.setTagName("表情签");
        System.out.println(JSON.toJSONString(t));

    }

    @Test
    void stringNullTest() {
        String a = " ";
        System.out.println(Objects.equals(a, StringUtil.BLANK));
        System.out.println(Objects.equals(a.trim(), StringUtil.BLANK));
    }

    @Test
    void timeTest() throws FileNotFoundException {
        /*LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(JSON.toJSONString(localDateTime));
        System.out.println(JSON.toJSONString(localDateTime.toString()));*/

        System.out.println(ResourceUtils.getURL("classpath:").getPath());
    }
}
