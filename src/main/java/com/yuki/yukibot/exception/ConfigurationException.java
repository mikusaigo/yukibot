package com.yuki.yukibot.exception;

import com.yuki.yukibot.util.enums.YukiBotRespStateEnum;

public class ConfigurationException extends YukiBotException{

    public ConfigurationException(YukiBotRespStateEnum exceptionEnum) {
        super(exceptionEnum);
    }
}
