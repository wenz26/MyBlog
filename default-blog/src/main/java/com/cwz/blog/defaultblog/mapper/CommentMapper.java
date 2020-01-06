package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.Comment;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
}
