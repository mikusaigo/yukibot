package com.yuki.yukibot.model.chatgpt;

public class FriendChatCache extends ChatCache{

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
