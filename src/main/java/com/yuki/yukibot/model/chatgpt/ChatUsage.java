package com.yuki.yukibot.model.chatgpt;


import lombok.Data;

@Data
public class ChatUsage {

    private int prompt_tokens;

    private int completion_tokens;

    private int total_tokens;
}
