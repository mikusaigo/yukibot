package com.yuki.yukibot.config;

import cn.hutool.core.collection.CollUtil;
import com.yuki.yukibot.util.constants.QQBotAllowedModeConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties("qqbot")
public class QQBotConfig {

    private Long id;
    private String password;
    private AllowedGroups allowedGroups;
    private AllowedFriends allowedFriends;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AllowedGroups getAllowedGroups() {
        return allowedGroups;
    }

    public void setAllowedGroups(AllowedGroups allowedGroups) {
        this.allowedGroups = allowedGroups;
    }

    public AllowedFriends getAllowedFriends() {
        return allowedFriends;
    }

    public void setAllowedFriends(AllowedFriends allowedFriends) {
        this.allowedFriends = allowedFriends;
    }

    public static class AllowedGroups {
        private String mode;

        private List<Long> ids;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode == null ? QQBotAllowedModeConstants.UNLIMITED : mode;
        }

        public List<Long> getIds() {
            return ids;
        }

        public void setIds(List<Long> ids) {
            this.ids = ids;
        }
    }

    public static class AllowedFriends {

        private String mode;

        private List<Long> ids;

        private Map<String, List<String>> friendGroups;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode == null ? QQBotAllowedModeConstants.UNLIMITED : mode;
        }

        public List<Long> getIds() {
            return ids;
        }

        public void setIds(List<Long> ids) {
            this.ids = ids;
        }

        public Map<String, List<String>> getFriendGroups() {
            if (CollUtil.isEmpty(friendGroups)) {
                return null;
            }
            List<String> friendGroupIds = friendGroups.get("ids");
            if (CollUtil.isEmpty(friendGroupIds)) {
                friendGroupIds.add("0");
            }
            return friendGroups;
        }

        public void setFriendGroups(Map<String, List<String>> friendGroups) {
            this.friendGroups = friendGroups;
        }
    }
}
