package com.yuki.yukibot.model.command;

import com.yuki.yukibot.util.enums.CommandTypeEnum;

public class Command {

    private CommandTypeEnum type;

    private Long targetId;

    public Command() {
    }

    public Command(CommandTypeEnum type) {
        this.type = type;
    }

    public Command(CommandTypeEnum type, Long targetId) {
        this.type = type;
        this.targetId = targetId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public CommandTypeEnum getType() {
        return type;
    }

    public void setType(CommandTypeEnum type) {
        this.type = type;
    }
}
