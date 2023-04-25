package com.yuki.yukibot.model.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    // 消息角色
    String role;

    // 消息内容
    String content;
}
