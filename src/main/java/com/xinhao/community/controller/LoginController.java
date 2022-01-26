package com.xinhao.community.controller;

import com.google.code.kaptcha.Producer;
import com.xinhao.community.entity.User;
import com.xinhao.community.service.UserService;
import com.xinhao.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.resource.HttpResource;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

/**
 * @Xinhao
 * @Date 2022/1/23
 * @Descrption
 */
@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    Producer producer;

    @Autowired
    UserService userService;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @RequestMapping(path = "/login")
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        String kaptcha = producer.createText();
        session.setAttribute("kaptcha",kaptcha);
        BufferedImage image = producer.createImage(kaptcha);
        response.setContentType("image/png");
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(Model model, User user, String kaptcha, Boolean rememberMe,
                        HttpSession session, HttpServletResponse response){
        if(StringUtils.isBlank(kaptcha) ||!kaptcha.equals(session.getAttribute("kaptcha"))){
            model.addAttribute("kaptchaMsg","验证码不正确");
            return "/site/login";
        }
        int expiredSeconds = rememberMe?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Date expired = new Date(System.currentTimeMillis() + expiredSeconds);
        Map<String, Object> message = userService.login(user,expired);
        if(message.containsKey("ticket")){
            model.addAttribute("msg","登陆成功！");
            model.addAttribute("target","/index");
            Cookie loginCookie = new Cookie("ticket",(String) message.get("ticket"));
            loginCookie.setMaxAge(expiredSeconds);
            loginCookie.setPath(contextPath);
            response.addCookie(loginCookie);
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg",message.get("usernameMsg"));
            model.addAttribute("passwordMsg",message.get("passwordMsg"));
            return "/site/login";
        }
    }
    @RequestMapping(path = "/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }


}
