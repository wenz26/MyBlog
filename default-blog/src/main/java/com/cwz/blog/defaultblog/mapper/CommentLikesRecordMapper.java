package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.CommentLikesRecord;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentLikesRecordMapper extends BeanMapper<CommentLikesRecord> {

    List<CommentLikesRecord> findCommentLikesRecordByUserId(@Param("userId") int userId);

    @Select("select COUNT(clr.id) from comment_record cr right join comment_likes_record clr on cr.id = clr.p_id where cr.answerer_id = #{userId} and clr.is_read = 1")
    int countCommentLikesRecordToNotReadByUserId(@Param("userId") int userId);

    @Update("update comment_likes_record set is_read = 0 where id = #{id}")
    int readThisThumbsUp(@Param("id") int id);

    @Update("UPDATE comment_likes_record SET is_read = 0 WHERE p_id IN " +
            "(SELECT result.id FROM(SELECT DISTINCT cr.id FROM comment_record cr RIGHT JOIN comment_likes_record clr " +
            "ON cr.id = clr.p_id WHERE cr.answerer_id = #{userId} AND clr.is_read = 1) result)")
    int readAllCommentLikesRecordNotReadByUserId(@Param("userId") int userId);

    @Delete("delete from comment_likes_record where p_id = #{pId}")
    int deleteCommentLikesRecordBypId(@Param("pId") int pId);

}
