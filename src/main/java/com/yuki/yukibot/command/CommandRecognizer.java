package com.yuki.yukibot.command;

import com.yuki.yukibot.util.constants.CommandPatternConstants;
import com.yuki.yukibot.util.enums.CommandTypeEnum;

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
    public static CommandTypeEnum getCommandTypeByMsg(String message){
        Pattern sysPattern = Pattern.compile(CommandPatternConstants.SYS_MSG_SET);
        Pattern clearAllPattern = Pattern.compile(CommandPatternConstants.CACHE_CLEAR_ALL);
        Pattern clearChatPattern = Pattern.compile(CommandPatternConstants.CACHE_CLEAR_CHAT);

        Matcher sysMatcher = sysPattern.matcher(message);
        Matcher clearAllMatcher = clearAllPattern.matcher(message);
        Matcher clearChatMatcher = clearChatPattern.matcher(message);
        if (sysMatcher.find()){
            return CommandTypeEnum.SYS_MSG_SET;
        }
        if (clearAllMatcher.find()){
            return CommandTypeEnum.CLEAR_ALL;
        }
        if (clearChatMatcher.find()){
            return CommandTypeEnum.CLEAR_CHAT;
        }
        return CommandTypeEnum.NORMAL;
    }
}
