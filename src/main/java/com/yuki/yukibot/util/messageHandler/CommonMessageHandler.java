package com.yuki.yukibot.util.messageHandler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.api.ChatGPTApi;
import com.yuki.yukibot.command.CommandRecognizer;
import com.yuki.yukibot.dao.ChatHistoryService;
import com.yuki.yukibot.exception.YukiBotException;
import com.yuki.yukibot.model.chatgpt.ChatCache;
import com.yuki.yukibot.model.chatgpt.ChatCompletionResponse;
import com.yuki.yukibot.model.chatgpt.ChatMessage;
import com.yuki.yukibot.model.chatgpt.ChatMessageCache;
import com.yuki.yukibot.util.AvailableChecker;
import com.yuki.yukibot.util.CacheKeyBuilder;
import com.yuki.yukibot.util.ChatUtil;
import com.yuki.yukibot.util.enums.CacheClearStrategyEnum;
import com.yuki.yukibot.util.enums.CommandTypeEnum;
import com.yuki.yukibot.util.enums.RoleEnum;
import com.yuki.yukibot.util.msgsender.GroupMessageSender;
import com.yuki.yukibot.util.msgsender.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.AnonymousMember;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendInputStatusChangedEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.event.events.StrangerMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageContent;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * 通用消息处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonMessageHandler extends SimpleListenerHost implements MessageHandler {

    private final AvailableChecker checker;

    private final ChatGPTApi chatGPTApi;

    private final ChatHistoryService chatHistoryService;

    /**
     * 群组消息处理
     * @param event 群组消息事件
     */
    @EventHandler
    @Override
    public void onGroupMessage(GroupMessageEvent event) {
        Group group = event.getGroup();
        // 校验群组是否有聊天的权限
        boolean available = checker.groupAvailableCheck(group);
        if (!available) {
            return;
        }
        groupChat(event);

    }

    /**
     * 群组成员chatgpt聊天
     * @param event 群组消息事件
     */
    private void groupChat(GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        // 仅处理at bot的消息
        MessageContent messageContent = message.get(MessageContent.Key);
        if (messageContent instanceof At) {
            At at = (At) messageContent;
            if (at.getTarget() == event.getBot().getId()) {
                sendGroupMsg(event);
            }
        }
    }

    /**
     * 好友消息处理
     * @param event 群组消息事件
     */
    @EventHandler
    @Override
    public void onFriendMessage(FriendMessageEvent event) {
        Friend friend = event.getFriend();
        // 校验好友是否有聊天的权限
        boolean available = checker.friendAvailableCheck(friend);
        if (!available) {
            return;
        }
        friendChat(event);
    }

    /**
     * 新朋友添加请求处理
     * @param event 新好友添加请求事件
     */
    @EventHandler
    @Override
    public void onNewFriendRequest(NewFriendRequestEvent event) {
        // 校验是否有添加bot为好友的权限
        boolean available = checker.friendAddAvailableCheck(event);
        if (available) {
            event.accept();
            log.info("添加好友成功,好友QQ号为====>{}, 好友名称为====>{}", event.getFromId(), event.getFromNick());
        } else {
            event.reject(false);
        }

    }

    /**
     * 好友输入状态处理
     * @param event 好友消息输入状态改变事件
     */
    @EventHandler
    @Override
    public void onFriendInputMessage(FriendInputStatusChangedEvent event) {

    }

    /**
     * 好友chatgpt聊天
     * @param event 好友消息事件
     */
    public void friendChat(FriendMessageEvent event) {
        sendFriendMsg(event);
    }

    /**
     * 陌生人消息处理
     * @param event 陌生人消息事件
     */
    @EventHandler
    @Override
    public void onStrangerMessage(StrangerMessageEvent event) {
        MessageSender.sendMessage(event.getSender(), "添加bot为好友后才可以聊天喵");
    }

    /**
     * 回复群组成员消息
     * @param event 群组消息事件
     */
    public void sendGroupMsg(GroupMessageEvent event) {
        Group group = event.getGroup();
        Member sender = event.getSender();
        MessageChain messageChain = event.getMessage();
        String message = ChatUtil.getRealMsg(messageChain.contentToString());
        if (CharSequenceUtil.isBlank(message)) {
            return;
        }
        CommandTypeEnum command = CommandRecognizer.getCommandTypeByMsg(messageChain.contentToString());
        String groupCacheKey = CacheKeyBuilder.buildGroupKey(group.getId(), sender.getId());
        String result;
        switch (command) {
            case SYS_MSG_SET:
                saveCache(groupCacheKey, new ChatCache(message, null, message.length(), true));
                result = "好的喵~";
                break;
            case CLEAR_ALL:
                chatHistoryService.clearHistory(groupCacheKey, CacheClearStrategyEnum.ALL);
                result = "清除成功喵~";
                break;
            case CLEAR_CHAT:
                chatHistoryService.clearHistory(groupCacheKey, CacheClearStrategyEnum.ALL_WITHOUT_SYS);
                result = "清除成功喵~";
                break;
            case NORMAL:
                result = getResult(groupCacheKey, messageChain, !(sender instanceof AnonymousMember));
                break;
            default:
                result = "bot好像坏掉了喵~ 请向管理员v50解决问题";
        }
        GroupMessageSender.sendQuoteAtMsg(group, sender, messageChain, result);
    }

    public void sendFriendMsg(FriendMessageEvent event) {
        Friend friend = event.getSender();
        MessageChain messageChain = event.getMessage();
        String friendCacheKey = CacheKeyBuilder.buildFriendKey(friend.getId());
        String result = getResult(friendCacheKey, messageChain, true);
        if (null == result) {
            return;
        }
        MessageSender.sendMessage(friend, result);
    }

    private String getResult(String cacheKey, MessageChain messageChain, boolean isSaveCache) throws YukiBotException {
        String message = messageChain.contentToString();
        String result;
        message = ChatUtil.getRealMsg(message);
        if (CharSequenceUtil.isBlank(message)) {
            return null;
        }
        List<ChatMessageCache> historyList = chatHistoryService.clearHistory(cacheKey, CacheClearStrategyEnum.FARTHEST_HALF);

        List<ChatMessage> clearedHistoryList = BeanUtil.copyToList(historyList, ChatMessage.class);

        ChatMessage chatMessage = new ChatMessage(RoleEnum.USER.getRole(), message);
        clearedHistoryList.add(chatMessage);
        int totalTokens = 0;
        try {
            ChatCompletionResponse chat = chatGPTApi.chat(clearedHistoryList);
            result = chat.getChoices().get(0).getMessage().getContent();
            totalTokens = chat.getUsage().getTotal_tokens();
        } catch (YukiBotException e) {
            log.error(e.getMessage());
            result = e.getMessage();
        }
        if (isSaveCache) {
            saveCache(cacheKey, new ChatCache(message, result, totalTokens, false));
        }
        return result;
    }

    /**
     * 保存群聊聊天记录
     *
     * @param cacheKey  聊天记录key
     * @param chatCache 聊天缓存实体
     */
    public void saveCache(String cacheKey, ChatCache chatCache) {
        List<ChatMessageCache> msgHistoryList = chatHistoryService.getMsgHistoryList(cacheKey);
        String cache = getJsonStrCache(chatCache, msgHistoryList);
        chatHistoryService.saveMsgHistory(cacheKey, cache);
    }

    private static String getJsonStrCache(ChatCache chatCache, List<ChatMessageCache> msgHistoryList) {
        ChatMessageCache user = new ChatMessageCache(RoleEnum.USER.getRole(), chatCache.getUserMsg(), chatCache.getToken());
        ChatMessageCache system = new ChatMessageCache(RoleEnum.SYSTEM.getRole(), chatCache.getUserMsg(), chatCache.getToken());
        ChatMessageCache assistant = new ChatMessageCache(RoleEnum.ASSISTANT.getRole(), chatCache.getChatMsg(), chatCache.getToken());
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
