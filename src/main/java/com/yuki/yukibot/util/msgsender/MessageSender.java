package com.yuki.yukibot.util.msgsender;

import net.mamoe.mirai.contact.Stranger;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;

/**
 * 发送QQ消息用
 */
public class MessageSender {

    public static void sendMessage(User user, String message){
        user.sendMessage(message);
    }

    public static void sendQuoteMsg(User user, MessageChain originalMsg, String reply){
        MessageChain replyMsg = new MessageChainBuilder()
                .append(new QuoteReply(originalMsg))
                .append(new PlainText(reply))
                .build();
        user.sendMessage(replyMsg);
    }
}
