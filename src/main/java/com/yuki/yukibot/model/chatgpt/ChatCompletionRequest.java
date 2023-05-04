package com.yuki.yukibot.model.chatgpt;

import cn.hutool.core.annotation.Alias;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ChatCompletionRequest {

    // 选择使用的模型，如gpt-3.5-turbo
    private String model;

    // 发送的消息列表
    private List<ChatMessage> messages;

    // 温度，参数从0-2，越低表示越精准，越高表示越广发，回答的内容重复率越低
    private Double temperature;

    // 回复条数，一次对话回复的条数
    private Integer num;

    // 是否流式处理，就像ChatGPT一样的处理方式，会增量的发送信息
    private Boolean stream;

    //
    private List<String> stop;

    @Alias("max_tokens")
    // 生成的答案允许的最大token数
    private Integer maxTokens;

    @Alias("frequency_penalty")
    private Integer frequencyPenalty;

    @Alias("presence_penalty")
    private Integer presencePenalty;
}
