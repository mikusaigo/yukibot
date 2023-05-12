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
    public static final String MUTE = "^@(\\d{5,})\\s+(禁言|mute|解除禁言|unmute)\\s?(?:(\\d+)(秒|分钟|小时|天|周|月))?$";

    // 校验指令消息是否为解除禁言
    public static final String UN_MUTE = "^@\\d{5,}\\s+(解除禁言|unmute)\\s+@\\d{5,}";

    // 校验指令消息是否为全员禁言
    public static final String MUTE_ALL = "^(全员禁言|muteall|取消全员禁言|unmuteall)$";

    // 校验指令消息是否为取消全员禁言
    public static final String UN_MUTE_ALL = "^(取消全员禁言|unmuteall)$";


}
