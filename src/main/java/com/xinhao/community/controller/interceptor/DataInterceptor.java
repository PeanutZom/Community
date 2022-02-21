package com.xinhao.community.controller.interceptor;

import com.xinhao.community.entity.User;
import com.xinhao.community.service.DataService;
import com.xinhao.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Xinhao
 * @Date 2022/2/21
 * @Descrption
 */
@Component
public class DataInterceptor implements HandlerInterceptor {
    @Autowired
    DataService dataService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计uv
        String ip =  request.getRemoteHost();
        dataService.recordUV(ip);

        //统计dau
        User user = hostHolder.getUser();
        if(user != null){
            dataService.recordDAU(user.getId());
        }

        return true;
    }

}
