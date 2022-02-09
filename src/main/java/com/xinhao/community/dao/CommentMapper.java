package com.xinhao.community.dao;

import com.xinhao.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Xinhao
 * @Date 2022/1/30
 * @Descrption
 */
@Mapper
public interface CommentMapper {
    public List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);
    public int selectCommentCountByEntity(int entityType, int entityId);
    public int insertComment(Comment comment);
    public Comment selectCommentById(int id);
}
