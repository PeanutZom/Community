package com.xinhao.community.service;

import com.xinhao.community.entity.User;

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
}
