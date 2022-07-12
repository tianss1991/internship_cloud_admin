package com.above.constans.exception;

import org.apache.shiro.authc.AuthenticationException;
/**
 *@Decription: 密码异常
 *@params:
 *@return:
 *@Author:hxj
 *@Date:2022/1/10 16:59
 */
public class PasswordException extends AuthenticationException {

    public static final String PASSWORD_ERROR = "密码错误";

    public PasswordException() {
        super();
    }

    public PasswordException(String message) {
        super(message);
    }

    public PasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordException(Throwable cause) {
        super(cause);
    }
}
