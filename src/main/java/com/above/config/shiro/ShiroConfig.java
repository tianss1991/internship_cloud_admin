package com.above.config.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }

    @Bean
    public DefaultWebSessionManager sessionManager(){
        GetSessionIdConfig getSessionIdConfig = new GetSessionIdConfig();
        getSessionIdConfig.setSessionIdUrlRewritingEnabled(false);
        getSessionIdConfig.setSessionValidationSchedulerEnabled(true);
        return getSessionIdConfig;
    }

    /**
     * @Description: 将自己的验证方式加入容器
     * @Author: LZH
     * @Date: 2022/1/9 15:55
     */
    @Bean
    public CustomRealm myShiroRealm() {
        return new CustomRealm();
    }

    /**
     * @Description: 权限管理，配置主要是Realm的管理认证
     * @Author: LZH
     * @Date: 2022/1/9 15:55
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myShiroRealm());
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    /**
     * @Description: Filter工厂，设置对应的拦截条件和跳转条件
     * @Author: LZH
     * @Date: 2022/1/9 15:54
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        LinkedHashMap<String, String> filterMap = new LinkedHashMap<>(40);
        //登出
        /*filterMap.put("/user/logout", "logout");*/
        //swagger接口权限 开放
        filterMap.put("/doc.html", "anon");
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/v2/**", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/user/login","anon");
        filterMap.put("/user/getCode","anon");
        filterMap.put("/wechat/appletLogin","anon");
        filterMap.put("/wechat/wechatBingAccount","anon");
        filterMap.put("/excelTemplate/*","anon");
        //上传文件接口
//        filterMap.put("/file/upload","anon");
        //对所有用户认证 - 必须放在最后，shiro按顺序拦截
        filterMap.put("/**", "authc");
        //登录
        /*shiroFilterFactoryBean.setLoginUrl("/login");*/
        //首页
        shiroFilterFactoryBean.setSuccessUrl("/index");
        //错误页面，认证不通过跳转
        shiroFilterFactoryBean.setUnauthorizedUrl("/error");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);

        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        filters.put("authc", new ShiroLoginFilter());

        return shiroFilterFactoryBean;
    }

    /**
     * @Description: 注入权限管理
     * @Author: LZH
     * @Date: 2022/1/9 15:55
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
