package com.xinhao.community.service.impl;

import com.xinhao.community.service.FollowService;
import com.xinhao.community.util.HostHolder;
import com.xinhao.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Xinhao
 * @Date 2022/2/6
 * @Descrption
 */
@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFollweeKey(userId, entityType);
                String follwerKey = RedisKeyUtil.getFollwerKey(entityId, entityType);
                redisTemplate.multi();
                redisTemplate.opsForZSet().add(followeeKey, entityId,System.currentTimeMillis());
                redisTemplate.opsForZSet().add(follwerKey, userId,System.currentTimeMillis());
                return redisTemplate.exec();
            }
        });
    }
    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String followeeKey = RedisKeyUtil.getFollweeKey(userId, entityType);
                String follwerKey = RedisKeyUtil.getFollwerKey(entityId, entityType);
                redisTemplate.multi();
                redisTemplate.opsForZSet().remove(followeeKey, entityId);
                redisTemplate.opsForZSet().remove(follwerKey, userId);
                return redisTemplate.exec();
            }
        });
    }
    @Override
    public long findFollowerCount(int entityType, int entityId) {
        String follwerKey = RedisKeyUtil.getFollwerKey(entityId, entityType);
        return redisTemplate.opsForZSet().zCard(follwerKey);
    }

    @Override
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFollweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    @Override
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFollweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    @Override
    public List<Map<String, Object>> findFollowerList(int entityType, int entityId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollwerKey(entityId, entityType);
        Set<Integer> followerIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if(followerIds == null){
            return null;
        }
        List<Map<String, Object>> followerList = new ArrayList<>();
        for (Integer followerId : followerIds){
            Map<String, Object> follower = new HashMap<>();
            follower.put("followerId", followerId);
            Double score = redisTemplate.opsForZSet().score(followerKey, followerId);
            follower.put("followTime", new Date(score.longValue()));
            followerList.add(follower);
        }
        return followerList;
    }

    @Override
    public List<Map<String, Object>> findFollweeList(int userId, int entityType, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFollweeKey(userId, entityType);
        Set<Integer> followeeIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if(followeeIds == null){
            return null;
        }
        List<Map<String, Object>> followeeList = new ArrayList<>();
        for(Integer followeeId : followeeIds){
            Map<String, Object> followee = new HashMap<>();
            followee.put("followeeId", followeeId);
            Double score = redisTemplate.opsForZSet().score(followeeKey, followeeId);
            followee.put("followTime",new Date(score.longValue()));
            followeeList.add(followee);
        }
        return followeeList;
    }
}
