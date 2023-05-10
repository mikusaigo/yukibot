package com.yuki.yukibot.util;

import cn.hutool.core.util.StrUtil;
import com.yuki.yukibot.exception.WrongDurationException;
import com.yuki.yukibot.model.Duration;
import com.yuki.yukibot.util.constants.DurationConstants;
import com.yuki.yukibot.util.enums.DurationUnit;
import com.yuki.yukibot.util.enums.YukiBotRespStateEnum;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationFormat {

    public static int getIntDurationSeconds(String duration){
        return formatDurationStr(duration).getDuration();
    }


    public static Duration formatDurationStr(String duration){
        if (StrUtil.isBlank(duration)){
            return new Duration(0, DurationUnit.SECONDS);
        }

        Pattern pattern = Pattern.compile("^(\\d+)(秒|分钟|小时|天|周|月)$");
        Matcher matcher = pattern.matcher(duration.trim());
        if (matcher.find()){
            System.out.println(matcher.groupCount());
            String time = matcher.group(1);
            String unit = matcher.group(2);
            int timeNum = Integer.parseInt(time);
            if (timeNum < 0) timeNum = 0;
            switch (unit){
                case "秒":
                    if (timeNum > DurationConstants.ONE_MONTH) timeNum = DurationConstants.ONE_MONTH;
                    return new Duration(timeNum, DurationUnit.SECONDS);
                case "分钟":
                    if (timeNum > DurationConstants.ONE_MONTH / 60) timeNum = DurationConstants.ONE_MONTH / 60;
                    return new Duration(timeNum * 60, DurationUnit.MINUTES);
                case "小时":
                    if (timeNum > DurationConstants.ONE_MONTH / 3600) timeNum = DurationConstants.ONE_MONTH / 3600;
                    return new Duration(timeNum * 60 * 60, DurationUnit.HOURS);
                case "天":
                    if (timeNum > 30) timeNum = 30;
                    return new Duration(timeNum * 60 * 60 * 24, DurationUnit.DAYS);
                case "周":
                    if (timeNum > 4) timeNum = 4;
                    return new Duration(timeNum * 60 * 60 * 24 * 7, DurationUnit.WEEKS);
                case "月":
                    return new Duration(DurationConstants.ONE_MONTH, DurationUnit.MONTH);
                default:
                    throw new WrongDurationException(YukiBotRespStateEnum.WRONG_DURATION);
            }
        }
        throw new WrongDurationException(YukiBotRespStateEnum.WRONG_DURATION);
    }

}
