package com.yuki.yukibot.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ServiceNode {

    private String address;

    private int port;

    private String method;

    public ServiceNode(Map<String, Object> map){
        address = String.valueOf(map.get("address"));
        port = (int) (map.get("port"));
        method = String.valueOf(map.get("method"));
    }

    @Override
    public String toString() {
        return "ServiceNode [address=" + address + ", port=" + port + ", method=" + method + "]";
    }
}
