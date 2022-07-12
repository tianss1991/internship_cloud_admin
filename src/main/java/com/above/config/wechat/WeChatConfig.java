package com.above.config.wechat;

/**
 * @Description: 微信小程序配置
 * @Author: LZH
 * @Date: 2022/2/21 10:33
 */
public class WeChatConfig {

    //测试小程序
//    public static final String APPID = "wxd09460dccf97f4cf";
//    public static final String APPSECRECT = "7d35bc2bfe41f9b8d6cbdbcf96d4b4b3";
    //正式小程序
    public static final String APPID = "wx968f3a51542010c3";
    public static final String APPSECRECT = "81e5a476ab1fbc1fe096691513db23a5";

    public static final String GRANTTYPE = "authorization_code";
    public static final String PAGE = "pages/center/index";
    //跳转的小程序版本 release-正式 trial-体验版 develop-开发版
    public static final String ENV_VERSION = "develop";

}
