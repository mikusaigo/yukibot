package com.yuki.yukibot.util.messageHandler;

import net.mamoe.mirai.event.events.FriendInputStatusChangedEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.event.events.StrangerMessageEvent;

/**
 * 消息处理器接口
 */
public interface MessageHandler{

    /**
     * 群组消息处理
     * @param event 群组消息事件
     */
    void onGroupMessage(GroupMessageEvent event);

    /**
     * 好友消息处理
     * @param event 群组消息事件
     */
    void onFriendMessage(FriendMessageEvent event);

    /**
     * 新朋友添加请求处理
     * @param event 新好友添加请求事件
     */
    void onNewFriendRequest(NewFriendRequestEvent event);

    /**
     * 好友输入状态处理
     * @param event 好友消息输入状态改变事件
     */
    void onFriendInputMessage(FriendInputStatusChangedEvent event);

    /**
     * 陌生人消息处理
     * @param event 陌生人消息事件
     */
    void onStrangerMessage(StrangerMessageEvent event);
}
