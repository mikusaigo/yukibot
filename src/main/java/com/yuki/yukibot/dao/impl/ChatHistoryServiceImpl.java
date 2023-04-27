package com.yuki.yukibot.dao.impl;

import com.yuki.yukibot.dao.ChatHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatHistoryServiceImpl implements ChatHistoryService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private void saveUserInfo(){

    }


}
