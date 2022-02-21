package com.xinhao.community.service;

import com.xinhao.community.entity.LoginTicket;
import com.xinhao.community.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Xinhao
 * @Date 2022/1/20
 * @Descrption
 */
public interface UserService {
    User getUserById(int id);

    Map<String, Object> register(User user);

    int activation(int userId, String code);

    Map<String, Object> login(User user, Date expired);

    void logout(String ticket);

    LoginTicket selectLoginTicket(String ticket);

    int updateUserHeaderURL(int userId, String headerUrl);

    int updatePassword(int userId, String password);

    User getUserByUsername(String username);

    public Collection<? extends GrantedAuthority> getAuthorities(int userId);

}
