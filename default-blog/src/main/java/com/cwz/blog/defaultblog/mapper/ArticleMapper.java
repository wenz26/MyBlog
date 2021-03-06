package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.Article;
import com.cwz.blog.defaultblog.entity.Tags;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper extends BeanMapper<Article> {

    /*@Select("select * from article where id = #{articleId}")
    @Results({
            @Result(property = "tags", javaType = List.class, many = @Many(select = "com.cwz.blog.defaultblog.mapper.ArticleMapper.findTagNameByArticleId"))
    })*/
    Article getArticleAndTagsByArticleId(@Param("articleId") int articleId, @Param("draft") Integer draft);

    /*@Select("select tag_name from article u right join article_tags ats on u.id = ats.article_id left join tags t on t.id = ats.tag_id where u.id = #{articleId}")
    Tags findTagNameByArticleId(@Param("articleId") int articleId);*/

    @Select("select id from article where draft = 1 and last_article_id <> 0 and next_article_id = 0 limit 1")
    Article findEndArticleId();

    @Insert("insert into article_tags values(#{articleId}, #{tagId})")
    void insertArticleAndTags(@Param("articleId") int articleId, @Param("tagId") int tagId);

    @Update("update article set last_article_id = #{lastArticleLd} where id = #{articleId}")
    void updateArticleLastId(@Param("lastArticleLd") int lastArticleLd, @Param("articleId") int articleId);

    @Update("update article set next_article_id = #{nextArticleId} where id = #{articleId}")
    void updateArticleNextId(@Param("nextArticleId") int nextArticleId, @Param("articleId") int articleId);

    List<Article> findAllToDraftArticles(@Param("draft") Integer draft, @Param("categoryId") Integer categoryId,
                                         @Param("publishDate") String publishDate, @Param("userId") Integer userId,
                                         @Param("articleTitle") String articleTitle,
                                         @Param("pageNum") int pageNum, @Param("rows") int rows);

    List<Article> findTimeRangeToDraftArticles(@Param("draft") Integer draft, @Param("categoryId") Integer categoryId,
                                               @Param("publishDate") String publishDate, @Param("userId") Integer userId,
                                               @Param("articleTitle") String articleTitle, @Param("timeRange") String timeRange,
                                               @Param("pageNum") int pageNum, @Param("rows") int rows);

    @Update("update article set likes = likes + 1 where id = #{articleId}")
    void updateLikeByArticleId(@Param("articleId") int articleId);

    @Select("select IFNULL(max(likes), 0) from article where id = #{articleId}")
    int findLikesByArticleId(@Param("articleId") int articleId);

    @Update("update article set favorites = favorites + 1 where id = #{articleId}")
    void updateFavoriteByArticleId(@Param("articleId") int articleId);

    @Select("select IFNULL(max(favorites), 0) from article where id = #{articleId}")
    int findFavoritesByArticleId(@Param("articleId") int articleId);


    @Select("select article_id from article_tags where tag_id = #{tagId} order by article_id desc")
    List<Object> findArticleByTag(@Param("tagId") int tagId);

    List<Article> selectArticleByTag(@Param("draft") Integer draft, @Param("categoryId") Integer categoryId, @Param("tagId") int tagId,
                                   @Param("timeRange") String timeRange, @Param("pageNum") int pageNum, @Param("rows") int rows);

    int countArticleByTag(@Param("draft") Integer draft, @Param("categoryId") Integer categoryId, @Param("tagId") int tagId,
                          @Param("timeRange") String timeRange);

    @Select("select id from article where article_categories = #{categoryId} and draft = 1 order by publish_date desc")
    List<Integer> findArticleByCategory(@Param("categoryId") int categoryId);

    @Update("update article set ${lastOrNextStr} = #{updateId} where id = #{articleId}")
    void updateLastOrNextId(@Param("lastOrNextStr") String lastOrNextStr, @Param("updateId") int updateId, @Param("articleId") int articleId);

    @Delete("delete from article where id = #{articleId}")
    void deleteByArticleId(@Param("articleId") int articleId);

    @Delete("delete from article_tags where article_id = #{articleId}")
    void deleteArticleAndTags(@Param("articleId") int articleId);

    @Update("update article set article_url = #{articleUrl} where id = #{articleId}")
    void updateArtcileUrlById(@Param("articleId") int articleId, @Param("articleUrl") String articleUrl);

    @Select("SELECT * FROM article")
    void selectAllByArticle();

    @Select("select user_id from article where id = #{articleId}")
    int findUserIdByArticleId(@Param("articleId") int articleId);

    @Select("select COUNT(*) from article_tags where tag_id = #{tag}")
    int countArticleNumByTag(@Param("tag") int tag);

    int countArticle(@Param("draft") Integer draft, @Param("categoryId") Integer categoryId,
                     @Param("publishDate") String publishDate, @Param("userId") Integer userId, @Param("articleTitle") String articleTitle);

    int countTimeRangeToArticle(@Param("draft") Integer draft, @Param("categoryId") Integer categoryId, @Param("publishDate") String publishDate,
                                @Param("userId") Integer userId, @Param("articleTitle") String articleTitle, @Param("timeRange") String timeRange);

    @Select("select COUNT(id) from article_likes_record where article_id = #{articleId} and is_read = 1")
    int countArticleLikesNotRead(@Param("articleId") int articleId);

    @Select("select id from comment_record where article_id = #{articleId} and p_id = 0")
    List<Integer> deleteCommentByArticleId(@Param("articleId") int articleId);

    @Select("select COUNT(id) from comment_record where article_id = #{articleId} and p_id = 0 and is_read = 1")
    int countCommentPIdByArticleId(@Param("articleId") int articleId);

    @Select("select COUNT(id) from article where user_id = #{userId} and draft = 1")
    int countArticleByUserId(@Param("userId") int userId);

    List<Article> findAllToDraftArticlesByTimes(@Param("draft") Integer draft, @Param("categoryId") Integer categoryId,
                                                @Param("articleType") String articleType, @Param("userId") Integer userId,
                                                @Param("articleTitle") String articleTitle, @Param("firstDate") String firstDate,
                                                @Param("lastDate") String lastDate, @Param("pageNum") int pageNum, @Param("rows") int rows);

    int countArticleByTimes(@Param("draft") Integer draft, @Param("categoryId") Integer categoryId,
                            @Param("articleType") String articleType, @Param("userId") Integer userId,
                            @Param("articleTitle") String articleTitle, @Param("firstDate") String firstDate,
                            @Param("lastDate") String lastDate);
}
