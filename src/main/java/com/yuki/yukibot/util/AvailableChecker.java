package com.yuki.yukibot.util;

import com.yuki.yukibot.dao.BotConfigService;
import com.yuki.yukibot.entity.BotConfigDO;
import com.yuki.yukibot.exception.ConfigurationException;
import com.yuki.yukibot.util.constants.QQBotAllowedModeConstants;
import com.yuki.yukibot.util.enums.YukiBotRespStateEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.friendgroup.FriendGroup;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AvailableChecker {

    private final BotConfigService botConfigService;

    public boolean hasAdminAuthority(MemberPermission permission){
        return MemberPermission.ADMINISTRATOR.equals(permission) || MemberPermission.OWNER.equals(permission);
    }

    public boolean groupAvailableCheck(Group group){
        BotConfigDO config = botConfigService.getConfig("yuki");
        boolean available = false;
        if (QQBotAllowedModeConstants.ID.equals(config.getAllowedGroupsMode())) {
            if (config.getAllowedGroupsIds().contains(group.getId())) {
                available = true;
            }
        } else {
            available = true;
        }
        return available;
    }

    public boolean friendAvailableCheck(Friend friend){
        boolean available = false;
        BotConfigDO config = botConfigService.getConfig("yuki");
        String mode = config.getAllowedFriendsMode();
        switch (mode) {
            case QQBotAllowedModeConstants.ID:
                List<Long> ids = config.getAllowedFriendsIds();
                long id = friend.getId();
                if (ids.contains(id)) {
                    available = true;
                }
                break;
            case QQBotAllowedModeConstants.FRIEND_GROUP:
                List<String> allowedGroupName = config.getAllowedGroupName();
                FriendGroup friendGroup = friend.getFriendGroup();
                if (allowedGroupName.contains(friendGroup.getName())) {
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
        BotConfigDO config = botConfigService.getConfig("yuki");
        if (0L == fromGroupId){
            log.info("有私自的新的好友添加请求，名称为====>{}, 验证消息为====>{}", event.getFromNick(), event.getMessage());
            if (Boolean.TRUE.equals(config.getAddWithoutPermission())){
                if (config.getTokenMessage().equals(event.getMessage())){
                    available = true;
                }
            }
        }else {
            log.info("有来自群聊的新的好友添加请求，群号为====>{}, 名称为====>{}, 验证消息为====>{}",event.getFromGroupId(), event.getFromNick(), event.getMessage());
            if (Boolean.TRUE.equals(config.getAddFromAllowedGroups())){
                if (config.getTokenMessage().equals(event.getMessage())){
                    available = true;
                }
            }
        }
        return available;
    }
}
