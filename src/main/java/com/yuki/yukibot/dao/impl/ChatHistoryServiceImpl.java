package com.yuki.yukibot.dao.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.dao.ChatHistoryService;
import com.yuki.yukibot.model.chatgpt.ChatMessageCache;
import com.yuki.yukibot.util.constants.CacheClearStrategyEnum;
import com.yuki.yukibot.util.constants.ChatConstants;
import com.yuki.yukibot.util.enums.RoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 聊天历史记录服务
 */
@Slf4j
@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 在缓存中根据群号，群成员记录历史记录
     *
     * @param cacheKey    群组记录key
     * @param jsonMsgList 历史记录的json格式字符串
     */
    public void saveMsgHistory(String cacheKey, String jsonMsgList) {
        redisTemplate.opsForValue().set(cacheKey, jsonMsgList, 1, TimeUnit.HOURS);
    }

    /**
     * 根据群号，群成员qq号获取历史记录
     *
     * @param cacheKey 群组记录key
     * @return 历史记录的json格式字符串
     */
    @Override
    public String getMsgHistory(String cacheKey) {
        Object value = redisTemplate.opsForValue().get(cacheKey);
        return String.valueOf(value);
    }

    /**
     * 获取聊天记录的实体集合
     *
     * @param cacheKey 聊天记录key
     * @return 聊天记录实体集合
     */
    @Override
    public List<ChatMessageCache> getMsgHistoryList(String cacheKey) {
        String msgHistory = this.getMsgHistory(cacheKey);
        if ("null".equals(msgHistory)) {
            return new LinkedList<>();
        }
        return JSONUtil.toList(msgHistory, ChatMessageCache.class);
    }

    /**
     * 检测历史记录中是否存在sys设定消息
     *
     * @param historyList 历史记录
     * @return 是否存在sys设定消息
     */
    @Override
    public boolean hasSysMsg(List<ChatMessageCache> historyList) {
        if (CollUtil.isEmpty(historyList)) {
            return false;
        }
        return RoleEnum.isSysRoleMsg(historyList.get(0));
    }

    /**
     * 根据配置及策略清理聊天记录
     *
     * @param cacheKey     聊天记录key
     * @param strategyEnum 记录清除策略
     * @return 删除后的历史记录集合
     */
    @Override
    public List<ChatMessageCache> clearHistory(String cacheKey, CacheClearStrategyEnum strategyEnum) {
        List<ChatMessageCache> historyList = getMsgHistoryList(cacheKey);
        // 记录清理后的历史记录
        List<ChatMessageCache> clearedHistoryList = null;
        // ALL：清理当前用户的全部历史记录
        // ALL_WITHOUT_SYS；清理当前用户的历史记录，但保留设定信息
        // FARTHEST_HALF：根据配置，从记录的最远部分开始向近处清理
        switch (strategyEnum) {
            case ALL:
                redisTemplate.delete(cacheKey);
                break;
            case ALL_WITHOUT_SYS:
                if (CollUtil.isNotEmpty(historyList)) {
                    if (hasSysMsg(historyList)) {
                        String jsonHistory = JSONUtil.toJsonStr(historyList.get(0));
                        saveMsgHistory(cacheKey, jsonHistory);
                        clearedHistoryList = Collections.singletonList(historyList.get(0));
                    }
                }
                break;
            case FARTHEST_HALF:
                clearedHistoryList = clearFarthestByTokens(historyList);
                break;
            default:
                break;
        }
        // 更新缓存为清理后的历史记录
        saveMsgHistory(cacheKey, JSONUtil.toJsonStr(clearedHistoryList));
        return clearedHistoryList;
    }

    /**
     * 根据配置，从记录的最远部分开始向近处清理
     *
     * @param historyList 用户历史记录
     * @return 清理后的历史记录
     * @see ChatConstants 清理的具体数量配置
     */
    public List<ChatMessageCache> clearFarthestByTokens(List<ChatMessageCache> historyList) {
        int size = historyList.size();
        List<ChatMessageCache> clearedHistoryList;
        // 如果历史记录中不包含sys设定记录，则当历史记录数大于等于设定的最大记录数时，历史记录折半，
        // 如果历史记录中包含sys设定记录，则当历史记录数大于等于设定的最大记录数 + 1时，历史记录折半，
        // 如果没有满足条件，直接返回原历史记录集合
        if (hasSysMsg(historyList)) {
            if (size >= ChatConstants.MAX_ONCE_CHAT_TIME + 1) {
                clearedHistoryList = CollUtil.sub(historyList, ChatConstants.REMOVE_HISTORY_SIZE + 2, size);
                clearedHistoryList.add(0, historyList.get(0));
                log.info("包含设定信息的数据,聊天记录折半");
            } else {
                return historyList;
            }
        } else {
            if (size >= ChatConstants.MAX_ONCE_CHAT_TIME) {
                clearedHistoryList = CollUtil.sub(historyList, ChatConstants.REMOVE_HISTORY_SIZE + 1, size);
                log.info("不包含设定信息的数据,聊天记录折半");
            } else {
                return historyList;
            }
        }
        return clearedHistoryList;
    }


}
