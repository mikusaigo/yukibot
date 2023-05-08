package com.yuki.yukibot.model.chatgpt;


import lombok.Data;

@Data
public class ChatUsage {

    // 请求的消息token数
    private int prompt_tokens;

    // 回复的消息token数
    private int completion_tokens;

    // 总token数
    private int total_tokens;
}
