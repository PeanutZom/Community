package com.xinhao.community.controller.interceptor;

import com.xinhao.community.entity.LoginTicket;
import com.xinhao.community.entity.User;
import com.xinhao.community.service.UserService;
import com.xinhao.community.util.CookieUtil;
import com.xinhao.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @Xinhao
 * @Date 2022/1/26
 * @Descrption
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie cookie = CookieUtil.getCookie(request, "ticket");
        if(cookie == null){
            return true;
        }
        LoginTicket loginTicket = userService.selectLoginTicket(cookie.getValue());
        //若ticket仍未登出且未过期，到数据库查询该用户，放入HostHolder,HostHolder由interceptor实例持有。
        if(loginTicket != null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date())){
            int userId = loginTicket.getUserId();
            User user = userService.getUserById(userId);
            hostHolder.setUser(user);

            //构建用户认证的结果，存入SecurityContext, 供SpringSecurity 进行授权
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user, user.getPassword(), userService.getAuthorities(userId));
            SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.remove();
        SecurityContextHolder.clearContext();
    }
}
