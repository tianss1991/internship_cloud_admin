package com.above.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @author imtss<br>
 * @version 1.0<br>
 * @date 2021-01-07 18:26 <br>
 */
@Slf4j
public class MyStringUtils {

    private MyStringUtils() {
    }

    //是否为正常的手机号码
    public static boolean isTelephone(String telephone) {
        //TODO 手机号验证暂时这样简单处理，待后面完善
        return ReUtil.isMatch("1\\d{10}", telephone);
    }

    //是否为虚拟的手机号
    public static boolean isVirtualTelephone(String telephone) {
        return ReUtil.isMatch("^1((62)|(65)|(66)|(67)|(70)|(71))\\d{8}$", telephone);
    }

    //是否为正常的邮箱
    public static boolean isEmail(String email) {
        return ReUtil.isMatch("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", email);
    }

    //是否为身份证号
    public static boolean isIdCardNumber(String idCardNumber) {
        return ReUtil.isMatch("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", idCardNumber);
}

    //从请求中获取token
    public static String getRequestToken(HttpServletRequest request) {
        //默认从请求头中获得token
        String token = request.getHeader("token");
        //如果header中不存在token，则从参数中获取token
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter("token");
        }
        return token;
    }

    public static String getCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String format = sdf.format(new Date());
        String randomCode = IdUtil.fastUUID().substring(0, 7);
        return "QQX".concat(format).concat(randomCode);
    }

    //将字符串解析成数字
    //不设置默认值并且返回-1代表有异常，外部视情况自行判断是否处理
    public static Integer getNumberFromStr(String numberStr, Integer defaultInt) {
        if (StringUtils.isEmpty(numberStr)) {
            if (defaultInt == null) {
                return 0;
            }
            return defaultInt;
        }
        try {
            return Integer.parseInt(numberStr);
        } catch (Exception e) {
            if (defaultInt == null) {
                return -1;
            }
            return defaultInt;
        }
    }

    /**
     * 生成固定位数的随机字符串
     *
     * @param length
     * @return
     */
    public static String uuId(Integer length) {
        if (length == null || length == 0) {
            return null;
        }
        // 随机生成固定位数的随机字符串
        String uuId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, length);
        return uuId;
    }

    /**
     * 获取手机验证码
     *
     * @return 验证码
     */
    public static String getPhoneCode(Integer length) {
        Integer minLength = 4;
        // 如果长度小于4位就报错
        if (length < minLength) {
            return "ERROR";
        }
        StringBuffer stringBuffer = new StringBuffer("");
        // 拼接
        for (int i = 0; i < length; i++) {
            stringBuffer.append("9");
        }
        String lengthStr = stringBuffer.toString();
        length = Integer.parseInt(lengthStr);
        // 生成指定长度的code
        String code = String.format("%0" + lengthStr.length() + "d", new Random().nextInt(length));
        return code;
    }

    public static void main(String[] args) {
        System.out.println(getCode());
    }
}
