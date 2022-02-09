package com.xinhao.community;

import com.xinhao.community.dao.MessageMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Xinhao
 * @Date 2022/1/19
 * @Descrption
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class MessageTest {
    @Autowired
    MessageMapper messageMapper;

    @Test
    public void test1(){
        System.out.println(messageMapper.getMessageList("111_112",0,100));
        System.out.println(messageMapper.getMessageRowsCount("111_112"));
        System.out.println(messageMapper.getSessionList(111, 0, 100));
        System.out.println(messageMapper.getSessionRowsCount(111));
        System.out.println(messageMapper.getUnreadMessageCount(131,null));
    }

}
