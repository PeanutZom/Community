package com.xinhao.community.service.impl;

import com.xinhao.community.dao.UserMapper;
import com.xinhao.community.entity.User;
import com.xinhao.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Xinhao
 * @Date 2022/1/20
 * @Descrption
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public User getUserById(int id) {
        return userMapper.selectUserById(id);
    }
}
