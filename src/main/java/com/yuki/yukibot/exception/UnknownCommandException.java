package com.yuki.yukibot.exception;

import com.yuki.yukibot.util.enums.YukiBotRespStateEnum;

public class UnknownCommandException extends YukiBotException{

    public UnknownCommandException(){
        super(YukiBotRespStateEnum.UNKNOWN_COMMAND);
    }

    public UnknownCommandException(YukiBotRespStateEnum exceptionEnum) {
        super(exceptionEnum);
    }
}
