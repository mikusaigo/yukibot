package com.yuki.yukibot.util.enums;

public enum YukiBotRespStateEnum {

    OK(200, "成功喵"),

    TOO_MANY_REQUESTS(429, "yuki忙不过来喵，请稍后再试");

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
