package com.yuki.yukibot.exception;

import com.yuki.yukibot.util.enums.YukiBotRespStateEnum;

public class WrongDurationException extends YukiBotException{
    public WrongDurationException(YukiBotRespStateEnum exceptionEnum) {
        super(exceptionEnum);
    }
}
