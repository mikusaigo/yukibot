package com.yuki.yukibot.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.api.ChatGPTApi;
import com.yuki.yukibot.dao.ChatHistoryService;
import com.yuki.yukibot.model.chatgpt.ChatMessage;
import com.yuki.yukibot.model.chatgpt.ChatUsage;
import com.yuki.yukibot.util.constants.BotConstants;
import com.yuki.yukibot.util.constants.ChatConstants;
import kotlin.coroutines.CoroutineContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageContent;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.DeviceInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class QQRobot extends SimpleListenerHost {

    private final ChatGPTApi chatGPTApi;

    protected static final Map<Group, Map<Member, List<ChatMessage>>> MEMBER_CHAT_CACHE = new HashMap<>();

    protected static final Map<Long, Boolean> AUTHORIZATION_GROUP = new HashMap<>();

    private final ChatHistoryService chatHistoryService;

    static {
        AUTHORIZATION_GROUP.put(992825862L, Boolean.TRUE);
        AUTHORIZATION_GROUP.put(917016978L, Boolean.TRUE);
        AUTHORIZATION_GROUP.put(694908428L, Boolean.TRUE);
    }

    @PostConstruct
    public void initBot() {
        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
        botConfiguration.setAutoReconnectOnForceOffline(true);
        botConfiguration.setLoginCacheEnabled(true);
        botConfiguration.setDeviceInfo(bot -> DeviceInfo.from(new File("src/main/resources/device/device.json")));
        // 创建机器人实例

        Bot bot = BotFactory.INSTANCE.newBot(BotConstants.BOT_QQ, BotConstants.BOT_PASSWORD, botConfiguration);
        // 注册事件监听器
        bot.getEventChannel().registerListenerHost(this);
        // 启动机器人
        bot.login();
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onGroupMessage(GroupMessageEvent event) {
        Group group = event.getGroup();
        if (Boolean.TRUE.equals(AUTHORIZATION_GROUP.get(group.getId()))) {
            MessageChain message = event.getMessage();
            MessageContent messageContent = message.get(MessageContent.Key);
            if (messageContent instanceof At) {
                At at = (At) messageContent;
                if (at.getTarget() == BotConstants.BOT_QQ) {
                    String prompt = message.contentToString();
                    sendMsg(event, prompt);
                }
            }
        }
    }

    public void sendMsg(GroupMessageEvent event, String message) {
        Group group = event.getGroup();
        Member sender = event.getSender();
        String result;
        boolean sysMsg = ChatUtil.isSysMsg(message);
        List<ChatMessage> msgHistoryList = chatHistoryService.getMsgHistoryList(group.getId(), sender.getId());
        List<ChatMessage> clearedHistoryList = chatHistoryService.clearHistory(group.getId(), sender.getId(), msgHistoryList);
        if (sysMsg) {
            message = ChatUtil.getRealMsg(message, true);
            if (CharSequenceUtil.isNotBlank(message)) {
                GroupMessageSender.sendQuoteAtMsg(group, sender, event.getMessage(), "好的");
                saveCache(group, sender, message, null, true);
            }
            return;
        } else {
            try {
                ChatMessage chatMessage = new ChatMessage(RoleEnum.USER.getRole(), message);
                clearedHistoryList.add(chatMessage);
                result = chatGPTApi.chat(clearedHistoryList);
                saveCache(group, sender, message, result, false);
            } catch (RuntimeException e) {
                e.printStackTrace();
                result = "网络不太好，请稍后重试";
            }
        }
        GroupMessageSender.sendQuoteAtMsg(group, sender, event.getMessage(), result);

    }

    public void sendNormalMsg(GroupMessageEvent event, String message) {
        MessageChainBuilder builder = new MessageChainBuilder()
                .append(new QuoteReply(event.getMessage()))
                .append(new PlainText(StrPool.C_SPACE + message));
        event.getGroup().sendMessage(builder.build());
    }

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }

    /**
     * 保存聊天记录
     *
     * @param group      群组
     * @param member     群员
     * @param userMsg    用户消息
     * @param chatGptMsg chatGpt消息
     */
    public void saveCache(Group group, Member member, String userMsg, String chatGptMsg, boolean isSysMsg) {
        List<ChatMessage> msgHistoryList = chatHistoryService.getMsgHistoryList(group.getId(), member.getId());
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
        chatHistoryService.saveMsgHistory(group.getId(), member.getId(), cache);

    }

    public static void clearCache(GroupMessageEvent event, String msg) {

    }

    public static String getRealContent(String message) {
        return null;
    }
}
