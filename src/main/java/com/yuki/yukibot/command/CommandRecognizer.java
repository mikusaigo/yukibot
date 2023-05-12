package com.yuki.yukibot.command;

import com.yuki.yukibot.model.command.Command;
import com.yuki.yukibot.model.command.Mute;
import com.yuki.yukibot.util.constants.CommandPatternConstants;
import com.yuki.yukibot.util.enums.ChatCommandTypeEnum;
import com.yuki.yukibot.util.enums.CommandTypeEnum;
import com.yuki.yukibot.util.enums.DurationUnit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 指令识别器，用于识别用户发来的消息
 */
public class CommandRecognizer {

    /**
     * 通过发来的消息识别是什么指令，如果不是则返回正常对话
     * @param message 用户消息
     * @return 指令类别
     */
    public static ChatCommandTypeEnum recognizeChatCommand(String message){
        Pattern sysPattern = Pattern.compile(CommandPatternConstants.SYS_MSG_SET);
        Pattern clearAllPattern = Pattern.compile(CommandPatternConstants.CACHE_CLEAR_ALL);
        Pattern clearChatPattern = Pattern.compile(CommandPatternConstants.CACHE_CLEAR_CHAT);

        Matcher sysMatcher = sysPattern.matcher(message);
        Matcher clearAllMatcher = clearAllPattern.matcher(message);
        Matcher clearChatMatcher = clearChatPattern.matcher(message);
        if (sysMatcher.find()){
            return ChatCommandTypeEnum.SYS_MSG_SET;
        }
        if (clearAllMatcher.find()){
            return ChatCommandTypeEnum.CLEAR_ALL;
        }
        if (clearChatMatcher.find()){
            return ChatCommandTypeEnum.CLEAR_CHAT;
        }
        return ChatCommandTypeEnum.NORMAL;
    }

    public static Command recognizeCommand(String message){
        if (CommandTypeEnum.equals(CommandTypeEnum.MUTE_ALL, message)){
            return new Command(CommandTypeEnum.MUTE_ALL);
        }
        if (CommandTypeEnum.equals(CommandTypeEnum.UN_MUTE_ALL, message)){
            return new Command(CommandTypeEnum.UN_MUTE_ALL);
        }

        Pattern mutePattern = Pattern.compile(CommandPatternConstants.MUTE);

        Matcher matcher = mutePattern.matcher(message);

        if (matcher.find()){
            String command = matcher.group(2);
            String memberId = matcher.group(1);
            Long targetId = Long.parseLong(memberId);
            if (CommandTypeEnum.equals(CommandTypeEnum.MUTE, command)){
                String timeNum = matcher.group(3);
                String durationUnit = matcher.group(4);
                return new Mute(targetId, Integer.parseInt(timeNum), DurationUnit.get(durationUnit));
            }
            if (CommandTypeEnum.equals(CommandTypeEnum.UN_MUTE, command)){
                return new Command(CommandTypeEnum.UN_MUTE, targetId);
            }
        }
        return null;
    }
}
