package com.xinhao.community.controller;

import com.xinhao.community.entity.User;
import com.xinhao.community.service.UserService;
import com.xinhao.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Xinhao
 * @Date 2022/1/23
 * @Descrption
 */
@Controller
public class RegisterController implements CommunityConstant {

    @Autowired
    UserService userService;

    @RequestMapping(path = "register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> errorMessage = userService.register(user);
        if(errorMessage.isEmpty()){
            model.addAttribute("msg","确认邮件已经发送，请尽快激活!");
            model.addAttribute("target","/login");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",errorMessage.get("usernameMsg"));
            model.addAttribute("passwordMsg",errorMessage.get("passwordMsg"));
            model.addAttribute("emailMsg",errorMessage.get("emailMsg"));
            return "/site/register";
        }

    }

    @RequestMapping(path = "activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg","您的账号已经激活成功,可以正常使用了!");
            model.addAttribute("target","/login");
        }else if(result==ACTIVATION_REPEAT){
            model.addAttribute("msg","您的账户已经激活过了");
            model.addAttribute("target","/index");
        }else if(result==ACTIVATION_FAILURE){
            model.addAttribute("msg","激活码错误，激活失败");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

}
