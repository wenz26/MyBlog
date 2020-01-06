package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.component.StringAndArray;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.constant.SiteOwner;
import com.cwz.blog.defaultblog.entity.Article;
import com.cwz.blog.defaultblog.entity.Categories;
import com.cwz.blog.defaultblog.entity.Tags;
import com.cwz.blog.defaultblog.mapper.ArticleMapper;
import com.cwz.blog.defaultblog.service.*;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: 陈文振
 * @date: 2019/12/3
 * @description: 文章业务操作实现类
 */
@Service("articleService")
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleLikesRecordService articleLikesRecordService;
    @Autowired
    private VisitStatisticsService visitStatisticsService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentLikesRecordService commentLikesRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoriesService categoriesService;
    @Autowired
    private TagsService tagsService;
    @Autowired
    private ArticleUserFavoriteRecordService articleUserFavoriteRecordService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public synchronized DataMap insertArticle(Article article, String username) {
        LocalDateTime localDateTime = LocalDateTime.now();

        int userId = userService.findIdByUsername(username);

        try {
            if (Objects.equals(StringUtil.BLANK, article.getOriginalAuthor()) || article.getOriginalAuthor() == null) {
                article.setOriginalAuthor(username);
            }
            if (Objects.equals(StringUtil.BLANK, article.getArticleUrl()) || article.getArticleUrl() == null) {
                article.setArticleUrl("test");
            }

            // 当前（未插入新纪录前）最后一篇已发布文章的id
            Article endArticleId = articleMapper.findEndArticleId();
            // 设置文章的上一篇文章id
            if (endArticleId != null && article.getDraft() == 1) {
                logger.info("设置文章的上一篇文章id");
                article.setLastArticleId(endArticleId.getId());
            }

            article.setUserId(userId);
            article.setPublishDate(localDateTime);
            article.setUpdateDate(localDateTime);

            String[] tagNames = {};
            if (!Objects.equals(StringUtil.BLANK, article.getTagName()) && article.getTagName() != null){
                logger.info("设置标签");
                tagNames = StringAndArray.stringToArray(article.getTagName());
            }

            // 设置文章分类 这里前端可以设置传相对应的 分类id （通过找到所有的分类用个下拉框）

            // 插入 文章
            article.setTagName(null);
            articleMapper.insertSelective(article);
            // 修改文章的url
            // 保存文章url
            String url = SiteOwner.SITE_OWNER_URL + "/article/" + article.getId();
            articleMapper.updateArtcileUrlById(article.getId(), url);

            // 设置文章对应的标签
            if (tagNames.length != 0) {
                for (String tagName : tagNames) {
                    // 判断这个tag有没有在标签库存在
                    DataMap tagDataMap = tagsService.updateTags(tagName, 1);
                    articleMapper.insertArticleAndTags(article.getId(), (Integer) tagDataMap.getData());
                }
            }


            if (article.getDraft() == 1) {
                logger.info("新文章加入访客量");
                // 新文章加入访客量
                visitStatisticsService.insertVisitArticlePage("article/" + article.getId());
            }

            // 设置上一篇文章的下一篇文章id
            if (endArticleId != null && article.getDraft() == 1) {
                logger.info("设置上一篇文章的下一篇文章id");
                updateArticleLastOrNextId("nextArticleId", article.getId(), endArticleId.getId());
            }

            Map<String, Object> dataMap = getDataMap(article, username);
            return DataMap.success().setData(dataMap);
        } catch (Exception e) {
            logger.error("发布文章 [{}] 出现异常：", article.getArticleTitle(), e);
            return DataMap.fail(CodeType.PUBLISH_ARTICLE_EXCEPTION);
        }
    }

    @Override
    public synchronized DataMap updateArticleById(Article article, String username) {

        Article oldArtcile = findArtcileInfoById(article.getId());
        Integer oldDraft = oldArtcile.getDraft();

        // 当 用户从转载改为原创时，要进行以下操作把原作者和文章url改过来，但是如果从原创改为转载就不用，前端有传值实现
        if (Objects.equals("原创", article.getArticleType())) {
            article.setOriginalAuthor(username);
            String url = SiteOwner.SITE_OWNER_URL + "/article/" + article.getId();
            article.setArticleUrl(url);
        }

        // 插入其他 文章信息
        String[] tagNames;
        if (!Objects.equals(StringUtil.BLANK, article.getTagName()) && article.getTagName() != null){
            tagNames = StringAndArray.stringToArray(article.getTagName());
            // 设置文章对应的标签
            if (tagNames.length != 0) {
                logger.info("设置文章对应的标签");
                // 先把 原本的标签进行删除
                articleMapper.deleteArticleAndTags(article.getId());

                for (String tagName : tagNames) {
                    // 判断这个tag有没有在标签库存在
                    DataMap tagDataMap = tagsService.updateTags(tagName, 1);
                    articleMapper.insertArticleAndTags(article.getId(), (Integer) tagDataMap.getData());
                }
            }
        }
        // 当前（未插入新纪录前）最后一篇已发布文章的id
        Article endArticleId = articleMapper.findEndArticleId();
        // 设置上一篇文章的下一篇文章id 和 该文章的上一篇文章id
        if (endArticleId != null && article.getDraft() == 1 && !Objects.equals(oldDraft, article.getDraft())) {
            logger.info("设置上一篇文章的下一篇文章id 和 该文章的上一篇文章id 和 修改发布时间");
            article.setLastArticleId(endArticleId.getId());
            updateArticleLastOrNextId("nextArticleId", article.getId(), endArticleId.getId());

            LocalDateTime localDateTime = LocalDateTime.now();
            article.setPublishDate(localDateTime);
        }
        if (article.getDraft() == 1 && !Objects.equals(oldDraft, article.getDraft())) {
            logger.info("新文章加入访客量");
            // 新文章加入访客量
            visitStatisticsService.insertVisitArticlePage("article/" + article.getId());
        }

        if (Objects.equals(oldDraft, article.getDraft())) {
            logger.info("这是已发布或者是还是在草稿箱中的文章");
            article.setLikes(null);
            article.setFavorites(null);
            article.setLastArticleId(null);
            article.setNextArticleId(null);
        }

        article.setTagName(null);
        Example example = new Example(Article.class);
        example.createCriteria().andEqualTo("id", article.getId());
        int update = articleMapper.updateByExampleSelective(article, example);
        logger.info("文章[{}]进行了修改的次数：" + update, article.getArticleTitle());

        Map<String, Object> dataMap = getDataMap(article, username);
        return DataMap.success().setData(dataMap);
    }

    @Override
    public DataMap getArticleByArticleId(int articleId, String username) {
        Article article = articleMapper.getArticleAndTagsByArticleId(articleId, null);

        if (article != null) {
            Map<String, Object> dataMap = new ConcurrentHashMap<>(32);

            String author = userService.findUsernameById(article.getUserId());
            Article lastArticle = findArtcileInfoById(article.getLastArticleId());
            Article nextArticle = findArtcileInfoById(article.getNextArticleId());

            dataMap.put("articleId", articleId);
            dataMap.put("author", author);
            dataMap.put("userId", article.getUserId());
            dataMap.put("originalAuthor", article.getOriginalAuthor());
            dataMap.put("articleTitle", article.getArticleTitle());
            dataMap.put("publishDate", TimeUtil.getFormatDateForSix(article.getPublishDate()));
            dataMap.put("updateDate", TimeUtil.getFormatDateForSix(article.getUpdateDate()));
            dataMap.put("articleContent", article.getArticleContent());
            dataMap.put("articleTabloid", article.getArticleTabloid());

            List<Tags> tags = article.getTags();
            String[] tagNames = new String[tags.size()];
            for (int i = 0; i < tags.size(); i++) {
                tagNames[i] = tags.get(i).getTagName();
            }
            dataMap.put("articleTags", tagNames);
            dataMap.put("articleType", article.getArticleType());
            dataMap.put("articleCategories", categoriesService.findCategoryByCategoryId(article.getArticleCategories()).getCategoryName());
            dataMap.put("articleUrl", article.getArticleUrl());
            if (!Objects.isNull(article.getImageUrl())){
                dataMap.put("imageUrl", article.getImageUrl());
            }
            dataMap.put("likes", article.getLikes());
            dataMap.put("favorites", article.getFavorites());
            dataMap.put("draft", article.getDraft());

            if (username == null) {
                dataMap.put("isLiked", 0);
                dataMap.put("isFavorite", 0);
            } else {
                if (articleLikesRecordService.isLike(articleId, username)) {
                    dataMap.put("isLiked", 1);
                } else {
                    dataMap.put("isLiked", 0);
                }

                if (articleUserFavoriteRecordService.isFavorite(articleId, username)) {
                    dataMap.put("isFavorite", 1);
                } else {
                    dataMap.put("isFavorite", 0);
                }
            }


            if (lastArticle != null) {
                dataMap.put("lastStatus","200");
                dataMap.put("lastArticleTitle", lastArticle.getArticleTitle());
                dataMap.put("lastArticleUrl", "/article/" + lastArticle.getId());
            } else {
                dataMap.put("lastStatus","500");
                dataMap.put("lastInfo","无");
            }

            if (nextArticle != null) {
                dataMap.put("nextStatus", "200");
                dataMap.put("nextArticleTitle", nextArticle.getArticleTitle());
                dataMap.put("nextArticleUrl", "/article/" + nextArticle.getId());
            } else {
                dataMap.put("nextStatus","500");
                dataMap.put("nextInfo","无");
            }
            return DataMap.success().setData(dataMap);
        }
        return DataMap.fail(CodeType.ARTICLE_NOT_EXIST);
    }

    @Override
    public Map<String, String> findArticleTitleByArticleId(int articleId) {
        Example example = new Example(Article.class);
        example.selectProperties("articleTitle", "articleTabloid");
        example.createCriteria().andEqualTo("id", articleId);
        Article article = articleMapper.selectOneByExample(example);

        Map<String, String> articleMap = new ConcurrentHashMap<>();
        if (article != null) {
            articleMap.put("articleTitle", article.getArticleTitle());
            articleMap.put("articleTabloid", article.getArticleTabloid());
        }
        return articleMap;
    }

    @Override
    public DataMap findAllToPublishArticles(int rows, int pageNum) {
        JSONArray jsonArray = getAllToDraftArticles(rows, pageNum, 1, null);
        return DataMap.success().setData(jsonArray);
    }

    @Override
    public DataMap findAllToDraftArticles(String username, int rows, int pageNum) {
        JSONArray jsonArray = getAllToDraftArticles(rows, pageNum, 0, username);
        return DataMap.success().setData(jsonArray);
    }

    @Override
    public void updateArticleLastOrNextId(String lastOrNext, int lastOrNextArticleId, int articleId) {
        if (Objects.equals("lastArticleId", lastOrNext)) {
            articleMapper.updateArticleLastId(lastOrNextArticleId, articleId);
        }
        if (Objects.equals("nextArticleId", lastOrNext)) {
            articleMapper.updateArticleNextId(lastOrNextArticleId, articleId);
        }
    }

    @Override
    public DataMap updateLikeByArticleId(int articleId) {

        articleMapper.updateLikeByArticleId(articleId);
        int likes = articleMapper.findLikesByArticleId(articleId);
        return DataMap.success().setData(likes);
    }

    @Override
    public DataMap updateFavoriteByArticleId(int articleId) {

        articleMapper.updateFavoriteByArticleId(articleId);
        int favorites = articleMapper.findFavoritesByArticleId(articleId);
        return DataMap.success().setData(favorites);
    }

    @Override
    public DataMap findArticleByTag(String tagName, int rows, int pageNum) {
        // 找到 标签名 对应的标签
        Tags tag = tagsService.tagIsExistByTagName(tagName);

        PageHelper.startPage(pageNum, rows);
        List<Object> articleIds = articleMapper.findArticleByTag(tag.getId());
        PageInfo<Object> pageInfo = new PageInfo<>(articleIds);

        JSONObject articleJson;
        JSONArray articleJsonArray = new JSONArray();
        JSONObject resultObject = new JSONObject();
        CommonReturn commonReturn = new CommonReturn();

        for (Object artcileId : articleIds) {
            Article article = articleMapper.getArticleAndTagsByArticleId((Integer) artcileId, 1);

            articleJson = getArticleByTagAndCategoryToJsonObject(article);
            articleJsonArray.add(articleJson);
        }

        resultObject.put("result", articleJsonArray);
        resultObject.put("tag", tagName);
        resultObject.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        return DataMap.success(CodeType.FIND_ARTICLE_BY_TAG).setData(resultObject);
    }

    @Override
    public DataMap findArticleByCategory(String categoryName, int rows, int pageNum) {
        Categories category = categoriesService.findCategoryByCategoryName(categoryName);

        List<Article> articles;
        PageInfo<Article> pageInfo;
        JSONObject articleObject;
        JSONArray articleJsonArray = new JSONArray();
        JSONObject resultObject = new JSONObject();
        CommonReturn commonReturn = new CommonReturn();

        PageHelper.startPage(pageNum, rows);
        if (category == null) {
            articles = articleMapper.findAllToDraftArticles(1, null, null, null);
            categoryName = "全部分类";
        } else {
            articles = articleMapper.findAllToDraftArticles(1, category.getId(), null, null);
        }
        pageInfo = new PageInfo<>(articles);

        for (Article article : articles) {
            articleObject = getArticleByTagAndCategoryToJsonObject(article);
            articleJsonArray.add(articleObject);
        }
        resultObject.put("result", articleJsonArray);
        resultObject.put("category", categoryName);
        resultObject.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        return DataMap.success().setData(resultObject);
    }

    @Override
    public DataMap findArticleByArchive(String archiveDay, String username, int rows, int pageNum) {
        Integer userId = null;
        if (!Objects.isNull(username) && !Objects.equals(username, StringUtil.BLANK)) {
            userId = userService.findIdByUsername(username);
        }
        if (!Objects.isNull(archiveDay)) {
            archiveDay = archiveDay.trim();
        }

        List<Article> articles;
        PageInfo<Article> pageInfo;

        JSONObject articleObject;
        JSONArray articleJsonArray = new JSONArray();
        JSONObject resultObject = new JSONObject();

        TimeUtil timeUtil = new TimeUtil();
        CommonReturn commonReturn = new CommonReturn();
        String showMonth = "hide";

        // true表示需要统计总数，这样会多进行一次请求select count(0)；省略掉true参数只返回分页数据。
        PageHelper.startPage(pageNum, rows);

        if (Objects.equals(archiveDay, StringUtil.BLANK) || Objects.isNull(archiveDay)) {
            logger.info("没有传入对应的日期，这里查找全部博文");
            articles = articleMapper.findAllToDraftArticles(1, null, null, userId);
        } else {
            archiveDay = timeUtil.timeYearToWhippletree(archiveDay);
            logger.info("这里查找日期为[{}]的博文信息", archiveDay);
            articles = articleMapper.findAllToDraftArticles(1, null, archiveDay, userId);
            showMonth = "show";
        }
        pageInfo = new PageInfo<>(articles);

        for (Article article : articles) {
            articleObject = getArticleByTagAndCategoryToJsonObject(article);
            articleJsonArray.add(articleObject);
        }

        resultObject.put("result", articleJsonArray);
        resultObject.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));

        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("draft", 1);
        if (!Objects.isNull(userId)) {
            criteria.andEqualTo("userId", userId);
        }
        resultObject.put("articleNum", articleMapper.selectCountByExample(example));

        resultObject.put("showMonth", showMonth);
        return DataMap.success().setData(resultObject);
    }

    @Override
    public DataMap getDraftArticle(Article article) {
        Map<String, Object> dataMap = new ConcurrentHashMap<>(16);
        dataMap.put("id", article.getId());
        dataMap.put("articleTitle", article.getArticleTitle());

        if (!Objects.isNull(article.getArticleType())){
            dataMap.put("articleType", article.getArticleType());
        }

        if (!Objects.isNull(article.getArticleCategories())){
            dataMap.put("articleCategories", categoriesService.findCategoryByCategoryId(article.getArticleCategories()).getCategoryName());
        }

        dataMap.put("articleUrl", article.getArticleUrl());

        if (!Objects.isNull(article.getImageUrl())){
            dataMap.put("imageUrl", article.getImageUrl());
        }

        dataMap.put("originalAuthor", article.getOriginalAuthor());
        dataMap.put("articleContent", article.getArticleContent());
        dataMap.put("draft", article.getDraft());

        List<Tags> tags = article.getTags();
        String[] tagNames = new String[tags.size()];
        for (int i = 0; i < tags.size(); i++) {
            tagNames[i] = tags.get(i).getTagName();
        }
        dataMap.put("articleTags", tagNames);
        return DataMap.success().setData(dataMap);
    }

    @Override
    public DataMap getArticleManagement(int rows, int pageNum, Integer userId, Integer draft) {

        PageHelper.startPage(pageNum, rows);
        List<Article> articles = articleMapper.findAllToDraftArticles(draft, null, null, userId);
        PageInfo<Article> pageInfo = new PageInfo<>(articles);

        JSONArray returnJsonArray = new JSONArray();
        JSONObject returnJson = new JSONObject();
        JSONObject articleJson;
        CommonReturn commonReturn = new CommonReturn();

        for (Article article : articles) {
            articleJson = getArticleByTagAndCategoryToJsonObject(article);
            String statisticsName = "article/" + article.getId();
            articleJson.put("visitNum", visitStatisticsService.getNumByStatisticsName(statisticsName));
            returnJsonArray.add(articleJson);
        }

        returnJson.put("result", returnJsonArray);
        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        return DataMap.success().setData(returnJson);
    }

    @Override
    public Article findArticleById(int id) {
        return articleMapper.getArticleAndTagsByArticleId(id, null);
    }

    @Override
    public int countArticleCategoryByCategory(int category) {
        Example example = new Example(Article.class);
        example.createCriteria().andEqualTo("articleCategories", category);
        return articleMapper.selectCountByExample(example);
    }


    @Override
    public int countArticleArchiveByArchive(String archiveDay) {
        Example articleExample = new Example(Article.class);
        Example.Criteria articleCriteria = articleExample.createCriteria();
        articleCriteria.andLike("publishDate", "%" + archiveDay + "%");
        return articleMapper.selectCountByExample(articleExample);
    }

    @Override
    public int countArticleToPublish() {
        Example example = new Example(Article.class);
        example.createCriteria().andEqualTo("draft", 1);
        return articleMapper.selectCountByExample(example);
    }

    @Override
    public int countArticleToDraft() {
        Example example = new Example(Article.class);
        example.createCriteria().andEqualTo("draft", 0);
        return articleMapper.selectCountByExample(example);
    }

    @Override
    public DataMap deleteArticle(int id) {
        try {
            Article deleteArticle = findArtcileInfoById(id);
            // 修改上一篇文章和下一篇文章的id
            articleMapper.updateLastOrNextId("last_article_id", deleteArticle.getLastArticleId(), deleteArticle.getNextArticleId());
            articleMapper.updateLastOrNextId("next_article_id", deleteArticle.getNextArticleId(), deleteArticle.getLastArticleId());
            // 删除文章
            articleMapper.deleteByArticleId(id);
            // 删除与该文章有关的所有文章点赞记录、文章评论、文章评论点赞记录、文章标签 文章收藏记录(这个不用系统去去掉)
            // 有一个 访问量
            commentService.deleteCommentByArticleId(id);
            commentLikesRecordService.deleteCommentLikesRecordByArticleId(id);
            articleLikesRecordService.deleteArticleLikesRecordByArticleId(id);
            articleMapper.deleteArticleAndTags(id);
        } catch (Exception e) {
            logger.error("删除文章出现错误，文章的id是[{}]：", id, e);
            return DataMap.fail(CodeType.DELETE_ARTICLE_FAIL);
        }
        return DataMap.success();
    }

    @Override
    public String getImageUrl(int articleId) {
        Example example = new Example(Article.class);
        example.selectProperties("imageUrl");
        example.createCriteria().andEqualTo("id", articleId);
        return articleMapper.selectOneByExample(example).getImageUrl();
    }

    /**
     * @description: 对插入或者修改时返回会数据进行封转
     * @author: 陈文振
     * @date: 2019/12/24
     * @param article
     * @param username
     * @return: java.util.Map
     */
    private Map<String, Object> getDataMap(Article article, String username){
        Map<String, Object> dataMap = new ConcurrentHashMap<>(10);
        dataMap.put("articleTitle", article.getArticleTitle());
        dataMap.put("updateDate", TimeUtil.getFormatDateForSix(article.getUpdateDate()));
        dataMap.put("author", username);
        // 本博客中的URL
        dataMap.put("articleUrl", "/article/" + article.getId());
        // 该文章是发布了还是在草稿箱（1为已发布，0为草稿）
        dataMap.put("draft", article.getDraft());
        if (!Objects.isNull(article.getImageUrl())){
            dataMap.put("imageUrl", article.getImageUrl());
        }
        return dataMap;
    }

    /**
     * @description: 通过 文章Id 来找到文章的具体信息
     * @author: 陈文振
     * @date: 2019/12/25
     * @param articleId
     * @return: com.cwz.blog.defaultblog.entity.Article
     */
    private Article findArtcileInfoById(int articleId) {
        Example example = new Example(Article.class);
        example.excludeProperties("tagName");
        example.createCriteria().andEqualTo("id", articleId);
        return articleMapper.selectOneByExample(example);
    }

    /**
     * @description: 根据 draft(是否是草稿或已发布) 来找到相对应的所有文章
     * @author: 陈文振
     * @date: 2019/12/25
     * @param rows
     * @param pageNum
     * @param draft: 是否为草稿（1为已发布，0为草稿）
     * @return: com.alibaba.fastjson.JSONArray
     */
    private JSONArray getAllToDraftArticles(int rows, int pageNum, int draft, String username){
        Integer userId = null;
        if (!Objects.isNull(username) && !Objects.equals(username, StringUtil.BLANK)) {
            userId = userService.findIdByUsername(username);
        }
        CommonReturn commonReturn = new CommonReturn();
        PageHelper.startPage(pageNum, rows);

        List<Article> articles = articleMapper.findAllToDraftArticles(draft, null, null, userId);


        PageInfo<Article> pageInfo = new PageInfo<>(articles);
        List<Map<String, Object>> newArticles = new ArrayList<>();
        Map<String, Object> map;

        for (Article article : articles) {
            map = new ConcurrentHashMap<>();
            map.put("thisArticleUrl", "/article/" + article.getId());

            List<Tags> tags = article.getTags();
            String[] tagNames = new String[tags.size()];
            for (int i = 0; i < tags.size(); i++) {
                tagNames[i] = tags.get(i).getTagName();
            }
            map.put("articleTags", tagNames);
            map.put("articleTitle", article.getArticleTitle());
            if (!Objects.isNull(article.getArticleType())){
                map.put("articleType", article.getArticleType());
            }
            map.put("publishDate", TimeUtil.getFormatDateForSix(article.getPublishDate()));
            map.put("originalAuthor", article.getOriginalAuthor());
            if (!Objects.isNull(article.getArticleCategories())){
                map.put("articleCategories", categoriesService.findCategoryByCategoryId(article.getArticleCategories()).getCategoryName());
            }
            map.put("articleTabloid", article.getArticleTabloid());
            map.put("likes", article.getLikes());
            map.put("favorites", article.getFavorites());
            map.put("draft", article.getDraft());
            if (!Objects.isNull(article.getImageUrl())){
                map.put("imageUrl", article.getImageUrl());
            }
            newArticles.add(map);
        }

        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(newArticles));
        Map<String, Object> thisPageInfo = commonReturn.mapToPageInfo(pageInfo);
        jsonArray.add(thisPageInfo);
        return jsonArray;
    }

    /**
     * @description: 对 通过标签查找文章 返回的数据进行一个封装
     * @author: 陈文振
     * @date: 2019/12/25
     * @param article
     * @return: com.alibaba.fastjson.JSONObject
     */
    public JSONObject getArticleByTagAndCategoryToJsonObject(Article article){
        JSONObject articleJson = new JSONObject();
        articleJson.put("articleId", article.getId());
        articleJson.put("originalAuthor", article.getOriginalAuthor());
        articleJson.put("articleTitle", article.getArticleTitle());

        if (!Objects.isNull(article.getArticleCategories())){
            articleJson.put("articleCategories", categoriesService.findCategoryByCategoryId(article.getArticleCategories()).getCategoryName());
        }

        articleJson.put("publishDate", TimeUtil.getFormatDateForSix(article.getPublishDate()));

        List<Tags> tags = article.getTags();
        String[] tagNames = new String[tags.size()];
        for (int i = 0; i < tags.size(); i++) {
            tagNames[i] = tags.get(i).getTagName();
        }
        articleJson.put("articleTags", tagNames);
        if (!Objects.isNull(article.getImageUrl())){
            articleJson.put("imageUrl", article.getImageUrl());
        }
        articleJson.put("draft", article.getDraft());
        return articleJson;
    }

}
