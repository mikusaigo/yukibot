package com.yuki.yukibot.exception;

import com.yuki.yukibot.util.enums.YukiBotRespStateEnum;

/**
 * 配置文件有错异常
 */
public class ConfigurationException extends YukiBotException{

    public ConfigurationException(YukiBotRespStateEnum exceptionEnum) {
        super(exceptionEnum);
    }
}
