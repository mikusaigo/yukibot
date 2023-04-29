package com.yuki.yukibot.util;

public enum RoleEnum {

    SYSTEM("system", "系统"),

    USER("user", "用户"),

    ASSISTANT("assistant", "助手");

    private final String role;

    private final String name;

    RoleEnum(String role, String name) {
        this.role = role;
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }
}
