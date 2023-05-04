package com.yuki.yukibot.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.api.ChatGPTApi;
import com.yuki.yukibot.config.QQBotConfig;
import com.yuki.yukibot.dao.ChatHistoryService;
import com.yuki.yukibot.exception.ConfigurationException;
import com.yuki.yukibot.exception.YukiBotException;
import com.yuki.yukibot.model.chatgpt.ChatMessage;
import com.yuki.yukibot.util.constants.QQBotAllowedModeConstants;
import com.yuki.yukibot.util.enums.RoleEnum;
import com.yuki.yukibot.util.enums.YukiBotRespStateEnum;
import com.yuki.yukibot.util.msgsender.GroupMessageSender;
import kotlin.coroutines.CoroutineContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.friendgroup.FriendGroup;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageContent;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * TODO: 将不同模式处理封装为返回布尔值
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QQRobot extends SimpleListenerHost {

    private final ChatGPTApi chatGPTApi;

    private final QQBotConfig qqBotConfig;

    private final ChatHistoryService chatHistoryService;

    @PostConstruct
    public void initBot() {
        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
        botConfiguration.setAutoReconnectOnForceOffline(true);
        botConfiguration.setLoginCacheEnabled(true);
//        botConfiguration.setDeviceInfo(bot -> DeviceInfo.from(new File("src/main/resources/device/device.json")));
        // 创建机器人实例

        Bot bot = BotFactory.INSTANCE.newBot(qqBotConfig.getId(), qqBotConfig.getPassword(), botConfiguration);
        // 注册事件监听器
        bot.getEventChannel().registerListenerHost(this);
        // 启动机器人
        bot.login();
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onGroupMessage(GroupMessageEvent event) {
        Group group = event.getGroup();
        if (QQBotAllowedModeConstants.ID.equals(qqBotConfig.getAllowedGroups().getMode())) {
            if (qqBotConfig.getAllowedGroups().getIds().contains(group.getId())) {
                groupChat(event);
            }
        } else {
            groupChat(event);
        }
    }

    private void groupChat(GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        MessageContent messageContent = message.get(MessageContent.Key);
        if (messageContent instanceof At) {
            At at = (At) messageContent;
            if (at.getTarget() == qqBotConfig.getId()) {
                sendMsg(event, message);
            }
        }
    }

    @EventHandler
    public void onFriendMessage(FriendMessageEvent event) {
        Friend friend = event.getSender();
        MessageChain message = event.getMessage();
        QQBotConfig.AllowedFriends allowedFriends = qqBotConfig.getAllowedFriends();
        String mode = allowedFriends.getMode();
        switch (mode) {
            case QQBotAllowedModeConstants.ID:
                List<Long> ids = allowedFriends.getIds();
                long id = friend.getId();
                if (ids.contains(id)) {
                    // TODO: 2023/5/4 处理配置是通过id
                    friend.sendMessage(chatGPTApi.chat(Collections.singletonList(new ChatMessage("user", message.contentToString()))));
                }
                break;
            case QQBotAllowedModeConstants.FRIEND_GROUP:
                Map<String, List<String>> friendGroups = allowedFriends.getFriendGroups();
                List<String> friendsGroupIds = friendGroups.get("ids");
                List<String> friendsGroupName = friendGroups.get("name");
                FriendGroup friendGroup = friend.getFriendGroup();
                if (friendsGroupIds.contains(String.valueOf(friendGroup.getId())) || friendsGroupName.contains(friendGroup.getName())) {
                    friend.sendMessage(chatGPTApi.chat(Collections.singletonList(new ChatMessage("user", message.contentToString()))));
                }
            case QQBotAllowedModeConstants.UNLIMITED:
                break;
            default:
                throw new ConfigurationException(YukiBotRespStateEnum.INVALID_ALLOWED_MODE);
        }

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

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        log.error(exception.getMessage());
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
