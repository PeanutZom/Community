package com.xinhao.community.dao.elasticsearch;

import com.xinhao.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Xinhao
 * @Date 2022/2/17
 * @Descrption
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
