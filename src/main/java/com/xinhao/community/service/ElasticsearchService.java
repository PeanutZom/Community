package com.xinhao.community.service;

import com.xinhao.community.dao.elasticsearch.DiscussPostRepository;
import com.xinhao.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

/**
 * @Xinhao
 * @Date 2022/2/17
 * @Descrption
 */
public interface ElasticsearchService {
    void saveDiscussPost(DiscussPost discussPost);
    void deleteDiscussPost(int id);
    Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit);
}
