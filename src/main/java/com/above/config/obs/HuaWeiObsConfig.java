package com.above.config.obs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class HuaWeiObsConfig {

    public static String endPoint;
    public static String bucketName;
    public static String apiKey;
    public static String secretKey;

    @Value("${ObsConfig.endPoint}")
    private String endPointValue;
    @Value("${ObsConfig.bucketName}")
    private String bucketNameValue;
    @Value("${ObsConfig.AK}")
    private String apiKeyValue;
    @Value("${ObsConfig.SK}")
    private String secretKeyValue;

    @PostConstruct
    public void initConfig() {
        endPoint = this.endPointValue;
        bucketName = this.bucketNameValue;
        apiKey = this.apiKeyValue;
        secretKey = this.secretKeyValue;
    }


}
