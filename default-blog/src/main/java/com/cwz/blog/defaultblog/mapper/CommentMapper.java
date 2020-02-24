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

    @Select("select * from comment_record where answerer_id = #{answererId} ORDER BY id desc")
    List<Comment> findAllCommentByAnswererId(@Param("answererId") int answererId);

    @Select("select COUNT(id) from comment_record")
    int countAllComment();

    @Select("select COUNT(id) from comment_record where answerer_id = #{answererId}")
    int countCommentByAnswererId(@Param("answererId") int answererId);

    @Select("select * from comment_record where id = #{id}")
    Comment findOneCommentById(@Param("id") int id);

    @Delete("delete from comment_record where id = #{id}")
    int deleteOneCommentById(@Param("id") int id);

    @Delete("delete from comment_record where p_id = #{pId}")
    int deleteOneCommentBypId(@Param("pId") int pId);

    @Select("select answerer_id from comment_record where id = #{pId}")
    int findUserIdByPId(@Param("pId") int pId);

    @Select("select answerer_id, comment_content from comment_record where id = #{pId}")
    Comment findCommentContentAndAnswererIdByPId(@Param("pId") int pId);

    @Select("select * from comment_record where article_id = #{articleId} and p_id = 0 order by id desc")
    List<Comment> findAllCommentByArticle(@Param("articleId") int articleId);

    @Select("select * from comment_record where article_id = #{articleId} and p_id = #{pId}")
    List<Comment> findAllCommentReplyByArticleAndPId(@Param("articleId") int articleId, @Param("pId") int pId);

    @Select("select COUNT(id) from comment_record where article_id = #{articleId}")
    int countCommentByArticleId(@Param("articleId") int articleId);

    @Select("select COUNT(id) from comment_record where p_id = #{id} and respondent_id = #{answererId} and is_read = 1")
    int countCommentByPId(@Param("id") int id, @Param("answererId") int answererId);

    @Select("select COUNT(id) from comment_likes_record where p_id = #{id} and user_id <> #{answererId} and is_read = 1")
    int countCommentLikesByPId(@Param("id") int id, @Param("answererId") int answererId);

    @Select("select DISTINCT respondent_id from comment_record where p_id = #{id} and respondent_id <> #{answererId}")
    List<Integer> findOtherCommentRespondentId(@Param("id") int id, @Param("answererId") int answererId);

    @Select("select * from comment_record where id = #{id}")
    Comment findCommentById(@Param("id") int id);

    List<Comment> findAllCommentBySome(@Param("articleTitle") String articleTitle, @Param("answererId") Integer answererId,
                                       @Param("commentContent") String commentContent, @Param("firstDate") String firstDate,
                                       @Param("lastDate") String lastDate, @Param("searchUsername") String searchUsername);

    int countAllCommentBySome(@Param("articleTitle") String articleTitle, @Param("answererId") Integer answererId,
                              @Param("commentContent") String commentContent, @Param("firstDate") String firstDate,
                              @Param("lastDate") String lastDate, @Param("searchUsername") String searchUsername);

    List<Comment> getUserComment(@Param("respondentId") Integer respondentId, @Param("answererId") Integer answererId,
                                 @Param("isRead") Integer isRead, @Param("firstDate") String firstDate,
                                 @Param("lastDate") String lastDate);

    @Select("select COUNT(id) from comment_record where respondent_id = #{respondentId} and answerer_id <> #{answererId} and is_read = 1")
    int countUserCommentNotRead(@Param("respondentId") Integer respondentId, @Param("answererId") Integer answererId);

}
