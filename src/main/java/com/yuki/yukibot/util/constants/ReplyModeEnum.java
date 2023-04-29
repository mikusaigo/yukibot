package com.yuki.yukibot.util.constants;

public enum ReplyModeEnum {

    QUOTE_REPLY("QUOTE_REPLY", "all"),

    AT("AT", "group"),

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
