package com.xinhao.community.service.impl;

import com.xinhao.community.dao.CommentMapper;
import com.xinhao.community.dao.DiscussPostMapper;
import com.xinhao.community.entity.Comment;
import com.xinhao.community.entity.DiscussPost;
import com.xinhao.community.service.CommentService;
import com.xinhao.community.util.CommunityConstant;
import com.xinhao.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @Xinhao
 * @Date 2022/1/30
 * @Descrption
 */
@Service
public class CommentServiceImpl implements CommentService, CommunityConstant {

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Override
    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    @Override
    public int findCommentCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCommentCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int addComment(Comment comment) {
        if(comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int updatedRows = commentMapper.insertComment(comment);
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            int postId = comment.getEntityId();
            DiscussPost entity = discussPostMapper.selectDiscussPostById(postId);
            discussPostMapper.updateDiscussPostCommentCount(postId, entity.getCommentCount() + 1);
        }
        return updatedRows;
    }

    @Override
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }
}
