package com.xinhao.community.service;

import java.util.List;
import java.util.Map;

/**
 * @Xinhao
 * @Date 2022/2/6
 * @Descrption
 */
public interface FollowService {

    public void follow(int userId, int entityType, int entityId);
    public void unfollow(int userId, int entityType, int entityId);
    public long findFollowerCount(int entityType, int entityId);
    public long findFolloweeCount(int userId, int entityType);
    public boolean hasFollowed(int userId, int entityType, int entityId);
    public List<Map<String, Object>> findFollowerList(int entityType, int entityId, int offset, int limit);
    public List<Map<String, Object>> findFollweeList(int userId, int entityType, int offset, int limit);
}
