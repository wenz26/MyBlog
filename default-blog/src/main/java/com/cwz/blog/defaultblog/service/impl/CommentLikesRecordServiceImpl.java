package com.cwz.blog.defaultblog.service.impl;

import com.cwz.blog.defaultblog.entity.CommentLikesRecord;
import com.cwz.blog.defaultblog.mapper.CommentLikesRecordMapper;
import com.cwz.blog.defaultblog.service.CommentLikesRecordService;
import com.cwz.blog.defaultblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author: 陈文振
 * @date: 2019/12/19
 * @description: 评论点赞记录业务操作实现类
 */
@Service("commentLikesRecordService")
public class CommentLikesRecordServiceImpl implements CommentLikesRecordService {

    @Autowired
    private CommentLikesRecordMapper commentLikesRecordMapper;
    @Autowired
    private UserService userService;

    @Override
    public boolean isLike(int articleId, int pId, String username) {
        Example example = new Example(CommentLikesRecord.class);
        example.createCriteria().andEqualTo("articleId", articleId)
                .andEqualTo("pId", pId)
                .andEqualTo("userId", userService.findIdByUsername(username));
        return commentLikesRecordMapper.selectOneByExample(example) != null;
    }

    @Override
    public void insertCommentLikesRecord(CommentLikesRecord commentLikesRecord) {
        commentLikesRecordMapper.insert(commentLikesRecord);
    }

    @Override
    public void deleteCommentLikesRecordByArticleId(int articleId) {
        Example example = new Example(CommentLikesRecord.class);
        example.createCriteria().andEqualTo("articleId", articleId);
        commentLikesRecordMapper.deleteByExample(example);
    }
}
