package com.xinhao.community.dao;

import com.xinhao.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Xinhao
 * @Date 2022/1/18
 * @Descrption
 */
@Component
@Mapper
public interface DiscussPostMapper {
    public List<DiscussPost> selectDiscussPost(int userId, int offset, int limit);
    public int selectDiscussPostRows(@Param("userId") int userId);
}
