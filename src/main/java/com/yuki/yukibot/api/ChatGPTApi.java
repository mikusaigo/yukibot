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

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatGPTApi {

    private final ChatGptConfig chatGptConfig;

    /**
     * post方式请求chatgpt接口
     * @param body 请求接口时携带的chatgpt参数，如模型，最大token等
     * @see ChatCompletionRequest
     * @return 响应数据
     */
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

    /**
     * 调用chatgpt接口聊天
     * @param messages 请求chatgpt时携带的消息参数，是包含role和content的消息集合
     * @return 响应结果实体
     */
    public ChatCompletionResponse chat(List<ChatMessage> messages) {
        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest()
                .setModel(chatGptConfig.getModel())
                .setMessages(messages)
                .setMaxTokens(chatGptConfig.getMaxTokens())
                .setPresencePenalty(chatGptConfig.getPresencePenalty())
                .setFrequencyPenalty(chatGptConfig.getFrequencyPenalty())
                .setTemperature(chatGptConfig.getTemperature())
                .setStream(chatGptConfig.getStream());
        return postChatGpt(JSONUtil.toJsonStr(chatCompletionRequest));
    }

}
