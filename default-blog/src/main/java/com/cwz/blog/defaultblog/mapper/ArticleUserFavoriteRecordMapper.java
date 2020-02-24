package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.ArticleUserFavoriteRecord;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleUserFavoriteRecordMapper extends BeanMapper<ArticleUserFavoriteRecord> {

    @Select("select * from article_user_favorite_record where user_id = #{userId} order by create_date desc")
    List<ArticleUserFavoriteRecord> findArticleFavoriteRecordByUserId(@Param("userId") int userId);

    @Select("select aufr.* from article_user_favorite_record aufr left join article a on aufr.article_id = a.id where aufr.user_id = #{userId} and a.article_title like '%${articleTitle}%' order by create_date desc")
    List<ArticleUserFavoriteRecord> findArticleFavoriteRecordByUserIdAndArticleTitle(@Param("userId") int userId, @Param("articleTitle") String articleTitle);

    @Delete("delete from article_user_favorite_record where id = #{id}")
    int deleteArticleFavoriteRecordById(@Param("id") int id);

    @Update("update article set favorites = favorites - 1 where id = #{articleId}")
    void updateArticleFavoriteRecord(@Param("articleId") int articleId);

    @Select("select article_id from article_user_favorite_record where id = #{id}")
    int selectArticleFavoriteRecordById(@Param("id") int id);

    @Select("select COUNT(id) from article_user_favorite_record where user_id = #{userId}")
    int countArticleFavoriteRecordByUserId(@Param("userId") int userId);

    @Select("select COUNT(aufr.id) from article_user_favorite_record aufr left join article a on aufr.article_id = a.id where aufr.user_id = #{userId} and a.article_title like '%${articleTitle}%'")
    int countArticleFavoriteRecordByUserIdAndArticleTitle(@Param("userId") int userId, @Param("articleTitle") String articleTitle);
}
