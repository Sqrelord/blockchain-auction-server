package edu.dhu.auction.web.auth;

import cn.hutool.core.map.MapUtil;
import org.springframework.http.HttpMethod;

import java.util.Map;

import static org.springframework.http.HttpMethod.*;

public class AuthPath {

    public static final Map<Object, Object> PATH =
            MapUtil.builder()
                    .put("/user/info", GET)
                    .put("/user/edit", POST)
                    .put("/user/password", POST)
                    .put("/user/address", GET)
                    .put("/user/address", POST)
                    .put("/user/address", DELETE)
                    .put("/lot/add", POST)
                    .put("/lot/comment", POST)
                    .put("/auction/participate", POST)
                    .put("/auction/bid", POST)
                    .put("/auction/list", GET)
                    .put("/order/list", GET)
                    .put("/order/info", GET)
                    .put("/order/status", POST)
                    .build();

    public static boolean isAuthPath(String url, String method) {
        HttpMethod requestMethod = MapUtil.getQuietly(PATH, url, HttpMethod.class, null);
        if (requestMethod != null) {
            return requestMethod.equals(HttpMethod.resolve(method));
        }
        return false;
    }

    public static String[] getPath(HttpMethod method) {
        return PATH.entrySet().stream()
                .filter(it -> it.getValue().equals(method))
                .map(it -> (String) it.getKey())
                .toArray(String[]::new);
    }
}
