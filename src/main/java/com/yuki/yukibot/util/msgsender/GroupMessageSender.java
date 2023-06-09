package com.yuki.yukibot.util.msgsender;

import cn.hutool.core.text.StrPool;
import com.yuki.yukibot.util.enums.ReplyModeEnum;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Dice;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;

/**
 * 发送qq群消息
 */
@Slf4j
public class GroupMessageSender extends MessageSender {


    /**
     * 发送普通群信息
     *
     * @param group   要发送消息的群
     * @param message 要发送的消息
     */
    public static void sendMsg(Group group, String message) {
        MessageChain reply = new MessageChainBuilder()
                .append(new PlainText(message))
                .build();
        group.sendMessage(reply);
    }

    public static void dicing(Group group){
        MessageChain diceReply = new MessageChainBuilder()
                .append(Dice.random())
                .build();
        group.sendMessage(diceReply);
    }

    /**
     * 发送群消息指定群成员
     *
     * @param group 要发送消息的群
     * @param member 要发送的消息
     * @param replyMode 要回复的消息的类型 （AT：@消息， QUOTE_REPLY: qq回复消息，ALL：qq回复+@）
     * @param message 发送者发送的消息
     * @param reply 回复的消息
     */
    public static boolean sendMsgToMember(Group group, Member member, String replyMode, MessageChain message, String reply) {
        MessageChainBuilder replyBuilder = new MessageChainBuilder();
        MessageChain replyChain;
        try {
            if (ReplyModeEnum.AT.getMode().equals(replyMode)) {
                replyChain = replyBuilder.append(new At(member.getId()))
                        .append(new PlainText(StrPool.C_SPACE + reply))
                        .build();
            } else if (ReplyModeEnum.QUOTE_REPLY.getMode().equals(replyMode)) {
                replyChain = replyBuilder.append(new QuoteReply(message))
                        .append(new PlainText(StrPool.C_SPACE + reply))
                        .build();
            } else if (ReplyModeEnum.ALL.getMode().equals(replyMode)) {
                replyChain = replyBuilder.append(new QuoteReply(message))
                        .append(new At(member.getId()))
                        .append(new PlainText(StrPool.C_SPACE + reply))
                        .build();
            } else {
                log.error("invalid replyMode ===> {}", replyMode);
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        group.sendMessage(replyChain);
        return true;
    }

    public static boolean sendAtMsg(Group group, Member member, String reply){
        return sendMsgToMember(group, member, ReplyModeEnum.AT.getMode(), null, reply);
    }

    public static boolean sendQuoteMsg(Group group, Member member, MessageChain messages, String reply){
        return sendMsgToMember(group, member, ReplyModeEnum.QUOTE_REPLY.getMode(), messages, reply);
    }

    public static boolean sendQuoteAtMsg(Group group, Member member, MessageChain messages, String reply){
        return sendMsgToMember(group, member, ReplyModeEnum.ALL.getMode(), messages, reply);
    }
}
