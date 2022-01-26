package com.xinhao.community;

import com.xinhao.community.dao.LoginTicketMapper;
import com.xinhao.community.entity.LoginTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;


/**
 * @Xinhao
 * @Date 2022/1/19
 * @Descrption
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class LoginTicketTest {
    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Test
    public void testInsert(){
        LoginTicket ticket = new LoginTicket();
        ticket.setTicket("abc");
        loginTicketMapper.insertTicket(ticket);
    }

    @Test
    public void testSelect(){
        LoginTicket ticket = loginTicketMapper.selectTicket("abc");
        System.out.println(ticket);
    }


}
