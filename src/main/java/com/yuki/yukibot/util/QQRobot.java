package com.yuki.yukibot.util;

import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.config.QQBotNormalConfig;
import com.yuki.yukibot.util.messageHandler.CommonMessageHandler;
import kotlin.coroutines.CoroutineContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.DeviceInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import xyz.cssxsh.mirai.device.MiraiDeviceGenerator;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * TODO: 将不同模式处理封装为返回布尔值
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QQRobot extends SimpleListenerHost {

    private final QQBotNormalConfig qqBotNormalConfig;

    private final CommonMessageHandler commonMessageHandler;

    @PostConstruct
    public void initBot() {
        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
        botConfiguration.setAutoReconnectOnForceOffline(true);
        botConfiguration.setLoginCacheEnabled(true);
        MiraiDeviceGenerator miraiDeviceGenerator = new MiraiDeviceGenerator();
        DeviceInfo deviceInfo = miraiDeviceGenerator.generate();
        String jsonStr = JSONUtil.toJsonStr(deviceInfo);
        log.info("device_info===> {}", jsonStr);
//        botConfiguration.setDeviceInfo(bot -> DeviceInfo.from(new File("src/main/resources/device/device.json")));
        // 创建机器人实例

        Bot bot = BotFactory.INSTANCE.newBot(qqBotNormalConfig.getId(), qqBotNormalConfig.getPassword(), botConfiguration);
        // 注册事件监听器
        bot.getEventChannel().registerListenerHost(commonMessageHandler);
        // 启动机器人
        bot.login();
    }

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        log.error(exception.getMessage());
    }


}
