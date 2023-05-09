package com.yuki.yukibot.dao;

import com.yuki.yukibot.model.chatgpt.ChatMessageCache;
import com.yuki.yukibot.util.enums.CacheClearStrategyEnum;

import java.util.List;

public interface ChatHistoryService {

    /**
     * 保存群组聊天记录
     * @param cacheKey 聊天记录key
     * @param jsonMsgList json消息集合
     */
    void saveMsgHistory(String cacheKey, String jsonMsgList);

    /**
     * 获取聊天记录的json字符串格式
     * @param cacheKey 聊天记录key
     * @return 聊天记录
     */
    String getMsgHistory(String cacheKey);

    /**
     * 获取聊天记录的实体集合
     * @param cacheKey 聊天记录key
     * @return 聊天记录实体集合
     */
    List<ChatMessageCache> getMsgHistoryList(String cacheKey);

    /**
     * 检测历史记录中是否存在sys设定消息
     * @param historyList 历史记录
     * @return 是否存在sys设定消息
     */
    boolean hasSysMsg(List<ChatMessageCache> historyList);

    /**
     * 根据配置及策略清理聊天记录
     *
     * @param cacheKey     聊天记录key
     * @param strategyEnum 记录清除策略
     * @return 删除后的历史记录集合
     */
    List<ChatMessageCache> clearHistory(String cacheKey, CacheClearStrategyEnum strategyEnum);
}
