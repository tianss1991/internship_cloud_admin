package com.above.config.shiro;

import com.above.constans.exception.PasswordException;
import com.above.constans.exception.RedisException;
import com.above.utils.CommonResult;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: shiro异常处理
 * @Author: LZH
 * @Date: 2022/1/9 16:47
 */
@ControllerAdvice
@Slf4j
public class MyExceptionHandler {

    /**
     * 登录认证异常
     */
    @ExceptionHandler({ AuthenticationException.class })
    public String authenticationException(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", "500");
        map.put("message", "验证码或密码错误");
        writeJson(map, response);
        return null;
    }

    /**
     *  认证失败
     * @param request
     * @param response
     * @return
     */
    @ExceptionHandler({ UnauthenticatedException.class })
    public String unAuthenticatiedException(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("code", "500");
        map.put("message", "账号或密码错误");
        writeJson(map, response);
        return null;
    }

    /**
     * 权限异常
     */
    @ExceptionHandler({ UnauthorizedException.class, AuthorizationException.class })
    public String authorizationException(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", "500");
        map.put("message", "无权限");
        writeJson(map, response);
        return null;
    }

    @ExceptionHandler({RedisException.class})
    public String redisCodeException(HttpServletRequest request, HttpServletResponse response,RedisException e){
        Map<String, Object> map = new HashMap<>(16);
        map.put("code", "500");
        map.put("message",e.getMessage());
        writeJson(map, response);
        return null;
    }
    @ExceptionHandler({PasswordException.class})
    public String passwordException(HttpServletRequest request, HttpServletResponse response,PasswordException e){
        Map<String, Object> map = new HashMap<>(16);
        map.put("code", "500");
        map.put("message",e.getMessage());
        writeJson(map, response);
        return null;
    }
    @ExceptionHandler({UnknownAccountException.class})
    public String unKnowAccountException(HttpServletRequest request, HttpServletResponse response,UnknownAccountException e){
        Map<String, Object> map = new HashMap<>(16);
        map.put("code", "500");
        map.put("message",e.getMessage());
        writeJson(map, response);
        return null;
    }

    private void writeJson(Map<String, Object> map, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            out = response.getWriter();

            out.write(JSON.toJSONString(map));
        } catch (IOException e) {
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
