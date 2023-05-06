package com.yuki.yukibot.model.chatgpt;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ChatCache {

    private String userMsg;

    private String chatMsg;

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

    public boolean isSysMsg() {
        return isSysMsg;
    }

    public void setSysMsg(boolean sysMsg) {
        isSysMsg = sysMsg;
    }
}
