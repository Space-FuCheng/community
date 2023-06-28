package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 当前用户的会话列表，针对每个会话只返回最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);
    //当前用户会话数量
    int selectConversationCount(int userId);

    //某个会话的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);
    //某个会话的私信数量
    int selectLetterCount(String conversationId);

    //未读私信数量
    int selectLetterUnreadCount(int userId, String conversationId);

    //增加私信
    int insertMessage(Message message);

    //更改消息状态
    int updateStatus(List<Integer> ids, int status);

}
