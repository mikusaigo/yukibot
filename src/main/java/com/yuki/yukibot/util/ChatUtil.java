package com.yuki.yukibot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {

    public static boolean isSysMsg(String message){
        String trimMsg = message.trim();
        String sysPattern = "^@\\d{5,}\\s?sys[：:]\\s?";
        Pattern pattern = Pattern.compile(sysPattern);
        Matcher matcher = pattern.matcher(trimMsg);
        return matcher.find();
    }

    public static boolean getClearType(String message){
        String trimMsg = message.trim();
        String sysPattern = "^@\\d{5,}\\s?[清除缓存|clear](\\s[全部|all])?$";
        Pattern pattern = Pattern.compile(sysPattern);
        Matcher matcher = pattern.matcher(trimMsg);
        return matcher.find();
    }

    public static String getRealMsg(String message, boolean isSysMsg){
//        String pattern = "\\[mirai:[a-z]+:\\d{5,}]\\s+(.*)";
        String pattern = "^@\\d{5,}\\s?(.*)";
        if (isSysMsg){
//            pattern = "\\[mirai:[a-z]+:\\d{5,}]\\ssys[:：]?\\s+(.*)";
            pattern = "^@\\d{5,}\\s?sys[:：]?\\s?(.*)";
        }
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(message);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

}
