package com.yuki.yukibot.util.messageHandler;

import net.mamoe.mirai.event.events.FriendInputStatusChangedEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.event.events.SignEvent;
import net.mamoe.mirai.event.events.StrangerMessageEvent;

public interface MessageHandler{

    void onGroupMessage(GroupMessageEvent event);

    void onFriendMessage(FriendMessageEvent event);

    void onNewFriendRequest(NewFriendRequestEvent event);

    void onFriendInputMessage(FriendInputStatusChangedEvent event);

    void onStrangerMessage(StrangerMessageEvent event);

    void onSignMessage(SignEvent event);
}
