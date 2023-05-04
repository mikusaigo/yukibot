package com.yuki.yukibot.util;

import com.yuki.yukibot.exception.YukiBotException;
import com.yuki.yukibot.util.enums.YukiBotRespStateEnum;

public class ResponseHandler {

    public static void handleRespByState(int state){
        if (state == YukiBotRespStateEnum.OK.getCode()){
            return;
        }
        YukiBotRespStateEnum[] values = YukiBotRespStateEnum.values();
        for (YukiBotRespStateEnum value : values) {
            if (value.getCode() == state){
                throw new YukiBotException(value);
            }
        }
    }
}
