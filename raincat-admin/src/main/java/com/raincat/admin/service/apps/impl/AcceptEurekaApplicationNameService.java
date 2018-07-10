package com.raincat.admin.service.apps.impl;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raincat.admin.service.apps.AcceptApplicationNameService;
import com.raincat.admin.service.apps.enums.AcceptApplicationNameEnum;
import com.raincat.common.holder.httpclient.OkHttpTools;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chaocoffee
 * @date 2018/7/5
 * @description
 */
@Service
@Slf4j
public class AcceptEurekaApplicationNameService implements AcceptApplicationNameService {

    @Value("${accept.apps.eureka.url}")
    private String eurekaServerUrl;

    @Value("${accept.apps.eureka.intervalTime}")
    private String appIntervalTime;

    private static volatile ConcurrentHashMap<String, Object> CACHE_MAP = new ConcurrentHashMap<>();

    private static String UPDATE_TIME_KEY = "ADMIN_UPDATE_TIME";


    @Override
    public <T> List<T> acceptAppNameList(List<T> apps) {
        Object updateTime = CACHE_MAP.get(UPDATE_TIME_KEY);
        if (updateTime == null) {
            log.info("首次加入 [参与分布式事务项目的应用名称] 缓存数据!");
            getEurekaAppList().stream().forEach(appName -> CACHE_MAP.put(appName, appName));
        } else {
            long dValue = System.currentTimeMillis() - (Long) updateTime;
            if (dValue > Long.valueOf(appIntervalTime) * 1000) {
                log.info("刷新 [参与分布式事务项目的应用名称] 缓存数据!");
                getEurekaAppList().stream().forEach(appName -> CACHE_MAP.put(appName, appName));
            }
        }
        CACHE_MAP.forEach((s, o) -> {
            if (!UPDATE_TIME_KEY.equals(s) && !apps.contains(s)) {
                apps.add((T) s);
            }
        });
        return apps;
    }

    @Override
    public AcceptApplicationNameEnum code() {
        return AcceptApplicationNameEnum.EUREKA;
    }

    private Set<String> getEurekaAppList() {
        Set<String> set = Sets.newHashSet();
        try {
            JsonObject response = OkHttpTools.getInstance().execute(buildRequest(eurekaServerUrl + APP_URL), JsonObject.class);
            log.debug("Eureka server response: [{}]", response);
            if (response == null) {
                return set;
            }
            JsonArray jsonArray = response.getAsJsonObject("applications").getAsJsonArray("application");
            if (jsonArray != null) {
                jsonArray.forEach(jsonObject -> {
                    JsonObject application = jsonObject.getAsJsonObject();
                    //每个实例信息
                    application.getAsJsonArray("instance").forEach(jsonObj -> {
                        JsonObject instance = jsonObj.getAsJsonObject();
                        //是否使用tx
                        JsonElement jsonElement = instance.getAsJsonObject("metadata").get("tx-metadata");
                        Optional<String> optional = Optional
                                .ofNullable(jsonElement)
                                .map(r -> jsonElement.getAsBoolean() ? "true" : null);
                        optional.ifPresent(r -> set.add(instance.get("app").getAsString().toLowerCase()));
                    });
                });
            }
            CACHE_MAP.put(UPDATE_TIME_KEY, System.currentTimeMillis());
        } catch (IOException e) {
            log.error("Eureka server response analysis error!", e);
        }
        return set;
    }

    private static Request buildRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                //.addHeader("Authorization", Credentials.basic("user", "password"))
                .build();
    }
}
