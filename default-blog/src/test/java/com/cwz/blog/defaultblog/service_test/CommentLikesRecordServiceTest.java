package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.entity.CommentLikesRecord;
import com.cwz.blog.defaultblog.mapper.CommentLikesRecordMapper;
import com.cwz.blog.defaultblog.service.CommentLikesRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentLikesRecordServiceTest {

    @Autowired
    private CommentLikesRecordService commentLikesRecordService;
    @Autowired
    private CommentLikesRecordMapper commentLikesRecordMapper;

    @Test
    void isLike() {
        boolean like = commentLikesRecordService.isLike(2, 1, "陈文振");
        System.out.println(like);
    }

    @Test
    void insertCommentLikesRecord() {
        LocalDateTime  localDateTime = LocalDateTime.now();
        CommentLikesRecord commentLikesRecord = new CommentLikesRecord();
        commentLikesRecord.setArticleId(1);
        commentLikesRecord.setpId(3);
        commentLikesRecord.setUserId(2);
        commentLikesRecord.setLikeDate(localDateTime);

        commentLikesRecordService.insertCommentLikesRecord(commentLikesRecord);
    }

    @Test
    void deleteCommentLikesRecordByArticleId() {
        commentLikesRecordService.deleteCommentLikesRecordByArticleId(2);
    }

    @Test
    void testAAA() {
//        List<CommentLikesRecord> result = commentLikesRecordMapper.findCommentLikesRecordByUserId(1);
//        System.out.println(JSON.toJSONString(result));
    }
}
