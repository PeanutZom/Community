package com.xinhao.community.controller;

import com.xinhao.community.annotation.LoginRequired;
import com.xinhao.community.entity.Comment;
import com.xinhao.community.entity.DiscussPost;
import com.xinhao.community.entity.PageInfo;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @Xinhao
 * @Date 2022/1/27
 * @Descrption
 */
@RequestMapping("/discuss")
@Controller
public class DiscussPostController implements CommunityConstant {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @LoginRequired
    @ResponseBody
    @RequestMapping(path = "/create",method = RequestMethod.POST)
    public String createDiscussPost(DiscussPost post){
        User user = hostHolder.getUser();
        post.setCreateTime(new Date());
        post.setUserId(user.getId());
        discussPostService.addDiscussPost(post);
        //报错将来统一处理
        return CommunityUtil.getJsonString(0,"发布成功");
    }

    @RequestMapping(path = "/detail/{id}", method = RequestMethod.GET)
    public String getDiscussPostDetail(@PathVariable(name = "id") int id, Model model, PageInfo page){
        User loginUser = hostHolder.getUser();
        DiscussPost post = discussPostService.findDiscussPostById(id);
        if(post == null){
            //若帖子不存在，则返回首页
            return "/index";
        }
        User poster = userService.getUserById(post.getUserId());
        int likeStatus = loginUser == null? 0:likeService.findEntityLikeStatus(loginUser.getId(),1, id);
        long likeCount = likeService.findEntityLikeCount(1, id);
        model.addAttribute("likeStatus",likeStatus);
        model.addAttribute("likeCount",likeCount);
        //评论分页信息
        page.setRows(commentService.findCommentCountByEntity(ENTITY_TYPE_POST, id));
        page.setPath("/discuss/detail/"+id);
        page.setLimit(5);

        //处理帖子的评论
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST,post.getId(),page.getOffset(),page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for(Comment comment : commentList){
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment",comment);
                User commentUser = userService.getUserById(comment.getUserId());
                commentVo.put("user",commentUser);
                likeStatus = loginUser == null? 0:likeService.findEntityLikeStatus(loginUser.getId(),2, comment.getId());
                likeCount = likeService.findEntityLikeCount(2, comment.getId());
                commentVo.put("likeStatus", likeStatus);
                commentVo.put("likeCount", likeCount);
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();

                if(replyList != null){
                    for (Comment reply : replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply",reply);
                        User replyUser = userService.getUserById(reply.getUserId());
                        replyVo.put("user",replyUser);
                        User target = reply.getTargetId() == 0? null : userService.getUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        likeStatus = loginUser == null?0:likeService.findEntityLikeStatus(loginUser.getId(),2, reply.getId());
                        likeCount = likeService.findEntityLikeCount(2, reply.getId());
                        replyVo.put("likeStatus", likeStatus);
                        replyVo.put("likeCount", likeCount);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replies", replyVoList);

                int replyNum = commentService.findCommentCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyNum);
                commentVoList.add(commentVo);
            }
        }


        model.addAttribute("post",post);
        model.addAttribute("poster",poster);
        model.addAttribute("comments",commentVoList);

        return "/site/discuss-detail";
    }

}
