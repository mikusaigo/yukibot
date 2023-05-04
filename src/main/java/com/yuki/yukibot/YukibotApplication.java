package com.yuki.yukibot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.yuki.yukibot")
public class YukibotApplication {

    public static void main(String[] args) {
        SpringApplication.run(YukibotApplication.class, args);
    }

}
