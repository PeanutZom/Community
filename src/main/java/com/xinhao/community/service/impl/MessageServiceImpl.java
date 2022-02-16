package com.xinhao.community.service.impl;

import com.xinhao.community.dao.MessageMapper;
import com.xinhao.community.entity.Message;
import com.xinhao.community.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Xinhao
 * @Date 2022/2/1
 * @Descrption
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    MessageMapper messageMapper;

    @Override
    public List<Message> findSession(int userId, int offset, int limit) {
        return messageMapper.getSessionList(userId, offset, limit);
    }

    @Override
    public int findSessionRows(int userId) {
        return messageMapper.getSessionRowsCount(userId);
    }

    @Override
    public List<Message> findMessage(String conversationId, int offset, int limit) {
        return messageMapper.getMessageList(conversationId, offset, limit);
    }

    @Override
    public int findMessageRows(String conversationId) {
        return messageMapper.getMessageRowsCount(conversationId);
    }

    @Override
    public int findUnreadMessageCount(int userId, String conversationId) {
        return messageMapper.getUnreadMessageCount(userId, conversationId);
    }

    @Override
    public int createMessage(Message message) {
        return messageMapper.insertMessage(message);
    }

    @Override
    public int readMessage(List<Integer> messageIds) {
        return messageMapper.updateMessageStatus(messageIds, 1);
    }

    @Override
    public int findNoticeRows(int userId, String topic) {
        return messageMapper.getNoticeCount(userId, topic);
    }

    @Override
    public int findUnreadNoticeCount(int userId, String topic) {
        return messageMapper.getUnreadNoticeCount(userId, topic);
    }

    @Override
    public Message findLatestUnreadNotice(int userId, String topic) {
        return messageMapper.selectLatestUnreadMessage(userId, topic);
    }

    @Override
    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}
