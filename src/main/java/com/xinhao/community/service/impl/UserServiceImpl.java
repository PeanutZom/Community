package com.xinhao.community.service.impl;

import com.xinhao.community.dao.UserMapper;
import com.xinhao.community.entity.User;
import com.xinhao.community.service.UserService;
import com.xinhao.community.util.CommunityConstant;
import com.xinhao.community.util.CommunityUtil;
import com.xinhao.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Xinhao
 * @Date 2022/1/20
 * @Descrption
 */
@Service
public class UserServiceImpl implements UserService, CommunityConstant {
    @Autowired
    UserMapper userMapper;

    @Autowired
    MailClient mailClient;

    @Autowired
    TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    String domain;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @Override
    public User getUserById(int id) {
        return userMapper.selectUserById(id);
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> errorMessage = new HashMap<>();

        //空值处理
        if(user == null){
            throw new IllegalArgumentException("参数User不能为null");
        }

        //用户名为空的处理
        if(StringUtils.isBlank(user.getUsername())){
            errorMessage.put("usernameMsg","用户名不能为空");
            return errorMessage;
        }
        //密码为空的处理
        if(StringUtils.isBlank(user.getPassword())){
            errorMessage.put("passwordMsg","密码不能为空");
            return errorMessage;
        }
        //邮箱为空的处理
        if(StringUtils.isBlank(user.getEmail())){
            errorMessage.put("emailMsg","邮箱不能为空");
            return errorMessage;
        }

        //用户信息重复的处理
        if(userMapper.selectUserByUsername(user.getUsername())!=null){
            errorMessage.put("usernameMsg","用户名已被注册");
            return errorMessage;
        }

        if(userMapper.selectUserByEmail(user.getEmail())!=null){
            errorMessage.put("emailMsg","邮箱已被注册");
            return errorMessage;
        }

        //对用户进行处理
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setCreateTime(new Date());
        user.setStatus(0);
        user.setType(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        //将用户插入并返回空的errorMessage
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        // http://localhost:8080/community/activation/101/code
        context.setVariable("link",domain+contextPath+"/activation/"+user.getId()+ "/"+ user.getActivationCode());
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail("激活邮件",content,user.getEmail());
        return errorMessage;
    }

    @Override
    public int activation(int userId, String code) {
        User user = userMapper.selectUserById(userId);
        if(user.getStatus()==0){
            if(code.equals(user.getActivationCode())){
                userMapper.updateStatus(userId,1);
                return ACTIVATION_SUCCESS;
            }else {
                return ACTIVATION_FAILURE;
            }

        }else{
            return ACTIVATION_REPEAT;
        }

    }


}
