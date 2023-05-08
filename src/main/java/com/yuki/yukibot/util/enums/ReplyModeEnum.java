package com.yuki.yukibot.util.enums;

/**
 * bot回复消息模式枚举
 */
public enum ReplyModeEnum {

    // qq回复模式 范围：群组，好友
    QUOTE_REPLY("QUOTE_REPLY", "all"),

    // qqAt模式 范围：群组
    AT("AT", "group"),

    // qq回复+At模式 范围：群组
    ALL("ALL", "group");

    private final String mode;

    private final String range;

    ReplyModeEnum(String mode, String range) {
        this.mode = mode;
        this.range = range;
    }

    public String getMode() {
        return mode;
    }

    public String getRange() {
        return range;
    }
}
