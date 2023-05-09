package com.yuki.yukibot.util.enums;

/**
 * yukibot系统响应码枚举
 */
public enum YukiBotRespStateEnum {

    OK(200, "成功喵"),

    TOO_MANY_REQUESTS(429, "yuki忙不过来喵，请稍后再试"),

    TOO_LONG_REQUEST(400, "被灌注太多了喵，请尝试@bot并输入清理全部"),

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
