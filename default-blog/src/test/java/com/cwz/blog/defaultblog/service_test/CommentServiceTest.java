package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.entity.Comment;
import com.cwz.blog.defaultblog.service.CommentService;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Test
    void insertComment() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Comment comment = new Comment();
        /*comment.setArticleId(1);
        comment.setAnswererId(1);
        comment.setpId(0);
        comment.setRespondentId(1);
        comment.setCommentDate(localDateTime);
        comment.setLikes(1);
        comment.setCommentContent("这个逼咋这么帅");*/
        comment.setArticleId(2);
        comment.setAnswererId(2);
        comment.setpId(2);
        comment.setRespondentId(1);
        comment.setCommentDate(localDateTime);
        comment.setLikes(0);
        comment.setCommentContent("这个逼咋这么帅");
        commentService.insertComment(comment);
    }

    @Test
    void findCommentByArticleId() {
        DataMap comment = commentService.findCommentByArticleId(1, "陈文振");
        System.out.println(JSON.toJSONString(comment));

    }

    @Test
    void replyReplyReturn() {
    }

    @Test
    void updateLikeByArticleIdAndId() {
        DataMap dataMap = commentService.updateLikeByArticleIdAndId(1, 1);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void findFiveNewComment() {
        DataMap dataMap =commentService.findFiveNewComment(5, 0);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void getUserComment() {
        DataMap dataMap = commentService.getUserComment(5, 0, "陈文振");
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void commentNum() {
        int i = commentService.commentNum();
        System.out.println(i);
    }

    @Test
    void deleteCommentByArticleId() {
        int i = commentService.deleteCommentByArticleId(2);
        System.out.println(i);
    }

    @Test
    void readOneCommentRecord() {
        DataMap dataMap = commentService.readOneCommentRecord(6);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void readAllComment() {
        DataMap dataMap = commentService.readAllComment("陈文振");
        System.out.println(JSON.toJSONString(dataMap));
    }
}
