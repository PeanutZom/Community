package com.xinhao.community.controller;

import com.xinhao.community.entity.DiscussPost;
import com.xinhao.community.entity.PageInfo;
import com.xinhao.community.entity.User;
import com.xinhao.community.service.DiscussPostService;
import com.xinhao.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Xinhao
 * @Date 2022/1/19
 * @Descrption
 */
@Controller()
public class HomeController {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    UserService userService;

    @RequestMapping("/index")
    public String getIndexPage(Model model, PageInfo pageInfo){
        pageInfo.setRows(discussPostService.findDiscussPostRows(0));
        pageInfo.setPath("/index");
        List<DiscussPost> list = discussPostService.findDiscussPost(0, pageInfo.getOffset(), pageInfo.getLimit());

        List<Map<String, Object>> postsWithUser = new ArrayList<>();
        for(DiscussPost post : list){
            Map<String, Object> postWithUser = new HashMap<>();
            User user = userService.getUserById(post.getUserId());
            postWithUser.put("post", post);
            postWithUser.put("user", user);
            postsWithUser.add(postWithUser);
        }
        model.addAttribute("postsWithUser",postsWithUser);
        return "/index";
    }
}
