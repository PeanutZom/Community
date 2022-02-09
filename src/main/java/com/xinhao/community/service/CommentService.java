package com.xinhao.community.service;

import com.xinhao.community.entity.Comment;

import java.util.List;

/**
 * @Xinhao
 * @Date 2022/1/30
 * @Descrption
 */

public interface CommentService {
    List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit);
    int findCommentCountByEntity(int entityType, int entityId);
    int addComment(Comment comment);
    Comment findCommentById(int id);
}
