package com.yuki.yukibot.dao;

import com.yuki.yukibot.model.chatgpt.ChatMessage;

import java.util.List;

public interface ChatHistoryService {

    void saveMsgHistory(Long groupId, Long memberId, String jsonMsgList);

    String getMsgHistory(Long groupId, Long memberId);

    List<ChatMessage> getMsgHistoryList(Long groupId, Long memberId);
}
