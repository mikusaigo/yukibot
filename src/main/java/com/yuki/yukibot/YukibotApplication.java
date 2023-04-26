package com.yuki.yukibot;

import com.yuki.yukibot.util.QQRobot;
import com.yuki.yukibot.util.constants.BotConstants;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YukibotApplication {

    public static void main(String[] args) {
        SpringApplication.run(YukibotApplication.class, args);
        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
        botConfiguration.setAutoReconnectOnForceOffline(true);
        botConfiguration.setLoginCacheEnabled(true);
        // 创建机器人实例

        Bot bot = BotFactory.INSTANCE.newBot(BotConstants.BOT_QQ, BotConstants.BOT_PASSWORD, botConfiguration);
        // 注册事件监听器
        bot.getEventChannel().registerListenerHost(new QQRobot());
        // 启动机器人
        bot.login();
    }

}
