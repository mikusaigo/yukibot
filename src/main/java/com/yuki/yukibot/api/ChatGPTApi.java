package com.yuki.yukibot.api;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.config.ChatGptConfig;
import com.yuki.yukibot.model.chatgpt.ChatCompletionRequest;
import com.yuki.yukibot.model.chatgpt.ChatCompletionResponse;
import com.yuki.yukibot.model.chatgpt.ChatMessage;
import com.yuki.yukibot.util.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.SocketException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatGPTApi {

    private final ChatGptConfig chatGptConfig;

    public ChatCompletionResponse postChatGpt(String body) {
        HttpResponse response = HttpUtil.createPost(chatGptConfig.getApi().getUrl())
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Authorization", "Bearer " + chatGptConfig.getApi().getKey())
                .body(body)
                .execute();
        log.info("chatgpt请求结果===>{}", JSONUtil.toJsonStr(response));
        ResponseHandler.handleRespByState(response.getStatus());
        String chatgptResp = response.body();
        return JSONUtil.toBean(chatgptResp, ChatCompletionResponse.class);
    }


    public String chat(List<ChatMessage> messages) {
        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest()
                .setModel(chatGptConfig.getModel())
                .setMessages(messages)
                .setMaxTokens(chatGptConfig.getMaxTokens())
                .setPresencePenalty(chatGptConfig.getPresencePenalty())
                .setFrequencyPenalty(chatGptConfig.getFrequencyPenalty())
                .setTemperature(chatGptConfig.getTemperature())
                .setStream(chatGptConfig.getStream());
        ChatCompletionResponse result = postChatGpt(JSONUtil.toJsonStr(chatCompletionRequest));
        return result.getChoices().get(0).getMessage().getContent();
    }

}
