package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.Comment;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper extends BeanMapper<Comment> {

    @Select("update comment_record set likes = likes + 1 where article_id = #{articleId} and id = #{id}")
    void updateLikeByArticleIdAndId(@Param("articleId") int articleId, @Param("id") int id);

    @Select("select IFNULL(max(likes), 0) from comment_record where article_id = #{articleId} and id = #{id}")
    int findLikesByArticleIdAndId(@Param("articleId") int articleId, @Param("id") int id);

    @Update("update comment_record set is_read = 0 where id = #{id}")
    void readCommentRecordById(@Param("id") int id);

    @Update("update comment_record set is_read = 0 where respondent_id = #{respondentId}")
    void readCommentRecordByRespondentId(@Param("respondentId") int respondentId);

    @Select("select * from comment_record ORDER BY id desc")
    List<Comment> findAllComment();

    @Select("select * from comment_record where id = #{id}")
    Comment findOneCommentById(@Param("id") int id);

    @Delete("delete from comment_record where id = #{id}")
    int deleteOneCommentById(@Param("id") int id);

    @Delete("delete from comment_record where p_id = #{pId}")
    int deleteOneCommentBypId(@Param("pId") int pId);

    @Select("select answerer_id from comment_record where id = #{pId}")
    int findUserIdByPId(@Param("pId") int pId);

    @Select("select * from comment_record where article_id = #{articleId} and p_id = 0 order by id desc")
    List<Comment> findAllCommentByArticle(@Param("articleId") int articleId);

    @Select("select * from comment_record where article_id = #{articleId} and p_id = #{pId}")
    List<Comment> findAllCommentReplyByArticleAndPId(@Param("articleId") int articleId, @Param("pId") int pId);

    @Select("select COUNT(id) from comment_record where article_id = #{articleId}")
    int countCommentByArticleId(@Param("articleId") int articleId);
}
