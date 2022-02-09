package com.xinhao.community.service;

/**
 * @Xinhao
 * @Date 2022/2/5
 * @Descrption
 */
public interface LikeService {
    public void like(int entityType, int entityId, int userId, int entityUserId);
    public long findEntityLikeCount(int entityType, int entityId);
    public int findEntityLikeStatus(int userId, int entityType, int entityId);
    public int findUserLikeCount(int userId);
}
