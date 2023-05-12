package com.yuki.yukibot.model.command;

import com.yuki.yukibot.util.enums.CommandTypeEnum;
import com.yuki.yukibot.util.enums.DurationUnit;

public class Mute extends Command{

    private int duration;

    private DurationUnit unit;

    public Mute(Long targetId, int duration, DurationUnit unit) {
        super(CommandTypeEnum.MUTE, targetId);
        this.duration = duration;
        this.unit = unit;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public DurationUnit getUnit() {
        return unit;
    }

    public void setUnit(DurationUnit unit) {
        this.unit = unit;
    }
}
