package com.xinhao.community.dao;

import com.xinhao.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Xinhao
 * @Date 2022/2/1
 * @Descrption
 */
@Mapper
public interface MessageMapper {
    List<Message> getSessionList(int userId, int offset, int limit);
    int getSessionRowsCount(int userId);
    List<Message> getMessageList(String conversationId, int offset, int limit);
    int getMessageRowsCount(String conversationId);
    int getUnreadMessageCount(int userId, String conversationId);
    int insertMessage(Message message);
    int updateMessageStatus(List<Integer> messageIds, int status);
    int getUnreadNoticeCount(int userId, String topic);
    int getNoticeCount(int userId, String topic);
    Message selectLatestUnreadMessage(int userId, String topic);
    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
