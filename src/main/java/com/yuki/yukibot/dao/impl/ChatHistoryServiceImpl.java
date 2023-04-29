package com.yuki.yukibot.dao.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.dao.ChatHistoryService;
import com.yuki.yukibot.model.chatgpt.ChatMessage;
import com.yuki.yukibot.util.constants.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveMsgHistory(Long groupId, Long memberId, String jsonMsgList){
        String key = RedisConstants.BASE_SYS_KEY + RedisConstants.GROUP_MSG_KEY + CharSequenceUtil.join(StrPool.COLON, groupId, memberId);
        redisTemplate.opsForValue().set(key, jsonMsgList);
    }

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
            return null;
        }
        return JSONUtil.toList(msgHistory, ChatMessage.class);
    }


}
