package com.yuki.yukibot.util;

import cn.hutool.core.util.StrUtil;
import com.yuki.yukibot.exception.WrongDurationException;
import com.yuki.yukibot.model.Duration;
import com.yuki.yukibot.model.command.Mute;
import com.yuki.yukibot.util.constants.DurationConstants;
import com.yuki.yukibot.util.enums.DurationUnit;
import com.yuki.yukibot.util.enums.YukiBotRespStateEnum;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationFormat {

    public static int getIntDurationSeconds(Mute mute){
        return getDuration(mute.getDuration(), mute.getUnit().getName()).getDuration();
    }


    public static Duration formatDurationStr(String duration){
        if (StrUtil.isBlank(duration)){
            return new Duration(0, DurationUnit.SECONDS);
        }

        Pattern pattern = Pattern.compile("(\\d+)(秒|分钟|小时|天|周|月)$");
        Matcher matcher = pattern.matcher(duration.trim());
        if (matcher.find()){
            String time = matcher.group(1);
            String unit = matcher.group(2);
            int timeNum = Integer.parseInt(time);
            if (timeNum < 0) timeNum = 0;
            return getDuration(timeNum, unit);
        }
        throw new WrongDurationException(YukiBotRespStateEnum.WRONG_DURATION);
    }

    @NotNull
    private static Duration getDuration(int timeNum, String unit) {
        switch (unit){
            case "秒":
                if (timeNum > DurationConstants.ONE_MONTH) timeNum = DurationConstants.ONE_MONTH;
                return new Duration(timeNum, DurationUnit.SECONDS);
            case "分钟":
                if (timeNum > DurationConstants.ONE_MONTH / 60) timeNum = DurationConstants.ONE_MONTH / 60;
                return new Duration(timeNum * DurationConstants.ONE_MINUTE, DurationUnit.MINUTES);
            case "小时":
                if (timeNum > DurationConstants.ONE_MONTH / 3600) timeNum = DurationConstants.ONE_MONTH / 3600;
                return new Duration(timeNum * DurationConstants.ONE_HOUR, DurationUnit.HOURS);
            case "天":
                if (timeNum > 30) timeNum = 30;
                return new Duration(timeNum * DurationConstants.ONE_DAY, DurationUnit.DAYS);
            case "周":
                if (timeNum > 4) timeNum = 4;
                return new Duration(timeNum * DurationConstants.ONE_WEEK, DurationUnit.WEEKS);
            case "月":
                return new Duration(DurationConstants.ONE_MONTH, DurationUnit.MONTH);
            default:
                throw new WrongDurationException(YukiBotRespStateEnum.WRONG_DURATION);
        }
    }

}
