package com.xinhao.community;

import com.xinhao.community.dao.DiscussPostMapper;
import com.xinhao.community.dao.UserMapper;
import com.xinhao.community.entity.DiscussPost;
import com.xinhao.community.entity.User;
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
public class UserTest {

    @Autowired
    UserMapper mapper;
    @Test
    public void testSelectUser(){
        //Test
        User user = mapper.selectUserById(12);
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("000000000");
        mapper.insertUser(user);
    }

}
