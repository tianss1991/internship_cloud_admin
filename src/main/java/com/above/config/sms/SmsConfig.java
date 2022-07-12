package com.above.config.sms;

/**
 * @Decription:阿里云的发送短信配置文件
 * @params:
 * @return:
 * @Author:hxj
 * @Date:2021/12/13 14:43
 */
public class SmsConfig {
    //accessKeyId
    public static String ACCESS_KEY_ID = "LTAI4GAUSZCMRNDT1gUMxzkQ";
    //accessSecret
    public static String ACCESS_KEY_SECRET = "FwjkmAVfuuOZfPpge7iiuuYVQpN0cE";
    //签名名称
    public static String SIGN_NAME = "安博科技实训云平台";
    //模板
    public static String TEMPLATE_CODE = "SMS_212660444";
    //过期的时间 5分钟
    public static Long EXPIRE_TIME = 5 * 60L;
    /**
     *  错误状态码
     */
    public static final String ERROR_STATUS = "ERROR";
    /**
     *  存到redis里面的抬头
     */
    public static final String REDIS_CODE = "userInfo:code:";
}
