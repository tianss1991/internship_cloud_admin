package com.above.utils;

import java.util.UUID;

public class RandomUtil {
    /**
     *@Decription:随机uuid根据传入的数字取参数；
     *@params:
     *@return:
     *@Author:hxj
     *@Date:2021/12/24 15:45
     */
    public static String randomUUID(Integer num){

        String uuid = UUID.randomUUID().toString().replaceAll("-","").substring(0,num);

        return uuid;
    }

}
