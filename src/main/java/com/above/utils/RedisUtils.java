package com.above.utils;

import com.above.config.redis.RedisOperator;
import com.above.dto.UserDto;
import com.above.po.User;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * @description redis工具类
 * @author imtss
 * @version 1.0
 * @date 2021/2/2、17:46
 */
@Component
public class RedisUtils {

    //加密的字符串,相当于签名
    public static final String SINGNATURE_TOKEN = "qianqianxun";
    //默认的Redis缓存过期时间
    private static final int defaultTime = 604800;

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private static RedisOperator staticRedisOperator;

    @PostConstruct
    public void init() {
        staticRedisOperator = redisOperator;
    }

    /**
     * 获取当前登录用户
     * @author imtss
     * @version 1.0
     * @date 2021/2/2 18:04
     * @param request
     *          token-身份token
     * @return
     **/
    public static User currentUser(HttpServletRequest request) {
        String token = MyStringUtils.getRequestToken(request);
        return currentUser(token);
    }
    public static User currentUser(String authToken) {
        if (!StringUtils.isEmpty(authToken)) {
            String userInfoJson = staticRedisOperator.get("user:info:" + authToken);
            if (!StringUtils.isEmpty(userInfoJson)) {
                return gson.fromJson(userInfoJson, new TypeToken<User>() {}.getType());
            }
            return null;
        }
        return null;
    }

    public static UserDto currentUserDto(HttpServletRequest request) {
        String token = MyStringUtils.getRequestToken(request);
        return currentUserDto(token);
    }
    public static UserDto currentUserDto(String authToken) {
        if (!StringUtils.isEmpty(authToken)) {
            String userInfoJson = staticRedisOperator.get("user:info:" + authToken);
            if (!StringUtils.isEmpty(userInfoJson)) {
                return gson.fromJson(userInfoJson, new TypeToken<UserDto>() {}.getType());
            }
            return null;
        }
        return null;
    }

    /**
     * 将用户信息放在redis中
     * key为token
     * @author imtss
     * @version 1.0
     * @date 2021/2/5 17:19
     * @param
     * @return authToken 生成的身份验证token
     **/
    public static String setUser(UserDto user) {
        return setUser(user, DigestUtils.sha256Hex(SINGNATURE_TOKEN + user.getTelephone()+ System.currentTimeMillis()));
    }
    public static String setUser(UserDto userInfo, HttpServletRequest request) {
        return setUser(userInfo, MyStringUtils.getRequestToken(request));
    }
    public static String setUser(UserDto userInfo, String authToken) {
        staticRedisOperator.set("user:info:" + authToken, gson.toJson(userInfo), defaultTime);
        //为了统计实时在线岗位数与实时在线兼职用户数，所以在设置用户token时，也将往Redis中放入这两个字段
        return authToken;
    }

    /**
     * 移除redis中的用户信息
     * @author imtss
     * @version 1.0
     * @date 2021/2/22 18:17
     * @param  request
     * @return
     **/
    public static void removeUser(HttpServletRequest request) {
        String token = MyStringUtils.getRequestToken(request);
        if (!StringUtils.isEmpty(token)) {
            staticRedisOperator.del("user:info:" + token);
        }
    }
    /**
     *@Decription:移除redis中的用户信息通过token
     *@Author:hxj
     *@Date:2021/5/7 11:53
     */

    public static void removeUser(String token) {
        if (!StringUtils.isEmpty(token)) {
            staticRedisOperator.del("user:info:" + token);
        }
    }

    /**
     * 设置没有期限的键值对
     * @Author: imtss
     * @Version: 1.0
     * @Date: 2021/3/22 18:29
     */
    public static void set(String key, String value) {
        staticRedisOperator.set(key, value);
    }
    /**
     * 设置有期限的键值对
     * @Author: imtss
     * @Version: 1.0
     * @Date: 2021/5/19 1:01
     */
    public static void set(String key, String value, long time) {
        staticRedisOperator.set(key, value, time);
    }

    /**
     * 获取key对应的value
     * @Author: imtss
     * @Version: 1.0
     * @Date: 2021/3/22 18:30
     */
    public static String get(String key) {
        return staticRedisOperator.get(key);
    }

    /**
     *@Decription:设置对应的ip以及访问时间
     *@params:
     *@return:
     *@Author:hxj
     *@Date:2021/8/11 16:01
     */
    public static void setIp(String ip, String params) {
        staticRedisOperator.set("ip:"+params+":"+ip.toString(), System.currentTimeMillis()+"",1);
    }

    public static String getIp(String ip, String params){
       String current = staticRedisOperator.get("ip:"+params+":"+ip.toString());

       if(StringUtils.isEmpty(current)){
           return null;
       }
       return current;
    }

    public static void setIp(String ip, String params, int time) {
        staticRedisOperator.set("ip:"+params+":"+ip.toString(), System.currentTimeMillis()+"",time);
    }

}
