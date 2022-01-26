package com.xinhao.community.controller;

import com.xinhao.community.annotation.LoginRequired;
import com.xinhao.community.entity.User;
import com.xinhao.community.service.UserService;
import com.xinhao.community.util.CommunityUtil;
import com.xinhao.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Xinhao
 * @Date 2022/1/26
 * @Descrption
 */
@RequestMapping("/user")
@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;

    @Value("${community.path.uploadPath}")
    String uploadPath;

    @Value("${community.path.domain}")
    String domain;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @Autowired
    HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload/header", method = RequestMethod.POST)
    public String uploadHeader(Model model, MultipartFile headerImage){
        if(headerImage==null){
            model.addAttribute("error", "您没有选择图片");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件的格式不正确！");
            return "/site/setting";
        }

        String newName = CommunityUtil.generateUUID()+suffix;
        File dest = new File(uploadPath + newName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("服务器文件传输失败"+e.getMessage());
        }

        String headerUrl = domain + contextPath + "/user/header/" + newName;
        userService.updateUserHeaderURL(hostHolder.getUser().getId(),headerUrl);
        return "redirect:/index";
    }

    @LoginRequired
    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response){
        File file = new File(uploadPath + filename);
        String suffix = filename.substring(filename.lastIndexOf("."));
        response.setContentType("image/"+suffix);
        try (FileInputStream is = new FileInputStream(file);
             OutputStream os = response.getOutputStream()
        ){byte[] buffer = new byte[1024];
            int b = 0;
            while(b != -1){
                b = is.read(buffer);
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败");
        }
    }

    @LoginRequired
    @RequestMapping(path = "reset/password", method = RequestMethod.POST)
    public String resetPassword(@CookieValue("ticket") String ticket, Model model, String oldPassword, String newPassword, String checkPassword){
        if(StringUtils.isBlank(oldPassword)){
            model.addAttribute("oldPasswordMsg", "密码不可为空");
            return "/site/setting";
        }
        if(StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg", "密码不可为空");
            return "/site/setting";
        }
        if(StringUtils.isBlank(checkPassword)){
            model.addAttribute("checkPasswordMsg", "密码不可为空");
            return "/site/setting";
        }
        if(!checkPassword.equals(newPassword)){
            model.addAttribute("checkPasswordMsg", "密码不一致");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        if(!CommunityUtil.md5(oldPassword+user.getSalt()).equals(user.getPassword())){
            model.addAttribute("oldPasswordMsg", "密码错误");
            return "/site/setting";
        }
        userService.updatePassword(user.getId(), newPassword);
        model.addAttribute("msg","密码修改成功");
        model.addAttribute("target","/login");
        userService.logout(ticket);
        return "/site/operate-result";
    }
}
