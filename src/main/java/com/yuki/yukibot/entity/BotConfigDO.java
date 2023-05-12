package com.yuki.yukibot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yuki.yukibot.util.typehandler.LongListTypeHandler;
import com.yuki.yukibot.util.typehandler.StringListTypeHandler;
import lombok.Data;

import java.util.List;

@Data
@TableName("bot_config")
public class BotConfigDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("bot_identify")
    private String botIdentify;

    @TableField("bot_id")
    private Long botId;

    @TableField("bot_pwd")
    private String botPwd;

    @TableField("add_from_allowed_groups")
    private Boolean addFromAllowedGroups;

    @TableField("add_without_permission")
    private Boolean addWithoutPermission;

    @TableField("token_message")
    private String tokenMessage;

    @TableField("allowed_groups_mode")
    private String allowedGroupsMode;

    @TableField(value = "allowed_groups_ids", typeHandler = LongListTypeHandler.class)
    private List<Long> allowedGroupsIds;

    @TableField("allowed_friends_mode")
    private String allowedFriendsMode;

    @TableField(value = "allowed_friends_ids", typeHandler = LongListTypeHandler.class)
    private List<Long> allowedFriendsIds;

    @TableField(value = "allowed_group_name", typeHandler = StringListTypeHandler.class)
    private List<String> allowedGroupName;

    @TableField("allowed_strangers")
    private Boolean allowedStrangers;
}
