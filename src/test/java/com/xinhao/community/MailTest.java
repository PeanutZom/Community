package com.xinhao.community;

import com.xinhao.community.util.MailClient;
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
public class MailTest {

    @Autowired
    MailClient client;
    @Autowired
    TemplateEngine engine;

    @Test
    public void testSendMail() throws MessagingException {
        client.sendMail("test","how r u","zhangxinhao@sjtu.edu.cn");
    }
    @Test
    public void testSendHtml() throws MessagingException {
        Context context = new Context();
        context.setVariable("username", "xinhao");
        String content = engine.process("/mail/demo", context);
        client.sendMail("test2", content, "zhangxinhao@sjtu.edu.cn");
    }
}
