package com.yuki.yukibot.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.yuki.yukibot.model.ServiceNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestController {

    public static void main(String[] args) {
        List<ServiceNode> serviceNodes = null;
        try {
            serviceNodes = parseSubscription("https://21a1430a.ghelper.me/rss/cc41316bfb741cb7d33aaace24111e63");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        serviceNodes.forEach(System.out::println);
    }

    public static List<ServiceNode> parseSubscription(String subscriptionUrl) throws IOException {
        // 打开订阅地址，并读取内容
        String result = HttpUtil.get(subscriptionUrl);

        // 解码并解压缩订阅内容
        byte[] decodedBytes = Base64.decode(result);

        // 解析订阅内容
        String uncompressedContent = new String(decodedBytes, StandardCharsets.UTF_8);
        List<ServiceNode> nodes = new ArrayList<>();
        String[] strings = uncompressedContent.split("[\\s\\n]+");
        for (String string : strings) {
            if (string.startsWith("http")){
                byte[] decode = Base64.decode(string);
                String s = new String(decode);
                ServiceNode bean = JSONUtil.toBean(s, ServiceNode.class);
                nodes.add(bean);
            }
        }

        return nodes;
    }

}
