package com.yuki.yukibot.util.enums;

public enum ChatCommandTypeEnum {

    SYS_MSG_SET("sys", "设定"),

    CLEAR_ALL("clearall", "清除所有"),

    CLEAR_CHAT("clearchat", "清除聊天"),

    NORMAL("noraml", "正常对话");

    final String enCode;

    final String cnCode;

    ChatCommandTypeEnum(String enCode, String cnCode) {
        this.enCode = enCode;
        this.cnCode = cnCode;
    }

    public String getEnCode() {
        return enCode;
    }

    public String getCnCode() {
        return cnCode;
    }
}
