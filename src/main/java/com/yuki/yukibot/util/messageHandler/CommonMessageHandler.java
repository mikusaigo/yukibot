package com.yuki.yukibot.util.messageHandler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.api.ChatGPTApi;
import com.yuki.yukibot.dao.ChatHistoryService;
import com.yuki.yukibot.exception.YukiBotException;
import com.yuki.yukibot.model.chatgpt.ChatMessage;
import com.yuki.yukibot.util.AvailableChecker;
import com.yuki.yukibot.util.ChatUtil;
import com.yuki.yukibot.util.enums.RoleEnum;
import com.yuki.yukibot.util.msgsender.GroupMessageSender;
import com.yuki.yukibot.util.msgsender.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendAddEvent;
import net.mamoe.mirai.event.events.FriendInputStatusChangedEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.event.events.SignEvent;
import net.mamoe.mirai.event.events.StrangerMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageContent;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonMessageHandler extends SimpleListenerHost implements MessageHandler {

    @Autowired
    private final AvailableChecker checker;

    @Autowired
    private final ChatGPTApi chatGPTApi;

    @Autowired
    private final ChatHistoryService chatHistoryService;

    @EventHandler
    @Override
    public void onGroupMessage(GroupMessageEvent event) {
        Group group = event.getGroup();
        boolean available = checker.groupAvailableCheck(group);
        if (!available){
            return;
        }
        groupChat(event);

    }

    private void groupChat(GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        MessageContent messageContent = message.get(MessageContent.Key);
        if (messageContent instanceof At) {
            At at = (At) messageContent;
            if (at.getTarget() == event.getBot().getId()) {
                sendMsg(event, message);
            }
        }
    }

    @EventHandler
    @Override
    public void onFriendMessage(FriendMessageEvent event) {
        Friend friend = event.getFriend();
        boolean available = checker.friendAvailableCheck(friend);
        if (!available){
            return;
        }
        friendChat(friend, event.getMessage());
    }

    @EventHandler
    @Override
    public void onNewFriendRequest(NewFriendRequestEvent event) {
        boolean available = checker.friendAddAvailableCheck(event);
        if (available) {
            event.accept();
            log.info("添加好友成功,好友QQ号为====>{}, 好友名称为====>{}", event.getFromId(), event.getFromNick());
        }else {
            event.reject(false);
        }

    }

    @EventHandler
    @Override
    public void onFriendInputMessage(FriendInputStatusChangedEvent event) {
        boolean inputting = event.getInputting();
        if (!inputting){
            MessageSender.sendMessage(event.getFriend(), "要离开了喵？");
        }
    }

    public void friendChat(Friend friend, MessageChain messageChain){
        ChatMessage chatMessage = new ChatMessage(RoleEnum.USER.getRole(), messageChain.contentToString());
        MessageSender.sendMessage(friend, chatGPTApi.chat(Collections.singletonList(chatMessage)));
    }

    @EventHandler
    @Override
    public void onStrangerMessage(StrangerMessageEvent event) {

    }

    @EventHandler
    @Override
    public void onSignMessage(SignEvent event) {

    }

    public void sendMsg(GroupMessageEvent event, MessageChain messageChain) {
        Group group = event.getGroup();
        Member sender = event.getSender();
        String message = messageChain.contentToString();
        String result;
        boolean sysMsg = ChatUtil.isSysMsg(message);
        List<ChatMessage> msgHistoryList = chatHistoryService.getMsgHistoryList(group.getId(), sender.getId());
        List<ChatMessage> clearedHistoryList = chatHistoryService.clearHistory(group.getId(), sender.getId(), msgHistoryList);
        if (sysMsg) {
            message = ChatUtil.getRealMsg(message, true);
            if (CharSequenceUtil.isNotBlank(message)) {
                GroupMessageSender.sendQuoteAtMsg(group, sender, event.getMessage(), "好的");
                saveCache(group.getId(), sender.getId(), message, null, true);
            }
            return;
        } else {
            try {
                message = ChatUtil.getRealMsg(message, false);
                ChatMessage chatMessage = new ChatMessage(RoleEnum.USER.getRole(), message);
                clearedHistoryList.add(chatMessage);
                result = chatGPTApi.chat(clearedHistoryList);
                saveCache(group.getId(), sender.getId(), message, result, false);
            } catch (YukiBotException e) {
                result = e.getMessage();
            } catch (RuntimeException e) {
                e.printStackTrace();
                result = "网络不太好，请稍后重试";
            }
        }
        GroupMessageSender.sendQuoteAtMsg(group, sender, messageChain, result);

    }


    /**
     * 保存聊天记录
     *
     * @param groupId    群组
     * @param memberId   群员
     * @param userMsg    用户消息
     * @param chatGptMsg chatGpt消息
     */
    public void saveCache(Long groupId, Long memberId, String userMsg, String chatGptMsg, boolean isSysMsg) {
        List<ChatMessage> msgHistoryList = chatHistoryService.getMsgHistoryList(groupId, memberId);
        ChatMessage user = new ChatMessage(RoleEnum.USER.getRole(), userMsg);
        ChatMessage system = new ChatMessage(RoleEnum.SYSTEM.getRole(), userMsg);
        ChatMessage assistant = new ChatMessage(RoleEnum.ASSISTANT.getRole(), chatGptMsg);
        if (CollUtil.isEmpty(msgHistoryList)) {
            msgHistoryList = new LinkedList<>();
        }
        if (isSysMsg) {
            ChatMessage first = CollUtil.getFirst(msgHistoryList);
            if (null != first && RoleEnum.SYSTEM.getRole().equals(first.getRole())) {
                msgHistoryList.set(0, system);
            }
            msgHistoryList.add(0, system);
        } else {
            msgHistoryList.add(user);
            msgHistoryList.add(assistant);
        }
        String cache = JSONUtil.toJsonStr(msgHistoryList);
        chatHistoryService.saveMsgHistory(groupId, memberId, cache);

    }
}
