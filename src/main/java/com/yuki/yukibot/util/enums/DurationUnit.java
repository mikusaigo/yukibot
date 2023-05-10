package com.yuki.yukibot.util.enums;

public enum DurationUnit {

    SECONDS("秒"),

    MINUTES("分钟"),

    HOURS("小时"),

    DAYS("天"),

    WEEKS("周"),

    MONTH("月");

    final String name;

    DurationUnit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
