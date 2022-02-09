package com.xinhao.community.controller;

import com.xinhao.community.entity.PageInfo;
import com.xinhao.community.entity.User;
import com.xinhao.community.service.FollowService;
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

import java.util.List;
import java.util.Map;

/**
 * @Xinhao
 * @Date 2022/2/6
 * @Descrption
 */
@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @ResponseBody
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);
        return CommunityUtil.getJsonString(0, "关注成功");
    }

    @ResponseBody
    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJsonString(0, "取关成功");
    }

    @RequestMapping(path = "/followee/{userId}", method = RequestMethod.GET)
    public String getFolloweePage(Model model, @PathVariable(value = "userId") int userId, PageInfo pageInfo){
        pageInfo.setPath("/followee/"+userId);
        pageInfo.setLimit(5);
        pageInfo.setRows((int)followService.findFolloweeCount(userId, ENTITY_TYPE_USER));
        List<Map<String, Object>> followeeList = followService.findFollweeList(userId, ENTITY_TYPE_USER, pageInfo.getOffset(), pageInfo.getLimit());
        User loginUser = hostHolder.getUser();
        if (followeeList != null){
            for (Map<String, Object> map : followeeList){
                User followee = userService.getUserById((Integer) map.get("followeeId"));
                map.put("followee", followee);
                boolean hasFollowed = followService.hasFollowed(loginUser.getId(), ENTITY_TYPE_USER, followee.getId());
                map.put("hasFollowed", hasFollowed);
            }
        }
        model.addAttribute("followeeList", followeeList);
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "/site/followee";
    }

    @RequestMapping(path = "/follower/{userId}", method = RequestMethod.GET)
    public String getFollowerPage(Model model, @PathVariable(value = "userId") int userId, PageInfo pageInfo){
        pageInfo.setPath("/follower/"+userId);
        pageInfo.setLimit(5);
        pageInfo.setRows((int)followService.findFollowerCount(ENTITY_TYPE_USER, userId));
        List<Map<String, Object>> followerList = followService.findFollowerList(ENTITY_TYPE_USER, userId, pageInfo.getOffset(), pageInfo.getLimit());
        User loginUser = hostHolder.getUser();
        if (followerList != null){
            for (Map<String, Object> map : followerList){
                User follower = userService.getUserById((Integer) map.get("followerId"));
                map.put("follower", follower);
                boolean hasFollowed = followService.hasFollowed(loginUser.getId(), ENTITY_TYPE_USER, follower.getId());
                map.put("hasFollowed", hasFollowed);
            }
        }
        model.addAttribute("followerList", followerList);
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "/site/follower";
    }



}
