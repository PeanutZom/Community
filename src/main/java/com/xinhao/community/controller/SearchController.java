package com.xinhao.community.controller;

import com.xinhao.community.entity.DiscussPost;
import com.xinhao.community.entity.PageInfo;
import com.xinhao.community.service.ElasticsearchService;
import com.xinhao.community.service.LikeService;
import com.xinhao.community.service.UserService;
import com.xinhao.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Xinhao
 * @Date 2022/2/17
 * @Descrption
 */
@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    ElasticsearchService elasticsearchService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String getSearchPage(String keyword, Model model, PageInfo pageInfo){

        Page<DiscussPost> result =
                elasticsearchService.searchDiscussPost(keyword, pageInfo.getCurrent() - 1, pageInfo.getLimit());

        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(result != null){
            for(DiscussPost discussPost : result){
                Map<String, Object> discussPostVo = new HashMap<>();
                discussPostVo.put("user", userService.getUserById(discussPost.getUserId()));
                discussPostVo.put("post", discussPost);
                discussPostVo.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId()));
                discussPosts.add(discussPostVo);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        pageInfo.setRows(result == null ? 0:(int) result.getTotalElements());
        pageInfo.setPath("/search?keyword=" + keyword);
        return "site/search";
    }
}
