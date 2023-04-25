package com.yuki.yukibot.util;

import cn.hutool.core.text.CharSequenceUtil;
import com.yuki.yukibot.api.ChatGPTApi;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageContent;
import net.mamoe.mirai.message.data.MessageKey;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QQRobot extends SimpleListenerHost {

    @Autowired
    private ChatGPTApi chatGPTApi;

    // 机器人QQ号码
    private static final long BOT_QQ = 2074312668L;
    // 机器人QQ密码
    private static final String BOT_PASSWORD = "zkmgzMX2";
    // 监听的QQ号码
    private static final long LISTEN_QQ = 675400192L;
    // 监听的QQ群号码
    private static final long LISTEN_GROUP = 917016978L;
    // botname
    private static final String BOT_NAME = "yukibot";

    public static void main(String[] args) {
        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
        // 创建机器人实例

        Bot bot = BotFactory.INSTANCE.newBot(BOT_QQ, BOT_PASSWORD, botConfiguration);
        // 注册事件监听器
        bot.getEventChannel().registerListenerHost(new QQRobot());
        // 启动机器人
        bot.login();
    }


    @EventHandler
    public void onGroupMessage(GroupMessageEvent event){
        Group group = event.getGroup();
        if (group.getId() == LISTEN_GROUP){
            Member sender = event.getSender();
            MessageChain message = event.getMessage();
            MessageContent messageContent = message.get(At.Key);
            if (messageContent instanceof At){
                At at = (At) messageContent;
                System.out.println(at.getTarget());
                if (at.getTarget() == BOT_QQ){
                    sendMsg(event, CharSequenceUtil.format("被{}@了", sender.getId()));
                }
            }
        }
    }

    public void sendMsg(GroupMessageEvent event, String message){
        MessageChainBuilder builder = new MessageChainBuilder()
                .append(new PlainText(message));
        // 发送回复消息
        event.getGroup().sendMessage(builder.build());
    }


}
