package com.yuki.yukibot.dao.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.dao.ChatHistoryService;
import com.yuki.yukibot.model.chatgpt.ChatMessage;
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

    @Override
    public List<ChatMessage> getMsgHistoryList(String cacheKey) {
        String msgHistory = this.getMsgHistory(cacheKey);
        if ("null".equals(msgHistory)) {
            return new LinkedList<>();
        }
        return JSONUtil.toList(msgHistory, ChatMessage.class);
    }

    @Override
    public boolean hasSysMsg(List<ChatMessage> historyList) {
        if (CollUtil.isEmpty(historyList)) {
            return false;
        }
        return RoleEnum.isSysRoleMsg(historyList.get(0));
    }

    @Override
    public List<ChatMessage> clearHistory(String cacheKey, List<ChatMessage> historyList, CacheClearStrategyEnum strategyEnum) {
        List<ChatMessage> clearedHistoryList = null;
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
                clearedHistoryList = clearFarthestByHalf(historyList);
                break;
            default:
                break;
        }
        saveMsgHistory(cacheKey, JSONUtil.toJsonStr(clearedHistoryList));
        return clearedHistoryList;
    }

    public List<ChatMessage> clearFarthestByHalf(List<ChatMessage> historyList) {
        int size = historyList.size();
        List<ChatMessage> clearedHistoryList;
        if (hasSysMsg(historyList)) {
            if (size > ChatConstants.MAX_ONCE_CHAT_TIME + 1) {
                clearedHistoryList = CollUtil.sub(historyList, ChatConstants.REMOVE_HISTORY_SIZE + 2, size);
                clearedHistoryList.add(0, historyList.get(0));
                log.info("包含设定信息的数据,聊天记录折半");
            } else {
                return historyList;
            }
        } else {
            if (size > ChatConstants.MAX_ONCE_CHAT_TIME) {
                clearedHistoryList = CollUtil.sub(historyList, ChatConstants.REMOVE_HISTORY_SIZE + 1, size);
                log.info("不包含设定信息的数据,聊天记录折半");
            } else {
                return historyList;
            }
        }
        return clearedHistoryList;
    }


}
