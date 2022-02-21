package com.xinhao.community.config;

import com.xinhao.community.controller.interceptor.DataInterceptor;
import com.xinhao.community.controller.interceptor.LoginCheckInterceptor;
import com.xinhao.community.controller.interceptor.LoginTicketInterceptor;
import com.xinhao.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Xinhao
 * @Date 2022/1/26
 * @Descrption
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    LoginCheckInterceptor loginCheckInterceptor;

    @Autowired
    MessageInterceptor messageInterceptor;

    @Autowired
    DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*.png");
//        registry.addInterceptor(loginCheckInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*.png");
        registry.addInterceptor(messageInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*.png");
        registry.addInterceptor(dataInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*.png");

    }
}
