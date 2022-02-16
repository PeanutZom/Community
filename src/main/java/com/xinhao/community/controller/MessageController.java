package com.xinhao.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinhao.community.annotation.LoginRequired;
import com.xinhao.community.entity.Message;
import com.xinhao.community.entity.PageInfo;
import com.xinhao.community.entity.User;
import com.xinhao.community.service.MessageService;
import com.xinhao.community.service.UserService;
import com.xinhao.community.util.CommunityConstant;
import com.xinhao.community.util.CommunityUtil;
import com.xinhao.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @Xinhao
 * @Date 2022/2/1
 * @Descrption
 */
@Controller
@RequestMapping()
public class MessageController implements CommunityConstant {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    @RequestMapping(path = "/message/list", method = RequestMethod.GET)
    public String getSessionList(Model model, PageInfo pageInfo){
        User loginUser = hostHolder.getUser();

        pageInfo.setLimit(5);
        pageInfo.setPath("/message/list");
        pageInfo.setRows(messageService.findSessionRows(loginUser.getId()));


        List<Message> sessions = messageService.findSession(loginUser.getId(),pageInfo.getOffset(), pageInfo.getLimit());
        List<Map<String, Object>> sessionVoList = new ArrayList<>();
        if(sessions != null){
            for(Message session : sessions){
                Map<String, Object> sessionVo = new HashMap<>();
                sessionVo.put("session",session);
                int userId = session.getFromId() == loginUser.getId() ? session.getToId() : session.getFromId();
                User user = userService.getUserById(userId);
                sessionVo.put("user",user);
                int messageCount = messageService.findMessageRows(session.getConversationId());
                sessionVo.put("messageCount",messageCount);
                int unread = messageService.findUnreadMessageCount(loginUser.getId(),session.getConversationId());
                sessionVo.put("unread",unread);
                sessionVoList.add(sessionVo);
            }
        }
        model.addAttribute("sessions",sessionVoList);

        int messageUnreadCount = messageService.findUnreadMessageCount(loginUser.getId(),null);
        model.addAttribute("messageUnreadCount",messageUnreadCount);

        int noticeUnreadCount = messageService.findUnreadNoticeCount(loginUser.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";
    }

    @RequestMapping(path = "/message/detail/{conversationId}", method = RequestMethod.GET)
    public String getSessionDetail(Model model, PageInfo pageInfo, @PathVariable("conversationId") String conversationId){
        User loginUser = hostHolder.getUser();

        pageInfo.setLimit(5);
        pageInfo.setPath("/message/detail/"+conversationId);
        pageInfo.setRows(messageService.findMessageRows(conversationId));

        List<Message> messageList = messageService.findMessage(conversationId, pageInfo.getOffset(), pageInfo.getLimit());
        List<Map<String, Object>> messageVoList = new ArrayList<>();

        if(messageList!=null){
            Message checkMessage = messageList.get(0);
            int targetId = checkMessage.getFromId() == loginUser.getId()? checkMessage.getToId() : checkMessage.getFromId();
            User target = userService.getUserById(targetId);
            model.addAttribute("target",target);
            for(Message message : messageList){
                Map<String, Object> messageVo = new HashMap<>();
                messageVo.put("message", message);
                User sender = userService.getUserById(message.getFromId());
                messageVo.put("sender",sender);
                messageVoList.add(messageVo);
            }
        }
        model.addAttribute("messages",messageVoList);
        model.addAttribute("loginUser",loginUser);

        List<Integer> toRead = getLetterIds(messageList);
        if(!toRead.isEmpty()){
            System.out.println(toRead);
            messageService.readMessage(toRead);
        }
        return "/site/letter-detail";
    }

    @LoginRequired
    @ResponseBody
    @RequestMapping(path = "/message/send", method = RequestMethod.POST)
    public String sendMessage(String toName, String content){
        User target = userService.getUserByUsername(toName);
        if(target==null){
            return CommunityUtil.getJsonString(1, "目标用户不存在");
        }
        Message message = new Message();
        message.setToId(target.getId());
        message.setContent(content);
        User loginUser = hostHolder.getUser();
        message.setFromId(loginUser.getId());
        message.setStatus(0);
        message.setCreateTime(new Date());
        if(message.getFromId()>message.getToId()){
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }else {
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }
        messageService.createMessage(message);
        return CommunityUtil.getJsonString(0);
    }

    @RequestMapping(path = "/notice/list")
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();

        //查询评论类通知

        Message message = messageService.findLatestUnreadNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVo = new HashMap<>();
        if(message != null){
            int unread = messageService.findUnreadNoticeCount(user.getId(),TOPIC_COMMENT);
            messageVo.put("unread",unread);
            int count = messageService.findNoticeRows(user.getId(), TOPIC_COMMENT);
            messageVo.put("count", count);
            messageVo.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> contentData = JSONObject.parseObject(content, Map.class);
            messageVo.put("user", userService.getUserById((Integer) contentData.get("userId")));
            messageVo.put("entityType", contentData.get("entityType"));
            messageVo.put("entityId", contentData.get("entityId"));
            messageVo.put("postId",contentData.get("postId"));
        }
        model.addAttribute("commentNotice", messageVo);

        //查询点赞类通知
        message = messageService.findLatestUnreadNotice(user.getId(), TOPIC_LIKE);
        messageVo = new HashMap<>();
        if(message != null){
            int unread = messageService.findUnreadNoticeCount(user.getId(),TOPIC_LIKE);
            messageVo.put("unread",unread);
            int count = messageService.findNoticeRows(user.getId(), TOPIC_LIKE);
            messageVo.put("count", count);
            messageVo.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> contentData = JSONObject.parseObject(content, Map.class);
            messageVo.put("user", userService.getUserById((Integer) contentData.get("userId")));
            messageVo.put("entityType", contentData.get("entityType"));
            messageVo.put("entityId", contentData.get("entityId"));
            messageVo.put("postId",contentData.get("postId"));
        }
        model.addAttribute("likeNotice", messageVo);

        //查询关注类通知
        message = messageService.findLatestUnreadNotice(user.getId(), TOPIC_FOLLOW);
        messageVo = new HashMap<>();
        if(message != null){
            int unread = messageService.findUnreadNoticeCount(user.getId(),TOPIC_FOLLOW);
            messageVo.put("unread",unread);
            int count = messageService.findNoticeRows(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count", count);
            messageVo.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> contentData = JSONObject.parseObject(content, Map.class);
            messageVo.put("user", userService.getUserById((Integer) contentData.get("userId")));
            messageVo.put("entityType", contentData.get("entityType"));
            messageVo.put("entityId", contentData.get("entityId"));
        }
        model.addAttribute("followNotice", messageVo);

        int messageUnreadCount = messageService.findUnreadMessageCount(user.getId(),null);
        model.addAttribute("messageUnreadCount",messageUnreadCount);

        int noticeUnreadCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    @RequestMapping(path = "/notice/detail/{topic}")
    public String getNoticeDetail(Model model, @PathVariable("topic") String topic, PageInfo pageInfo){
        User user = hostHolder.getUser();

        pageInfo.setLimit(5);
        pageInfo.setPath("/notice/detail/" + topic);
        pageInfo.setRows(messageService.findNoticeRows(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, pageInfo.getOffset(), pageInfo.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if(noticeList != null){
            for (Message notice : noticeList){
                Map<String, Object> noticeVo = new HashMap<>();
                noticeVo.put("notice", notice);

                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> contentData = JSONObject.parseObject(content, Map.class);

                noticeVo.put("entityType", contentData.get("entityType"));
                noticeVo.put("entityId", contentData.get("entityId"));
                noticeVo.put("user", userService.getUserById((Integer) contentData.get("userId")));
                noticeVo.put("postId", contentData.get("postId"));
                // 通知作者
                noticeVo.put("fromUser", userService.getUserById(notice.getFromId()));

                noticeVoList.add(noticeVo);
            }
        }
        model.addAttribute("notices", noticeVoList);

        //设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if(!ids.isEmpty()){
            System.out.println("here!!!!!!!!!!!!!!!!!!!!!!!!");
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }
}
