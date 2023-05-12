package com.yuki.yukibot.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuki.yukibot.entity.BotConfigDO;

public interface BotConfigService extends IService<BotConfigDO> {

    /**
     * 通过bot qq号获取bot配置信息
     * @param id bot qq号
     * @return bot配置信息
     */
    BotConfigDO getConfig(Long id);

    /**
     * 通过bot 标识号获取bot配置信息
     * @param identify bot 标识号
     * @return bot配置信息
     */
    BotConfigDO getConfig(String identify);
}
