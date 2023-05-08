package com.yuki.yukibot.model.chatgpt;

public class ChatMessageCache extends ChatMessage{

    private int token;

    public ChatMessageCache(String role, String content, int token) {
        super(role, content);
        this.token = token;
    }

    public ChatMessageCache(int token) {
        this.token = token;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }
}
