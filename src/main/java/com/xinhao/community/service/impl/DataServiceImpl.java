package com.xinhao.community.service.impl;

import com.xinhao.community.service.DataService;
import com.xinhao.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Xinhao
 * @Date 2022/2/21
 * @Descrption
 */
@Service
public class DataServiceImpl implements DataService {
    @Autowired
    RedisTemplate redisTemplate;



    @Override
    public void recordUV(String ip) {
        redisTemplate.opsForHyperLogLog().add(RedisKeyUtil.getUVKey(new Date()), ip);
    }

    @Override
    public long calculateUV(Date start, Date end) {
        if(start == null || end == null){
            throw new IllegalArgumentException("时间参数不能为空");
        }
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getUVKey(calendar.getTime());
            keyList.add(key);
            calendar.add(Calendar.DATE, 1);
        }
        String unionKey = RedisKeyUtil.getUVKey(start, end);
        redisTemplate.opsForHyperLogLog().union(unionKey, keyList.toArray());
        return redisTemplate.opsForHyperLogLog().size(unionKey);
    }

    @Override
    public void recordDAU(int userId) {
        redisTemplate.opsForValue().setBit(RedisKeyUtil.getDauKey(new Date()), userId, true);
    }

    @Override
    public long calculateDAU(Date start, Date end) {
        if(start == null || end == null){
            throw new IllegalArgumentException("时间参数不能为空");
        }
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getDauKey(calendar.getTime());
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE, 1);
        }
        String unionKey = RedisKeyUtil.getDauKey(start, end);
        return (long)redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR, unionKey.getBytes(), keyList.toArray(new byte[0][0]));
                return connection.bitCount(unionKey.getBytes());
            }
        });
    }
}
