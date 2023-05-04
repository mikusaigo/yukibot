package com.yuki.yukibot.util.enums;

public enum YukiBotRespStateEnum {

    OK(200, "成功喵"),

    TOO_MANY_REQUESTS(429, "yuki忙不过来喵，请稍后再试"),

    INVALID_ALLOWED_MODE(601, "不合法的QQBot开放的QQ群组或QQ好友过滤模式配置");

    private final int code;

    private final String message;

    YukiBotRespStateEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
