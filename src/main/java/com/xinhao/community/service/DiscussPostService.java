package com.xinhao.community.service;

import com.xinhao.community.entity.DiscussPost;

import java.util.List;

/**
 * @Xinhao
 * @Date 2022/1/20
 * @Descrption
 */
public interface DiscussPostService {
    List<DiscussPost> findDiscussPost(int userId, int offset, int limit);
    int findDiscussPostRows(int userId);
    int addDiscussPost(DiscussPost discussPost);
    DiscussPost findDiscussPostById(int id);
}
