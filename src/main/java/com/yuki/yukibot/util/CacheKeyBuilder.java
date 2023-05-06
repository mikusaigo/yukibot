package com.yuki.yukibot.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.yuki.yukibot.util.constants.RedisConstants;

public class CacheKeyBuilder {

    /**
     * 构建群组消息缓存key
     * @param groupId 群号
     * @param memberId 群成员qq号
     * @return 群组消息缓存key
     */
    public static String buildGroupKey(Long groupId, Long memberId){
        return RedisConstants.BASE_SYS_KEY + RedisConstants.GROUP_MSG_KEY + CharSequenceUtil.join(StrPool.COLON, groupId, memberId);
    }

    /**
     * 构建好友消息缓存key
     * @param friendId qq号
     * @return 好友消息缓存key
     */
    public static String buildFriendKey(Long friendId){
        return RedisConstants.BASE_SYS_KEY + RedisConstants.FRIEND_MSG_KEY + StrPool.COLON + friendId;
    }
}
