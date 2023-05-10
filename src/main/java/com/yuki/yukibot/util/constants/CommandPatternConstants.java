package com.yuki.yukibot.util.constants;

/**
 * 指令校验正则表达式常量类
 */
public class CommandPatternConstants {

    // 校验指令消息是否为系统设定
    public static final String SYS_MSG_SET = "^(@\\d{5,}\\s?)?(sys|设定)[:：]?\\s?";

    // 校验指令消息是否为清除所有历史记录缓存
    public static final String CACHE_CLEAR_ALL = "^(@\\d{5,}\\s?)?(清除所有|clearall)$";

    // 校验指令消息是否为清除历史记录缓存，但保留系统设定
    public static final String CACHE_CLEAR_CHAT = "^(@\\d{5,}\\s?)?(清除聊天|clearchat)$";

    // 校验指令消息是否为禁言
    public static final String MUTE = "^(禁言|mute)";

    // 校验指令消息是否为取消禁言
    public static final String UN_MUTE = "^(取消禁言|unmute)";

    // 校验指令消息是否为全员禁言
    public static final String MUTE_ALL = "^(全员禁言|muteall)$";

    // 校验指令消息是否为取消全员禁言
    public static final String UN_MUTE_ALL = "^(取消全员禁言|unmuteall)$";


}
