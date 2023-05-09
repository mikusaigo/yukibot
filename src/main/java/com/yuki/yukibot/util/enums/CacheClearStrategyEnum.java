package com.yuki.yukibot.util.enums;

/**
 * 缓存清理策略枚举
 */
public enum CacheClearStrategyEnum {

    ALL("ALL", "删除所有记录"),

    ALL_WITHOUT_SYS("ALL_WITHOUT_SYS", "删除除系统设定外的所有记录"),

    FARTHEST_HALF("FARTHEST_HALF", "从最远记录开始折半");

    final String code;

    final String name;

    CacheClearStrategyEnum(java.lang.String code, java.lang.String name) {
        this.code = code;
        this.name = name;
    }

    public java.lang.String getCode() {
        return code;
    }

    public java.lang.String getName() {
        return name;
    }
}
