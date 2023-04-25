package com.yuki.yukibot.model.chatgpt;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatCompletionRequest {

    // 选择使用的模型，如gpt-3.5-turbo
    String model;

    // 发送的消息列表
    List<ChatMessage> messages;

    // 温度，参数从0-2，越低表示越精准，越高表示越广发，回答的内容重复率越低
    Double temperature;

    // 回复条数，一次对话回复的条数
    Integer num;

    // 是否流式处理，就像ChatGPT一样的处理方式，会增量的发送信息
    Boolean stream;

    //
    List<String> stop;

    // 生成的答案允许的最大token数
    Integer max_tokens;

    Integer frequency_penalty;

    Integer presence_penalty;

    // 对话用户
    String user;
}
