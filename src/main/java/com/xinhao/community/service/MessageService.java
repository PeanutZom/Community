package com.xinhao.community.service;

import com.xinhao.community.entity.Message;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Xinhao
 * @Date 2022/2/1
 * @Descrption
 */

public interface MessageService {
    List<Message> findSession(int userId, int offset, int limit);
    int findSessionRows(int userId);
    List<Message> findMessage(String conversationId, int offset, int limit);
    int findMessageRows(String conversationId);
    int findUnreadMessageCount(int userId, String conversationId);
    int createMessage(Message message);
    int readMessage(List<Integer> messageIds);
}
