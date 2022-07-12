package com.above.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 字符串分割拼接工具类
 * @Author: LZH
 * @Date: 2021/10/21 10:49
 */
public class StringCutUtil {

    /**
     * @Description: 字符串按英文逗号分割
     * @Author: LZH
     * @Date: 2021/10/21 10:49
     */
    public static List<String> splitString(String str,String symbol){
        String[] split = str.split(symbol);
        //此集合无法操作添加元素
        List<String> list = Arrays.asList(split);
        List<String> list1 = new ArrayList<String>(list);
        return list1;
    }

    /**
     * @Description: 数组按符号拼接(String)
     * @Author: LZH
     * @Date: 2021/10/21 10:50
     */
    public static String appendStringByString(List<String> list,String symbol){
        String[] arr = list.toArray(new String[0]);
        StringBuilder sb = new StringBuilder();
        if (arr != null && arr.length > 0) {
            for (int i = 0; i < arr.length; i++) {
                if (i < arr.length - 1) {
                    sb.append(arr[i]).append(symbol);
                } else {
                    sb.append(arr[i]);
                }
            }
        }
        return sb.toString();

    }
    /**
     * @Description: 数组按符号拼接(int)
     * @Author:
     * @Date: 2022/03/31 9:33
     */
    public static String appendStringByInteger(List<Integer> list,String symbol){
       Integer[] arr = (Integer[]) list.toArray(new Integer[0]);
        StringBuilder sb = new StringBuilder();
        if (arr != null && arr.length > 0) {
            for (int i = 0; i < arr.length; i++) {
                if (i < arr.length - 1) {
                    sb.append(arr[i]).append(symbol);
                } else {
                    sb.append(arr[i]);
                }
            }
        }
        return sb.toString();

    }

}


