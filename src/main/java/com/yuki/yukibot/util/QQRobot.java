package com.yuki.yukibot.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.api.ChatGPTApi;
import com.yuki.yukibot.model.chatgpt.ChatMessage;
import com.yuki.yukibot.util.constants.BotConstants;
import com.yuki.yukibot.util.constants.ChatConstants;
import kotlin.coroutines.CoroutineContext;
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
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class QQRobot extends SimpleListenerHost {

    private static final ChatGPTApi chatGPTApi = new ChatGPTApi();

    public static final Map<Group, Map<Member,List<ChatMessage>>> MEMBER_CHAT_CACHE = new HashMap<>();

    public static final Map<Long, Boolean> AUTHORIZATION_GROUP = new HashMap<>();

    static {
        AUTHORIZATION_GROUP.put(992825862L, Boolean.TRUE);
        AUTHORIZATION_GROUP.put(917016978L, Boolean.TRUE);
        AUTHORIZATION_GROUP.put(694908428L, Boolean.TRUE);
    }

    public static void main(String[] args) {
        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
        botConfiguration.setAutoReconnectOnForceOffline(true);
        botConfiguration.setLoginCacheEnabled(true);
        // 创建机器人实例

        Bot bot = BotFactory.INSTANCE.newBot(BotConstants.BOT_QQ, BotConstants.BOT_PASSWORD, botConfiguration);
        // 注册事件监听器
        bot.getEventChannel().registerListenerHost(new QQRobot());
        // 启动机器人
        bot.login();
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onGroupMessage(GroupMessageEvent event){
        Group group = event.getGroup();
        if (Boolean.TRUE.equals(AUTHORIZATION_GROUP.get(group.getId()))){
            MessageChain message = event.getMessage();
            MessageContent messageContent = message.get(MessageContent.Key);
            if (messageContent instanceof At){
                At at = (At) messageContent;
                if (at.getTarget() == BotConstants.BOT_QQ){
                    String prompt = message.contentToString();
                    clearCache(event, prompt);
                    sendMsg(event, prompt);
                }
            }
        }
    }

    public void sendMsg(GroupMessageEvent event, String message){
        Group group = event.getGroup();
        Member sender = event.getSender();
        String result;
        try {
            ChatMessage chatMessage = new ChatMessage(RoleEnum.USER.getRole(), message);
            List<ChatMessage> historyMsg = getHistoryMsg(group, sender);
            if (CollUtil.isEmpty(historyMsg)){
                result = chatGPTApi.chat(Collections.singletonList(chatMessage));
            }else {
                historyMsg.add(chatMessage);
                result = chatGPTApi.chat(historyMsg);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            result = "网络不太好，请稍后重试";
        }
        System.out.println(result);
        MessageChainBuilder builder = new MessageChainBuilder()
                .append(new QuoteReply(event.getMessage()))
                .append(new PlainText(StrPool.C_SPACE + result));
        // 发送回复消息
        try {
            event.getGroup().sendMessage(builder.build());
            saveCache(event.getGroup(), event.getSender(), message, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }

    /**
     * 保存聊天记录
     * @param group 群组
     * @param member 群员
     * @param userMsg 用户消息
     * @param chatGptMsg chatGpt消息
     */
    public static void saveCache(Group group, Member member, String userMsg, String chatGptMsg){
        Map<Member, List<ChatMessage>> memberListMap = MEMBER_CHAT_CACHE.get(group);
        ChatMessage user = new ChatMessage(RoleEnum.USER.getRole(), userMsg);
        ChatMessage assistant = new ChatMessage(RoleEnum.ASSISTANT.getRole(), chatGptMsg);
        if (CollUtil.isEmpty(memberListMap)){
            Map<Member, List<ChatMessage>> groupChatMap = new HashMap<>();
            List<ChatMessage> memberMsgList = new LinkedList<>();
            memberMsgList.add(user);
            memberMsgList.add(assistant);
            groupChatMap.put(member, memberMsgList);
            MEMBER_CHAT_CACHE.put(group, groupChatMap);
            return;
        }
        List<ChatMessage> chatMessages = memberListMap.get(member);

        if (CollUtil.isEmpty(chatMessages)){
            List<ChatMessage> memberMsgList = new LinkedList<>();
            memberMsgList.add(user);
            memberMsgList.add(assistant);
            memberListMap.put(member, memberMsgList);
            return;
        }
        if (chatMessages.size() >= ChatConstants.MAX_ONCE_CHAT_TIME){
            log.info("目前缓存了的数据====>{}", JSONUtil.toJsonStr(MEMBER_CHAT_CACHE));
            chatMessages.clear();
        }
        chatMessages.add(user);
        chatMessages.add(assistant);

    }

    public static List<ChatMessage> getHistoryMsg(Group group, Member member){
        Map<Member, List<ChatMessage>> memberListMap = MEMBER_CHAT_CACHE.get(group);
        if (CollUtil.isEmpty(memberListMap)){
            return null;
        }
        return memberListMap.get(member);
    }

    public static void clearCache(GroupMessageEvent event, String msg){
        if ("end".equalsIgnoreCase(msg) || "结束".equals(msg)){
            List<ChatMessage> historyMsg = getHistoryMsg(event.getGroup(), event.getSender());
            if (CollUtil.isEmpty(historyMsg)){
                return;
            }
            historyMsg.clear();
        }
    }
}
