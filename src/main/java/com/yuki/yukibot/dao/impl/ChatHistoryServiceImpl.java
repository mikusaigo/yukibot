package com.yuki.yukibot.dao.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.dao.ChatHistoryService;
import com.yuki.yukibot.model.chatgpt.ChatMessage;
import com.yuki.yukibot.util.RoleEnum;
import com.yuki.yukibot.util.constants.ChatConstants;
import com.yuki.yukibot.util.constants.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
     * @param groupId 群号
     * @param memberId 群成员qq号
     * @param jsonMsgList 历史记录的json格式字符串
     */
    public void saveMsgHistory(Long groupId, Long memberId, String jsonMsgList){
        String key = RedisConstants.BASE_SYS_KEY + RedisConstants.GROUP_MSG_KEY + CharSequenceUtil.join(StrPool.COLON, groupId, memberId);
        redisTemplate.opsForValue().set(key, jsonMsgList);
    }

    /**
     * 根据群号，群成员qq号获取历史记录
     * @param groupId 群号
     * @param memberId 群成员qq号
     * @return
     */
    @Override
    public String getMsgHistory(Long groupId, Long memberId) {
        String key = RedisConstants.BASE_SYS_KEY + RedisConstants.GROUP_MSG_KEY + CharSequenceUtil.join(StrPool.COLON, groupId, memberId);
        Object value = redisTemplate.opsForValue().get(key);
        return String.valueOf(value);
    }

    @Override
    public List<ChatMessage> getMsgHistoryList(Long groupId, Long memberId) {
        String msgHistory = getMsgHistory(groupId, memberId);
        if ("null".equals(msgHistory)){
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
    public List<ChatMessage> clearHistory(Long groupId, Long memberId, List<ChatMessage> historyList) {
        int size = historyList.size();
        List<ChatMessage> clearedHistoryList;
        if (hasSysMsg(historyList)){
            if (size > ChatConstants.MAX_ONCE_CHAT_TIME + 1){
                clearedHistoryList = CollUtil.sub(historyList, ChatConstants.REMOVE_HISTORY_SIZE + 2, size);
                clearedHistoryList.add(0, historyList.get(0));
                log.info("包含设定信息的数据,聊天记录折半,群组Id===>{}, 群员Id===>{}", groupId, memberId);
            }else {
                return historyList;
            }
        }else {
            if (size > ChatConstants.MAX_ONCE_CHAT_TIME){
                clearedHistoryList = CollUtil.sub(historyList, ChatConstants.REMOVE_HISTORY_SIZE + 1, size);
                log.info("不包含设定信息的数据,聊天记录折半,群组Id===>{}, 群员Id===>{}", groupId, memberId);
            }else {
                return historyList;
            }
        }
        saveMsgHistory(groupId, memberId, JSONUtil.toJsonStr(clearedHistoryList));
        return clearedHistoryList;
    }

    public String getGroupCacheKey(Long groupId, Long memberId){
        return RedisConstants.BASE_SYS_KEY + RedisConstants.GROUP_MSG_KEY + CharSequenceUtil.join(StrPool.COLON, groupId, memberId);
    }


}
