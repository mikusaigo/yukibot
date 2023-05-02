package com.yuki.yukibot.api;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.model.chatgpt.ChatCompletionRequest;
import com.yuki.yukibot.model.chatgpt.ChatCompletionResponse;
import com.yuki.yukibot.model.chatgpt.ChatMessage;
import com.yuki.yukibot.model.chatgpt.ExecuteRet;
import com.yuki.yukibot.util.RoleEnum;
import com.yuki.yukibot.util.constants.ChatConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

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
        log.info("chatgpt请求结果===>{}", JSONUtil.toJsonStr(response));
        if (429 == response.getStatus()){
            return new ExecuteRet(response.isOk(), "请求次数过多", null, response.getStatus());
        }
        ChatCompletionResponse bean = JSONUtil.toBean(response.body(), ChatCompletionResponse.class);
        return new ExecuteRet(response.isOk(), bean.getChoices().get(0).getMessage().getContent(), null, 200);
    }


    public String chat(List<ChatMessage> messages) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .max_tokens(1000)
                .frequency_penalty(0)
                .presence_penalty(0)
                .temperature(0.7)
                .stream(false)
                .build();
        ExecuteRet result = post(JSONUtil.toJsonStr(chatCompletionRequest));
        return result.getRespStr();
    }

    public String chat(String prompt, boolean isContinuous){
        ChatMessage chatMessage = new ChatMessage(RoleEnum.USER.getRole(), prompt);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo-0301")
                .messages(Collections.singletonList(chatMessage))
                .max_tokens(ChatConstants.MAX_TOKEN)
                .frequency_penalty(0)
                .presence_penalty(0)
                .temperature(0.7)
                .stream(false)
                .build();
        ExecuteRet result = post(JSONUtil.toJsonStr(chatCompletionRequest));
        return result.getRespStr();
    }

}
