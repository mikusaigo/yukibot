package com.yuki.yukibot.api;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.model.chatgpt.ChatCompletionRequest;
import com.yuki.yukibot.model.chatgpt.ChatCompletionResponse;
import com.yuki.yukibot.model.chatgpt.ChatMessage;
import com.yuki.yukibot.model.chatgpt.ExecuteRet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ChatGPTApi {
    private static final String API_KEY = "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";

    public ExecuteRet post(String body) {
        HttpResponse response = HttpUtil.createPost("https://api.openai.com/v1/chat/completions")
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Authorization", "Bearer " + API_KEY)
                .body(body)
                .execute();
        ChatCompletionResponse bean = JSONUtil.toBean(response.body(), ChatCompletionResponse.class);
        return new ExecuteRet(response.isOk(), bean.getChoices().get(0).getMessage().getContent(), null, 200);
    }


    public String chat(String prompt) {
        ChatMessage chatMessage = new ChatMessage("user", prompt);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(Collections.singletonList(chatMessage))
                .max_tokens(1000)
                .frequency_penalty(0)
                .presence_penalty(0)
                .temperature(0.7)
                .stream(false)
                .build();
        ExecuteRet result = post(JSONUtil.toJsonStr(chatCompletionRequest));
        return result.getRespStr();
    }


}
