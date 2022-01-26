package com.xinhao.community.util;

import com.xinhao.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @Xinhao
 * @Date 2022/1/26
 * @Descrption
 */
@Component
public class HostHolder{
    ThreadLocal<User> map = new ThreadLocal<>();
    public User getUser(){
        return map.get();
    }
    public void setUser(User user){
        map.set(user);
    }
    public void remove(){
        map.remove();
    }

}
