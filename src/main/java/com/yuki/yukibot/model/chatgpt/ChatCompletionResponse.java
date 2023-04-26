package com.yuki.yukibot.model.chatgpt;

import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionResponse {

    private String id;

    private String object;

    private long created;

    private String model;

    private ChatUsage usage;

    private List<ChatCompletionChoice> choices;
}
