package com.yuki.yukibot.util.messageHandler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.api.ChatGPTApi;
import com.yuki.yukibot.dao.ChatHistoryService;
import com.yuki.yukibot.exception.YukiBotException;
import com.yuki.yukibot.model.chatgpt.ChatCache;
import com.yuki.yukibot.model.chatgpt.ChatMessage;
import com.yuki.yukibot.util.AvailableChecker;
import com.yuki.yukibot.util.CacheKeyBuilder;
import com.yuki.yukibot.util.ChatUtil;
import com.yuki.yukibot.util.constants.CacheClearStrategyEnum;
import com.yuki.yukibot.util.enums.RoleEnum;
import com.yuki.yukibot.util.msgsender.GroupMessageSender;
import com.yuki.yukibot.util.msgsender.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendInputStatusChangedEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.event.events.SignEvent;
import net.mamoe.mirai.event.events.StrangerMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageContent;
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
                sendGroupMsg(event);
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
        friendChat(event);
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
//            MessageSender.sendMessage(event.getFriend(), "要离开了喵？");
        }
    }

    public void friendChat(FriendMessageEvent event){
        sendFriendMsg(event);
    }

    @EventHandler
    @Override
    public void onStrangerMessage(StrangerMessageEvent event) {

    }

    @EventHandler
    @Override
    public void onSignMessage(SignEvent event) {

    }

    public void sendGroupMsg(GroupMessageEvent event) {
        Group group = event.getGroup();
        Member sender = event.getSender();
        MessageChain messageChain = event.getMessage();
        String groupCacheKey = CacheKeyBuilder.buildGroupKey(group.getId(), sender.getId());
        String result = getResult(groupCacheKey, messageChain);
        if (null == result){
            return;
        }
        GroupMessageSender.sendQuoteAtMsg(group, sender, messageChain, result);
    }

    public void sendFriendMsg(FriendMessageEvent event){
        Friend friend = event.getSender();
        MessageChain messageChain = event.getMessage();
        String friendCacheKey = CacheKeyBuilder.buildFriendKey(friend.getId());
        String result = getResult(friendCacheKey, messageChain);
        if (null == result){
            return;
        }
        MessageSender.sendMessage(friend, result);
    }

    private String getResult(String cacheKey, MessageChain messageChain) {
        String message = messageChain.contentToString();
        String result;
        boolean sysMsg = ChatUtil.isSysMsg(message);
        message = ChatUtil.getRealMsg(message, sysMsg);
        if (CharSequenceUtil.isBlank(message)){
            return null;
        }
        List<ChatMessage> msgHistoryList = chatHistoryService.getMsgHistoryList(cacheKey);
        List<ChatMessage> clearedHistoryList = chatHistoryService.clearHistory(cacheKey, msgHistoryList, CacheClearStrategyEnum.FARTHEST_HALF);
        if (sysMsg) {
            result = "好的";
            saveCache(cacheKey, new ChatCache(message, null, true));
        } else {
            try {
                ChatMessage chatMessage = new ChatMessage(RoleEnum.USER.getRole(), message);
                clearedHistoryList.add(chatMessage);
                result = chatGPTApi.chat(clearedHistoryList);
                saveCache(cacheKey, new ChatCache(message, result, false));
            } catch (YukiBotException e) {
                result = e.getMessage();
            } catch (RuntimeException e) {
                e.printStackTrace();
                result = "网络不太好，请稍后重试";
            }
        }
        return result;
    }

    /**
     * 保存群聊聊天记录
     *
     * @param cacheKey 聊天记录key
     * @param chatCache  聊天缓存实体
     */
    public void saveCache(String cacheKey, ChatCache chatCache) {
        List<ChatMessage> msgHistoryList = chatHistoryService.getMsgHistoryList(cacheKey);
        String cache = getJsonStrCache(chatCache, msgHistoryList);
        chatHistoryService.saveMsgHistory(cacheKey, cache);
    }

    private static String getJsonStrCache(ChatCache chatCache, List<ChatMessage> msgHistoryList) {
        ChatMessage user = new ChatMessage(RoleEnum.USER.getRole(), chatCache.getChatMsg());
        ChatMessage system = new ChatMessage(RoleEnum.SYSTEM.getRole(), chatCache.getUserMsg());
        ChatMessage assistant = new ChatMessage(RoleEnum.ASSISTANT.getRole(), chatCache.getChatMsg());
        if (CollUtil.isEmpty(msgHistoryList)) {
            msgHistoryList = new LinkedList<>();
        }
        if (chatCache.isSysMsg()) {
            ChatMessage first = CollUtil.getFirst(msgHistoryList);
            if (null != first && RoleEnum.SYSTEM.getRole().equals(first.getRole())) {
                msgHistoryList.set(0, system);
            }
            msgHistoryList.add(0, system);
        } else {
            msgHistoryList.add(user);
            msgHistoryList.add(assistant);
        }
        return JSONUtil.toJsonStr(msgHistoryList);
    }
}
