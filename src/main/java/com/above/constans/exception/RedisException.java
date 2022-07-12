package com.above.constans.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 *@Decription: redis异常
 *@params:
 *@return:
 *@Author:hxj
 *@Date:2022/1/10 15:08
 */
public class RedisException extends AuthenticationException {

    public static final String CODE_EXPIRE = "验证码过期";

    public static final String CODE_ERROR = "验证码错误";

    // 5分钟
    public static final transient Long EXPIRETIME = 5 * 60 * 1000L;

    public RedisException() {
        super();
    }

    public RedisException(String message) {
        super(message);
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisException(Throwable cause) {
        super(cause);
    }

}
