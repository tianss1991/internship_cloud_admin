package com.above.utils;

import cn.hutool.crypto.digest.BCrypt;
//import org.mindrot.jbcrypt.BCrypt;

/**
 * @author imtss
 * @version 1.0
 * @description 密码加密工具
 * @date 2021/1/19、8:30
 */
public class PasswordCryptoTool {

    /**
     * @Description: 默认密码长度
     * @Author: LZH
     * @Date: 2022/1/10 14:50
     */
    public static final Integer DEFAULT_PASSWORD_LENGTH=6;

    /**
     * @Description: 获取账号后六位当作密码
     * @Author: LZH
     * @Date: 2022/3/3 13:46
     */
    public static String getDefaultPassword(String num){
        int length = num.length();
        if (length<=DEFAULT_PASSWORD_LENGTH){
            return encryptPassword(num);
        }else {
            String substring = num.substring(length - DEFAULT_PASSWORD_LENGTH, length);
            System.out.println("密码为"+substring);
            return encryptPassword(substring);
        }

    }

    /**
     * 加密密码
     *
     * @param plainPw
     * @return
     */
    public static String encryptPassword(String plainPw) {
        //FIXME 可以在hash前，对password预先处理，使破解难度加大
        return BCrypt.hashpw(plainPw, BCrypt.gensalt());
    }

    /**
     * 密码检查
     *
     * @param plainPw
     * @param encryptPw
     * @return
     */
    public static boolean checkPassword(String plainPw, String encryptPw) {
        //FIXME 可以在hash前，对password预先处理，使破解难度加大
        return BCrypt.checkpw(plainPw, encryptPw);
    }

    public static void main(String[] args) {
        System.out.println(getDefaultPassword("445454545455412"));
    }

}
