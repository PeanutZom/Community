package com.xinhao.community.controller;

import com.xinhao.community.annotation.LoginRequired;
import com.xinhao.community.entity.Comment;
import com.xinhao.community.entity.DiscussPost;
import com.xinhao.community.entity.User;
import com.xinhao.community.service.CommentService;
import com.xinhao.community.service.DiscussPostService;
import com.xinhao.community.service.LikeService;
import com.xinhao.community.service.UserService;
import com.xinhao.community.util.CommunityConstant;
import com.xinhao.community.util.CommunityUtil;
import com.xinhao.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Xinhao
 * @Date 2022/2/5
 * @Descrption
 */
@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    CommentService commentService;



    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId){
        User loginUser = hostHolder.getUser();

        if(loginUser == null){
            return CommunityUtil.getJsonString(1,"请登录",null);
        }
        int entityUserId = 0;
        if(entityType == ENTITY_TYPE_POST){
            DiscussPost discussPost = discussPostService.findDiscussPostById(entityId);
            entityUserId = discussPost.getUserId();
        }else {
            Comment comment = commentService.findCommentById(entityId);
            entityUserId = comment.getUserId();
        }
        likeService.like(entityType,entityId,loginUser.getId(),entityUserId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount",likeService.findEntityLikeCount(entityType, entityId));
        map.put("likeStatus", likeService.findEntityLikeStatus(loginUser.getId(), entityType, entityId));
        return CommunityUtil.getJsonString(0, null, map);
    }

    @ResponseBody
    @RequestMapping(path = "/like/user", method = RequestMethod.POST)
    public String userLikeCount(){
        User loginUser = hostHolder.getUser();
        int count = likeService.findUserLikeCount(loginUser.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("count",count);
        return CommunityUtil.getJsonString(0, null, map);
    }
}
