package com.yuki.yukibot.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuki.yukibot.dao.BotConfigService;
import com.yuki.yukibot.entity.BotConfigDO;
import com.yuki.yukibot.mapper.BotConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class BotConfigServiceImpl extends ServiceImpl<BotConfigMapper, BotConfigDO> implements BotConfigService {

    /**
     * 通过bot qq号获取bot配置信息
     *
     * @param id bot qq号
     * @return bot配置信息
     */
    @Override
    public BotConfigDO getConfig(Long id) {
        return baseMapper.getBotConfigById(id);
    }

    /**
     * 通过bot 标识号获取bot配置信息
     *
     * @param identify bot 标识号
     * @return bot配置信息
     */
    @Override
    public BotConfigDO getConfig(String identify) {
        return baseMapper.getBotConfigByIden(identify);
    }
}
