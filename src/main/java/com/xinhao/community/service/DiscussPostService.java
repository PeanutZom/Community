package com.xinhao.community.service;

import com.xinhao.community.entity.DiscussPost;

import java.util.List;

/**
 * @Xinhao
 * @Date 2022/1/20
 * @Descrption
 */
public interface DiscussPostService {
    public List<DiscussPost> findDiscussPost(int userId, int offset, int limit);
    public int findDiscussPostRows(int userId);
}
