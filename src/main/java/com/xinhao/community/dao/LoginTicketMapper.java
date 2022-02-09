package com.xinhao.community.dao;

import com.xinhao.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

/**
 * @Xinhao
 * @Date 2022/1/24
 * @Descrption
 */
@Mapper
@Deprecated
public interface LoginTicketMapper {
    public LoginTicket selectTicket(String ticket);
    public int insertTicket(LoginTicket ticket);
    public int updateTicketStatus(String ticket, int status);
}
