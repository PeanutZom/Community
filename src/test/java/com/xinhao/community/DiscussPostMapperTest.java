package com.xinhao.community;

import com.xinhao.community.dao.DiscussPostMapper;
import com.xinhao.community.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Xinhao
 * @Date 2022/1/19
 * @Descrption
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class DiscussPostMapperTest {

    @Autowired
    DiscussPostMapper mapper;
    @Test
    public void testSelectDiscussPost(){
        List<DiscussPost> list = mapper.selectDiscussPost(0,0,10, 0);
        for (DiscussPost post : list){
            System.out.println(post);
        }
        System.out.println(mapper.selectDiscussPostRows(1));
    }
}
