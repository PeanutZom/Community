package com.xinhao.community;

import com.xinhao.community.util.MailClient;
import com.xinhao.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;

/**
 * @Xinhao
 * @Date 2022/1/19
 * @Descrption
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class FilterTest {
    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    public void test1(){
        String insult = "你是猪鼻吧？";
        String filterd = sensitiveFilter.filter(insult);
        System.out.println(filterd);
    }
}
