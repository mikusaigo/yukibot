package com.yuki.yukibot;

import com.yuki.yukibot.util.QQRobot;
import com.yuki.yukibot.util.constants.BotConstants;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.DeviceInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication(scanBasePackages = "com.yuki.yukibot")
public class YukibotApplication {

    public static void main(String[] args) {
        SpringApplication.run(YukibotApplication.class, args);
    }

}
