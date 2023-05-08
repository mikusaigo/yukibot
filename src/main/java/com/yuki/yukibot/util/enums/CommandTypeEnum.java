package com.yuki.yukibot.util.enums;

/**
 * 指令类型枚举
 */
public enum CommandTypeEnum {

    SYS_MSG_SET("sys", "设定"),

    NORMAL("normal", "正常对话"),

    CLEAR_ALL("clearall", "清除所有"),

    CLEAR_CHAT("clearchat", "清除聊天");

    final String enCode;

    final String cnCode;

    CommandTypeEnum(String enCode, String cnCode) {
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
