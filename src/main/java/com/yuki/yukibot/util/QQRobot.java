package com.yuki.yukibot.util;

import cn.hutool.core.text.CharSequenceUtil;
import com.yuki.yukibot.YukibotApplication;
import com.yuki.yukibot.api.ChatGPTApi;
import com.yuki.yukibot.model.chatgpt.ExecuteRet;
import com.yuki.yukibot.util.constants.BotConstants;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageContent;
import net.mamoe.mirai.message.data.MessageKey;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

@Component
public class QQRobot extends SimpleListenerHost {

    private static final ChatGPTApi chatGPTApi = new ChatGPTApi();


    public static void main(String[] args) {
        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH);
        botConfiguration.setAutoReconnectOnForceOffline(true);
        botConfiguration.setLoginCacheEnabled(true);
        // 创建机器人实例

        Bot bot = BotFactory.INSTANCE.newBot(BotConstants.BOT_QQ, BotConstants.BOT_PASSWORD, botConfiguration);
        // 注册事件监听器
        bot.getEventChannel().registerListenerHost(new QQRobot());
        // 启动机器人
        bot.login();
    }

    @EventHandler
    public void onGroupMessage(GroupMessageEvent event){
        Group group = event.getGroup();
        if (group.getId() == BotConstants.LISTEN_GROUP){
            Member sender = event.getSender();
            MessageChain message = event.getMessage();
            MessageContent messageContent = message.get(MessageContent.Key);
            if (messageContent instanceof At){
                At at = (At) messageContent;
                if (at.getTarget() == BotConstants.BOT_QQ){
                    String prompt = message.contentToString();
                    System.out.println(prompt);
                    sendMsg(event, prompt);
                }
            }
        }
    }

    public void sendMsg(GroupMessageEvent event, String message){
        String result = chatGPTApi.chat(message);
        System.out.println(result);
        MessageChainBuilder builder = new MessageChainBuilder()
                .append(new At(event.getSender().getId()))
                .append(new PlainText(result));
        // 发送回复消息
        event.getGroup().sendMessage(builder.build());
    }

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }
}
