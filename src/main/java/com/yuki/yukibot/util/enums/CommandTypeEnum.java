package com.yuki.yukibot.util.enums;

/**
 * 指令类型枚举
 */
public enum CommandTypeEnum {

    MUTE("mute", "禁言"),

    UN_MUTE("unmute", "取消禁言"),

    MUTE_ALL("muteall", "全体禁言"),

    UN_MUTE_ALL("unmuteall", "全员禁言取消");

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

    public static boolean equals(CommandTypeEnum typeEnum, String command){
        return typeEnum.getCnCode().equals(command) || typeEnum.getEnCode().equals(command);
    }
}
