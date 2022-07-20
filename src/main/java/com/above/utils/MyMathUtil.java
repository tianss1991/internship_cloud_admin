package com.above.utils;

import java.math.BigDecimal;

public class MyMathUtil {


    /**
     * 赤道半径(单位m)
     */
    private static final double EARTH_RADIUS = 6371000;
    private static final BigDecimal PIE = new BigDecimal(Math.PI);

    /**
     * 转化为弧度(rad)
     * */
    private static BigDecimal rad(String num)
    {
        return new BigDecimal(num).multiply(PIE).divide(new BigDecimal("180"),BigDecimal.ROUND_HALF_DOWN);
    }
    /**
     *  计算两个经纬度之间的距离
     * @param lo1 第一点的精度
     * @param la1 第一点的纬度
     * @param lo2 第二点的精度
     * @param la2 第二点的纬度
     * @return 返回的距离，单位m
     * */
    public static BigDecimal getTwoPointDistance(String lo1, String la1, String lo2, String la2){
        //转化弧度
        BigDecimal la1Rad = rad(la1);
        BigDecimal la2Rad = rad(la2);

        BigDecimal laDivide = rad(la1).subtract(rad(la2));
        BigDecimal loDivide = rad(lo1).subtract(rad(lo2));

        double distant = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(laDivide.divide(new BigDecimal(2),BigDecimal.ROUND_HALF_DOWN).doubleValue()), 2) +
                Math.cos(la1Rad.doubleValue()) * Math.cos(la2Rad.doubleValue()) * Math.pow(Math.sin(loDivide.divide(new BigDecimal(2),BigDecimal.ROUND_HALF_DOWN).doubleValue()), 2)));
        distant = distant * EARTH_RADIUS;

        return new BigDecimal(Math.round(distant * 10000) / 10000);
    }



    public static void main(String[] args) {

        String lo1 = "119.29647";
        String la1 = "26.07421";

        String lo2 = "119.29647";
        String la2 = "26.07421";

        //转化弧度
        BigDecimal la1Rad = rad(la1);
        BigDecimal la2Rad = rad(la2);

        BigDecimal laDivide = rad(la1).subtract(rad(la2));
        BigDecimal loDivide = rad(lo1).subtract(rad(lo2));

        double distant = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(laDivide.divide(new BigDecimal(2),BigDecimal.ROUND_HALF_DOWN).doubleValue()), 2) +
                Math.cos(la1Rad.doubleValue()) * Math.cos(la2Rad.doubleValue()) * Math.pow(Math.sin(loDivide.divide(new BigDecimal(2),BigDecimal.ROUND_HALF_DOWN).doubleValue()), 2)));
        distant = distant * EARTH_RADIUS;

        BigDecimal finalDistant = new BigDecimal(Math.round(distant * 10000) / 10000);

        System.out.println(finalDistant);

    }

}
