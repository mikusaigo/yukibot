package com.yuki.yukibot.util;

import com.yuki.yukibot.util.constants.DurationConstants;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.GroupSettings;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;

public class GroupManager {

    public static void mute(Member member){
        member.mute(DurationConstants.ONE_MINUTE);
    }

    public static void mute(Member member, String duration) {
        member.mute(DurationFormat.formatDurationStr(duration).getDuration());
    }

    public static void mute(Member member, int durationSeconds) {
        member.mute(durationSeconds);
    }

    public static void unmute(NormalMember normalMember){
        normalMember.unmute();
    }

    public static void muteAll(Group group, boolean cancel){
        GroupSettings settings = group.getSettings();
        settings.setMuteAll(cancel);
    }
}
