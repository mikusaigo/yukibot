package com.yuki.yukibot.exception;

import com.yuki.yukibot.util.enums.YukiBotRespStateEnum;

public class YukiBotException extends RuntimeException{

    private final String message;

    @Override
    public String getMessage() {
        return message;
    }

    public YukiBotException(YukiBotRespStateEnum exceptionEnum) {
        this.message = exceptionEnum.getMessage();
    }
}
