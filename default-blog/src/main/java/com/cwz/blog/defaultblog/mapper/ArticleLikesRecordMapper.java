package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.ArticleLikesRecord;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArticleLikesRecordMapper extends BeanMapper<ArticleLikesRecord> {

    @Update("update article_likes_record set is_read = 0 where id = #{id}")
    int readThisThumbsUp(@Param("id") int id);

    @Select("select article_id from article_likes_record where id = #{id}")
    int findArticleIdById(@Param("id") int id);

    @Update("update article_likes_record set is_read = 0 where ")
    int readAllThumbsUp(@Param("userId") int userId);

    @Select("select alr.* from article a right join article_likes_record alr on a.id = alr.article_id where a.user_id = #{userId} order by alr.id desc")
    List<ArticleLikesRecord> findArticleLikesRecordByUserId(@Param("userId") int userId);

    @Select("select COUNT(alr.id) from article a right join article_likes_record alr on a.id = alr.article_id where a.user_id = #{userId} and alr.is_read = 1")
    int countArticleLikesRecordToNotReadByUserId(@Param("userId") int userId);

    @Select("select a.id from article a right join article_likes_record alr on a.id = alr.article_id where a.user_id = #{userId} and alr.is_read = 1")
    List<Integer> findArticleIdToNotReadByUserId(@Param("userId") int userId);

    @Update("UPDATE article_likes_record SET is_read = 0 WHERE article_id IN " +
            "(SELECT result.id FROM(SELECT DISTINCT a.id FROM article a RIGHT JOIN article_likes_record alr " +
            "ON a.id = alr.article_id WHERE a.user_id = #{userId} AND alr.is_read = 1) result)")
    int readAllArticleLikesRecordNotReadByUserId(@Param("userId") int userId);

}
