package com.above.config.shiro;

import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: shiro 登录判断
 * @Author: LZH
 * @Date: 2022/1/9 16:48
 */
@Slf4j
public class ShiroLoginFilter extends FormAuthenticationFilter {
    /**
     * 如果isAccessAllowed返回false 则执行onAccessDenied
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (request instanceof HttpServletRequest) {
            if (((HttpServletRequest) request).getMethod().equalsIgnoreCase("OPTIONS")) {
                return true;
            }
        }

//        return true;
        return super.isAccessAllowed(request, response, mappedValue);
    }
    /**
     * 在访问controller前判断是否登录，返回json，不进行重定向。
     */
    @Override
        protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException, IOException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        //这里是个坑，如果不设置的接受的访问源，那么前端都会报跨域错误，因为这里还没到corsConfig里面
        httpServletResponse.setHeader("Access-Control-Allow-Origin", ((HttpServletRequest) request).getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().write(JSONObject.toJSON(CommonResult.error(401,"未登录")).toString());
        return false;
    }
}