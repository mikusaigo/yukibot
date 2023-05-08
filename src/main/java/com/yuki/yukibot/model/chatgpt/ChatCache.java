package com.yuki.yukibot.model.chatgpt;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ChatCache {

    // 用户消息
    private String userMsg;

    // chatgpt回复的消息
    private String chatMsg;

    // 当前已使用的token
    // TODO: 2023/5/8 在这里记录token可能会在数据删除时产生问题
    private int token;

    // 是否为设定消息
    private boolean isSysMsg;

    public String getUserMsg() {
        return userMsg;
    }

    public void setUserMsg(String userMsg) {
        this.userMsg = userMsg;
    }

    public String getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(String chatMsg) {
        this.chatMsg = chatMsg;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public boolean isSysMsg() {
        return isSysMsg;
    }

    public void setSysMsg(boolean sysMsg) {
        isSysMsg = sysMsg;
    }
}
