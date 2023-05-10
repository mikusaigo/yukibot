package com.yuki.yukibot.util;

import com.yuki.yukibot.util.constants.DurationConstants;
import net.mamoe.mirai.contact.Member;

public class GroupManager {

    public static void mute(Member member){
        member.mute(DurationConstants.ONE_MINUTE);
    }

    public static void mute(Member member, String duration) {
        member.mute(DurationFormat.getIntDurationSeconds(duration));
    }

    public static void mute(Member member, int durationSeconds) {
        member.mute(durationSeconds);
    }
}
