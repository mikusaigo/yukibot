package com.yuki.yukibot.util.enums;

import com.yuki.yukibot.model.chatgpt.ChatMessage;

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

    public static boolean isSysRoleMsg(ChatMessage message){
        return SYSTEM.getRole().equals(message.getRole());
    }

    public static boolean isUserRoleMsg(ChatMessage message){
        return USER.getRole().equals(message.getRole());
    }

    public static boolean isAssistantRoleMsg(ChatMessage message){
        return ASSISTANT.getRole().equals(message.getRole());
    }
}
