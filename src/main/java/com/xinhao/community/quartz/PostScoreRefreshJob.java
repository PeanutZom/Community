package com.xinhao.community.quartz;

import com.xinhao.community.entity.DiscussPost;
import com.xinhao.community.service.DiscussPostService;
import com.xinhao.community.service.ElasticsearchService;
import com.xinhao.community.service.LikeService;
import com.xinhao.community.util.CommunityConstant;
import com.xinhao.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Xinhao
 * @Date 2022/2/22
 * @Descrption
 */

public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    LikeService likeService;

    @Autowired
    ElasticsearchService elasticsearchService;

    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败!", e);
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        BoundSetOperations operations = redisTemplate.boundSetOps(RedisKeyUtil.getPostToFreshKey());
        if(operations.size() == 0){
            logger.info("任务取消，没有需要刷新的帖子");
            return;
        }
        logger.info("任务开始， 正在刷新帖子分数:"+operations.size());
        while (operations.size() > 0){
            refresh((Integer) operations.pop());
        }
        logger.info("任务结束。");
    }

    public void refresh(int postId){
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null){
            logger.error("帖子不存在");
            return;
        }

        // 是否精华
        boolean wonderful = post.getStatus() == 1;
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // 计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数 = 帖子权重 + 距离天数
        double score = Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        // 更新帖子分数
        discussPostService.updateScore(postId, score);
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    };
}
