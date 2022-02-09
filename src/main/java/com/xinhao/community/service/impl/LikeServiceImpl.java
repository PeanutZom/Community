package com.xinhao.community.service.impl;

import com.xinhao.community.service.LikeService;
import com.xinhao.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

/**
 * @Xinhao
 * @Date 2022/2/5
 * @Descrption
 */
@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void like(int entityType, int entityId, int userId, int entityUserId) {
//        SetOperations setOperations = redisTemplate.opsForSet();
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        if(setOperations.isMember(entityLikeKey, userId)){
//            setOperations.remove(entityLikeKey, userId);
//            redisTemplate.opsForValue().decrement(RedisKeyUtil.getUserLikeKey(entityUserId));
//        }else {
//            setOperations.add(entityLikeKey, userId);
//            redisTemplate.opsForValue().increment(RedisKeyUtil.getUserLikeKey(entityUserId));
//        }
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
                redisTemplate.multi();
                if(isMember){
                    redisTemplate.opsForSet().remove(entityLikeKey, userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                }else {
                    redisTemplate.opsForSet().add(entityLikeKey, userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }
                return redisTemplate.exec();
            }
        });
       
    }

    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        SetOperations setOperations = redisTemplate.opsForSet();
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return setOperations.size(entityLikeKey);
    }

    @Override
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        SetOperations setOperations = redisTemplate.opsForSet();
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        if(setOperations.isMember(entityLikeKey, userId)){
            return 1;
        }else {
            return 0;
        }

    }

    @Override
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer likeCountObj = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        int likeCount = likeCountObj == null? 0:likeCountObj;

        return likeCount;
    }
}
