package com.yuki.yukibot.util;

import com.yuki.yukibot.config.QQBotAuthorityConfig;
import com.yuki.yukibot.exception.ConfigurationException;
import com.yuki.yukibot.util.constants.QQBotAllowedModeConstants;
import com.yuki.yukibot.util.enums.YukiBotRespStateEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.friendgroup.FriendGroup;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class AvailableChecker {

    private final QQBotAuthorityConfig qqBotAuthorityConfig;

    public boolean groupAvailableCheck(Group group){
        boolean available = false;
        if (QQBotAllowedModeConstants.ID.equals(qqBotAuthorityConfig.getAllowedGroups().getMode())) {
            if (qqBotAuthorityConfig.getAllowedGroups().getIds().contains(group.getId())) {
                available = true;
            }
        } else {
            available = true;
        }
        return available;
    }

    public boolean friendAvailableCheck(Friend friend){
        boolean available = false;
        QQBotAuthorityConfig.AllowedFriends allowedFriends = qqBotAuthorityConfig.getAllowedFriends();
        String mode = allowedFriends.getMode();
        switch (mode) {
            case QQBotAllowedModeConstants.ID:
                List<Long> ids = allowedFriends.getIds();
                long id = friend.getId();
                if (ids.contains(id)) {
                    available = true;
                }
                break;
            case QQBotAllowedModeConstants.FRIEND_GROUP:
                Map<String, List<String>> friendGroups = allowedFriends.getFriendGroups();
                List<String> friendsGroupIds = friendGroups.get("ids");
                List<String> friendsGroupName = friendGroups.get("name");
                FriendGroup friendGroup = friend.getFriendGroup();
                if (friendsGroupIds.contains(String.valueOf(friendGroup.getId())) || friendsGroupName.contains(friendGroup.getName())) {
                    available = true;
                }
                break;
            case QQBotAllowedModeConstants.UNLIMITED:
                available = true;
                break;
            default:
                throw new ConfigurationException(YukiBotRespStateEnum.INVALID_ALLOWED_MODE);
        }
        return available;
    }

    public boolean friendAddAvailableCheck(NewFriendRequestEvent event){
        boolean available = false;
        long fromGroupId = event.getFromGroupId();
        if (0L == fromGroupId){
            log.info("有私自的新的好友添加请求，名称为====>{}, 验证消息为====>{}", event.getFromNick(), event.getMessage());
            if (Boolean.TRUE.equals(qqBotAuthorityConfig.getAddWithoutPermission())){
                if (qqBotAuthorityConfig.getTokenMessage().equals(event.getMessage())){
                    available = true;
                }
            }
        }else {
            log.info("有来自群聊的新的好友添加请求，群号为====>{}, 名称为====>{}, 验证消息为====>{}",event.getFromGroupId(), event.getFromNick(), event.getMessage());
            if (Boolean.TRUE.equals(qqBotAuthorityConfig.getAddFromAllowedGroups())){
                if (qqBotAuthorityConfig.getTokenMessage().equals(event.getMessage())){
                    available = true;
                }
            }
        }
        return available;
    }
}
