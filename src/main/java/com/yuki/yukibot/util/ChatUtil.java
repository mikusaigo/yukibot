package com.yuki.yukibot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {

    public static String getRealMsg(String message){
        String pattern = "^(?:@\\d{5,}\\s?)?(?:sys[:：]\\s?)?(.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(message);
        if (m.find()) {
            return m.group(1).trim();
        }
        return null;
    }

}
