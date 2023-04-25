package com.yuki.yukibot.model.chatgpt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatCompletionChoice {

    Integer index;

    ChatMessage message;

    String finishReason;

}
