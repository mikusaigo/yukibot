package com.yuki.yukibot.util.constants;

public class ChatConstants {

    public static final int MAX_TOKEN = 2048;

    // 用户对话的记录数上限（不包含sys设定记录）
    public static final int MAX_ONCE_CHAT_TIME = 20;

    // 单次清理的数据数量
    public static final int REMOVE_HISTORY_SIZE = MAX_ONCE_CHAT_TIME / 2;
}
