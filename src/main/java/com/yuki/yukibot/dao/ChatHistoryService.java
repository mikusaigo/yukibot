package com.yuki.yukibot.dao;

import com.yuki.yukibot.model.chatgpt.ChatMessage;

import java.util.List;

public interface ChatHistoryService {

    /**
     * 保存聊天记录
     * @param groupId 群号
     * @param memberId 群成员qq号
     * @param jsonMsgList json消息集合
     */
    void saveMsgHistory(Long groupId, Long memberId, String jsonMsgList);

    /**
     * 获取聊天记录的json字符串格式
     * @param groupId 群号
     * @param memberId 群成员qq号
     * @return 聊天记录
     */
    String getMsgHistory(Long groupId, Long memberId);

    /**
     * 获取聊天记录的实体集合
     * @param groupId 群号
     * @param memberId 群成员qq号
     * @return 聊天记录实体集合
     */
    List<ChatMessage> getMsgHistoryList(Long groupId, Long memberId);

    /**
     * 检测历史记录中是否存在sys设定消息
     * @param historyList 历史记录
     * @return 是否存在sys设定消息
     */
    boolean hasSysMsg(List<ChatMessage> historyList);

    /**
     * 根据配置清理部分聊天记录
     * @param groupId 群号
     * @param memberId 群成员qq号
     * @param historyList 历史记录
     * @return 删除后的历史记录集合
     */
    List<ChatMessage> clearHistory(Long groupId, Long memberId, List<ChatMessage> historyList);
}
