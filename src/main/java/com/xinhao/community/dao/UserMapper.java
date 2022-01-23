package com.xinhao.community.dao;

import com.xinhao.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @Xinhao
 * @Date 2022/1/20
 * @Descrption
 */
@Component
@Mapper
public interface UserMapper {
    User selectUserById(int id);
    User selectUserByEmail(String email);
    User selectUserByUsername(String username);
    int insertUser(User user);
    int updateStatus(int id, int status);
    int updatePassword(int id, String password);
    int updateHeader(int id, String headerUrl);
}
