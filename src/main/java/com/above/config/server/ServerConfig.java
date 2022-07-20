package com.above.config.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServerConfig {

    public static String localSaveUrl;
    public static String apiBaseUrl;

    @Value("${ServerConfig.localSaveUrl}")
    private String localSaveUrlValue;
    @Value("${ServerConfig.apiBaseUrl}")
    private String apiBaseUrlValue;

    @PostConstruct
    public void initConfig() {
        localSaveUrl = this.localSaveUrlValue;
        apiBaseUrl = this.apiBaseUrlValue;
    }

}
