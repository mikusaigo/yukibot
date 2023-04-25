package com.yuki.yukibot;

import com.yuki.yukibot.api.ChatGPTApi;
import com.yuki.yukibot.dao.BasicAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.Authenticator;

@SpringBootApplication
public class YukibotApplication {

    public static void main(String[] args) {
        SpringApplication.run(YukibotApplication.class, args);
        ChatGPTApi chatGPTApi = new ChatGPTApi();
//        System.setProperty("http.proxyHost", "a907a270-rtzls0-1gqjy.wtt5.p5pv.com");
//        System.setProperty("http.proxyPort", "22443");
//        System.setProperty("https.proxyHost", "a907a270-rtzls0-1gqjy.wtt5.p5pv.com");
//        System.setProperty("https.proxyPort", "22443");
//        System.setProperty("http.proxyUserName", "1418692441@qq.com");
//        System.setProperty("http.proxyPassword", "bc65e7ce-b73a-11ed-b581-f23c9164ca5d");
//        System.setProperty("https.proxyUserName", "1418692441@qq.com");
//        System.setProperty("https.proxyPassword", "bc65e7ce-b73a-11ed-b581-f23c9164ca5d");
//        Authenticator.setDefault(new BasicAuthenticator("1418692441@qq.com", "bc65e7ce-b73a-11ed-b581-f23c9164ca5d"));
        System.out.println(chatGPTApi.chat("你好"));
    }

}
