package com.xinhao.community.controller;

import com.xinhao.community.annotation.LoginRequired;
import com.xinhao.community.entity.Comment;
import com.xinhao.community.entity.DiscussPost;
import com.xinhao.community.entity.Event;
import com.xinhao.community.entity.User;
import com.xinhao.community.event.EventProducer;
import com.xinhao.community.service.CommentService;
import com.xinhao.community.service.DiscussPostService;
import com.xinhao.community.service.UserService;
import com.xinhao.community.util.CommunityConstant;
import com.xinhao.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @Xinhao
 * @Date 2022/2/1
 * @Descrption
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    DiscussPostService discussPostService;

    @LoginRequired
    @RequestMapping(path = "/add/{postId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("postId") int postId, Comment comment){
        User user = hostHolder.getUser();
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        comment.setUserId(user.getId());
        commentService.addComment(comment);

        //向被评论的用户发送通知
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(user.getId())
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityId())
                .setData("postId", postId);
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        //触发发帖事件
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            event = new Event().setTopic(TOPIC_PUBLISH)
                    .setUserId(user.getId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(comment.getEntityId());
            eventProducer.fireEvent(event);
        }

        return "redirect:/discuss/detail/"+postId;
    }
}
