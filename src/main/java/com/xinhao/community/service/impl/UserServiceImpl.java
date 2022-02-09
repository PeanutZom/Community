package com.xinhao.community.service.impl;

import com.xinhao.community.dao.LoginTicketMapper;
import com.xinhao.community.dao.UserMapper;
import com.xinhao.community.entity.LoginTicket;
import com.xinhao.community.entity.User;
import com.xinhao.community.service.UserService;
import com.xinhao.community.util.CommunityConstant;
import com.xinhao.community.util.CommunityUtil;
import com.xinhao.community.util.MailClient;
import com.xinhao.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public User getUserById(int id) {
//        return userMapper.selectUserById(id);
        User user = getUserFromCache(id);
        if(user == null){
            user = initUserToCache(id);
        }
        return user;
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
                clearCache(userId);
                return ACTIVATION_SUCCESS;
            }else {
                return ACTIVATION_FAILURE;
            }

        }else{
            return ACTIVATION_REPEAT;
        }

    }

    @Override
    public Map<String, Object> login(User user, Date expired) {
        Map<String, Object> message = new HashMap<>();
        User userInDB = userMapper.selectUserByUsername(user.getUsername());

        if(StringUtils.isBlank(user.getUsername())){
            message.put("usernameMsg","用户名不能为空");
            return message;
        }
        if(StringUtils.isBlank(user.getPassword())){
            message.put("passwordMsg","密码不能为空");
        }

        if(userInDB == null){
            message.put("usernameMsg","用户名不存在");
            return message;
        }
        if(!userInDB.getPassword().equals(CommunityUtil.md5(user.getPassword() + userInDB.getSalt()))){
            message.put("passwordMsg","密码不正确");
            return message;
        }
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userInDB.getId());
        ticket.setStatus(0);
        ticket.setExpired(expired);
        ticket.setTicket(CommunityUtil.generateUUID());

//        loginTicketMapper.insertTicket(ticket);
        //在redis数据库中存入ticket
        String ticketKey = RedisKeyUtil.getTicketKey(ticket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, ticket);

        message.put("ticket",ticket.getTicket());
        return message;
    }

    @Override
    public void logout(String ticket) {
//        loginTicketMapper.updateTicketStatus(ticket, 1);
        //在修改redis库中的ticket
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
        return;
    }

    @Override
    public LoginTicket selectLoginTicket(String ticket) {
        //return loginTicketMapper.selectTicket(ticket);
        String ticketkey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketkey);
    }

    @Override
    public int updateUserHeaderURL(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }

    @Override
    public int updatePassword(int userId, String password) {
        User user = userMapper.selectUserById(userId);
        String salt = user.getSalt();
        String newPassword = CommunityUtil.md5(password + salt);
        int rows = userMapper.updatePassword(userId, newPassword);
        clearCache(userId);
        return rows;
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectUserByUsername(username);
    }

    public User getUserFromCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    public User initUserToCache(int userId){
        User user = userMapper.selectUserById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    public void clearCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

}
