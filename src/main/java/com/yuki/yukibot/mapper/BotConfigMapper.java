package com.yuki.yukibot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuki.yukibot.entity.BotConfigDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BotConfigMapper extends BaseMapper<BotConfigDO> {

    BotConfigDO getBotConfigById(@Param("id") Long id);

    BotConfigDO getBotConfigByIden(@Param("identify") String identify);
}
