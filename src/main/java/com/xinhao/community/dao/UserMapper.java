package com.xinhao.community.dao;

import com.xinhao.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
}
