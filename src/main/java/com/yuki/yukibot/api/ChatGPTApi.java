package com.yuki.yukibot.api;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.model.chatgpt.ChatCompletionRequest;
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
    private static final String API_ENDPOINT = "https://api.chatgpt.com";
    private static final String API_KEY = "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";
    private static final MultiThreadedHttpConnectionManager CONNECTION_MANAGER = new MultiThreadedHttpConnectionManager();

    static {
        // 默认单个host最大链接数
        CONNECTION_MANAGER.getParams().setDefaultMaxConnectionsPerHost(
                Integer.valueOf(20));
        // 最大总连接数，默认20
        CONNECTION_MANAGER.getParams()
                .setMaxTotalConnections(20);
        // 连接超时时间
        CONNECTION_MANAGER.getParams()
                .setConnectionTimeout(60000);
        // 读取超时时间
        CONNECTION_MANAGER.getParams().setSoTimeout(60000);
    }

    public ExecuteRet post(String body) {
        HttpResponse response = HttpUtil.createPost("https://api.openai.com/v1/chat/completions")
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Authorization", "Bearer " + API_KEY)
                .body(body)
                .execute();
        HttpRequest request = HttpRequest.post("");
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=utf-8");
        header.put("Authorization", "Bearer " + API_KEY);
        request.addHeaders(header);
        System.out.println(response.body());
        return new ExecuteRet(false, "", null, -1);
    }

    public static void main(String[] args) {
//        ChatGPTApi chatGPTApi = new ChatGPTApi();
//        System.out.println(chatGPTApi.chat("你好"));
        HttpRequest request = HttpRequest.post("https://api.openai.com/v1/chat/completions");
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=utf-8");
        header.put("Authorization", "Bearer " + API_KEY);
        request.addHeaders(header);
        ChatMessage chatMessage = new ChatMessage("user", "hello world");
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(Collections.singletonList(chatMessage))
                .max_tokens(1000)
                .frequency_penalty(0)
                .presence_penalty(0)
                .temperature(0.7)
                .stream(false)
                .build();
        request.body(JSONUtil.toJsonStr(chatCompletionRequest));
        HttpResponse execute = request.execute();
        System.out.println(JSONUtil.toJsonStr(execute.body()));
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
