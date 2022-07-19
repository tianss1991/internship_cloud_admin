package com.above.config.shiro;

import com.above.config.sms.SmsConfig;
import com.above.constans.exception.PasswordException;
import com.above.constans.exception.RedisException;
import com.above.dto.AuthRolePermissionDto;
import com.above.dto.UserDto;
import com.above.po.AuthRole;
import com.above.po.WechatUser;
import com.above.service.AuthRolePermissionService;
import com.above.service.AuthRoleService;
import com.above.service.UserService;
import com.above.service.WechatUserService;
import com.above.utils.PasswordCryptoTool;
import com.above.utils.RedisUtils;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 实现Realm
 * @Author: LZH
 * @Date: 2022/1/9 15:30
 */
@Slf4j
public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;
    @Autowired
    @Lazy
    private AuthRoleService authRoleService;
    @Autowired
    @Lazy
    private AuthRolePermissionService authRolePermissionService;
    @Autowired
    private WechatUserService wechatUserService;

    /**
     * @Description: 权限配置类
     * @Author: LZH
     * @Date: 2022/1/9 15:19
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取用户
        Object primaryPrincipal = principalCollection.getPrimaryPrincipal();
        Integer roleId = (Integer) primaryPrincipal;
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        // 根据用户的role权限来获取响应的信息来查询是否存在权限
        // 如果获取到的roleId为null直接返回
        if(roleId == null || roleId == 0){
            return null;
        }
        AuthRole authRole = authRoleService.getById(roleId);
        // 设置角色
        simpleAuthorizationInfo.addRole(authRole.getRoleCode());
        // 获取权限信息
        List<AuthRolePermissionDto> permission = authRolePermissionService.getPermission(roleId);
        if(permission == null || permission.isEmpty()){
            permission = new ArrayList<>();
        }
        for (AuthRolePermissionDto authRolePermissionDto : permission) {
            simpleAuthorizationInfo.addStringPermission(authRolePermissionDto.getPermissionName());
        }
        return simpleAuthorizationInfo;
    }

    /**
     * @Description: 认证配置类
     * @Author: LZH
     * @Date: 2022/1/9 15:18
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        if (StringUtils.isEmpty(authenticationToken.getPrincipal())) {
            return null;
        }
        UsernamePasswordToken authenticationToken1 = (UsernamePasswordToken) authenticationToken;
        String userName = authenticationToken1.getUsername();
        Subject subject = SecurityUtils.getSubject();
        //获取用户信息
        String nameOrPhone = authenticationToken.getPrincipal().toString();
        Session session = subject.getSession();
        String codeStr = "code";
        //获取登录端
        Object loginType = session.getAttribute("loginType");
        //是否为验证码登录
        Object object = session.getAttribute("flag");
        boolean flag = object != null;
        //是否微信
        Object wechat = session.getAttribute("wechat");
        boolean wechatFlag = wechat != null;
        /*切换身份*/
        Object isChoos = session.getAttribute("isChoose");
        boolean isChoose = isChoos != null;

        /*-------------------切换权限-------------------*/
        if (isChoose){
            return chooseRole(session, nameOrPhone, null);
        }
        /*-------------------微信登录-------------------*/
        if (wechatFlag){
            return wechatLogin(session,nameOrPhone,null);
        }
        /*-------------------普通登录方式-------------------*/
        if (flag) {
            /*手机号验证码*/
           return telephoneAndCodeLogin(session,nameOrPhone,codeStr,loginType);
        } else {
            /*账号密码登录*/
            return normalLogin(session,nameOrPhone,loginType);
        }

    }

    /**
     *  账号密码登录
     * @param session 事务缓存
     * @param nameOrPhone 账号
     * @param loginType 登录端
     * @return SimpleAuthenticationInfo
     */
    private SimpleAuthenticationInfo normalLogin(Session session,String nameOrPhone,Object loginType){
        // 调用service方法 获取用户
        UserDto userDto = this.userService.getUserInfoByPhoneOrUserAccount(nameOrPhone,loginType);
        String password = (String) session.getAttribute(nameOrPhone);
        if (userDto == null) {
            throw new UnknownAccountException("用户不存在");
        }
        Integer roleId = userDto.getUserRoleDto() == null ? 0 : userDto.getUserRoleDto().getId();
        // 判断密码是否一致
        if(!PasswordCryptoTool.checkPassword(password,userDto.getPassword())){
            throw new PasswordException(PasswordException.PASSWORD_ERROR);
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(roleId,password,getName());
        // 把用户信息放入缓存
        String token = (String) session.getId();
        session.setAttribute(nameOrPhone, token);
        session.setAttribute(token, userDto);
        return simpleAuthenticationInfo;
    }

    /**
     *  手机号验证码登录
     * @param session 事务缓存
     * @param nameOrPhone 账号
     * @param codeStr 验证码
     * @param loginType 登录端
     * @return SimpleAuthenticationInfo
     */
    private SimpleAuthenticationInfo telephoneAndCodeLogin(Session session,String nameOrPhone,String codeStr,Object loginType){
        String timeStr = "time";
        //用户输入的code
        String redisCode = (String) session.getAttribute(nameOrPhone);
        if (!redisCode.equals("9999")){
            // 获取缓存中的验证码
//            JSONObject map = (JSONObject) session.getAttribute(SmsConfig.REDIS_CODE + nameOrPhone);
//            log.info("sessionid"+session.getId());
//            // 获取传入的code
//            if (map == null || map.get(codeStr) == null) {
//                log.error("====>1");
//                throw new RedisException(RedisException.CODE_EXPIRE);
//            }
//            String phoneCodeByCache = (String) map.get(codeStr);
//            // 判断是否过期
//            if (RedisException.EXPIRETIME < (System.currentTimeMillis() - (Long) map.get(timeStr))) {
//                session.removeAttribute(SmsConfig.REDIS_CODE + nameOrPhone);
//                throw new RedisException(RedisException.CODE_EXPIRE);
//            }
            String phoneCodeByCache = RedisUtils.get(SmsConfig.REDIS_CODE + nameOrPhone);
            // 验证是否验证码一致
            if (!phoneCodeByCache.equalsIgnoreCase(redisCode)) {
                throw new RedisException(RedisException.CODE_ERROR);
            }
        }
        // 获取用户
        UserDto userDto = this.userService.getUserInfoByPhoneOrUserAccount(nameOrPhone,loginType);
        if (userDto == null) {
            userDto = this.userService.registerUser(nameOrPhone);
            if (userDto == null){
                throw new UnknownAccountException("注册失败");
            }
        }
        Integer roleId = userDto.getUserRoleDto().getId();
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(roleId,redisCode,getName());
        // 设置token并且返回
        // 删除该验证码
        session.removeAttribute(SmsConfig.REDIS_CODE + nameOrPhone);
        // 丢入到缓存中
        String token = (String) session.getId();
        session.setAttribute(nameOrPhone, token);
        session.setAttribute(token, userDto);
        log.info(session.getId().toString());
        // 设置权限
        return simpleAuthenticationInfo;
    }

    /**
     *  微信小程序登录
     * @param session 事务缓存
     * @param nameOrPhone 账号
     * @param loginType 登录端
     * @return SimpleAuthenticationInfo
     */
    private SimpleAuthenticationInfo wechatLogin(Session session,String nameOrPhone,Object loginType){
        SimpleAuthenticationInfo simpleAuthenticationInfo = null;
        UserDto userDto = null;
        //是否微信第一次
        Object first = session.getAttribute("first");
        boolean firstFlag = first != null;

        //如果微信第一次登录，则直接登入一个我权限账号，进行绑定
        if (firstFlag){
            simpleAuthenticationInfo = new SimpleAuthenticationInfo(0,"ok",getName());
        }else {
            // 调用service方法 获取用户
            userDto = this.userService.getUserInfoByPhoneOrUserAccount(nameOrPhone,null);
            if (userDto == null) {
                throw new UnknownAccountException("用户不存在");
            }
            //通过用户账号获取openid
            QueryWrapper<WechatUser> wechatUserQueryWrapper = new QueryWrapper<>();
            wechatUserQueryWrapper.eq("account_number",userDto.getAccountNumber());
            WechatUser wechatUser = wechatUserService.getOne(wechatUserQueryWrapper);
            if (wechatUser == null) {
                throw new UnknownAccountException("该用户未绑定微信账号");
            }
            Integer roleId = userDto.getUserRoleDto() == null ? 0 : userDto.getUserRoleDto().getId();
            simpleAuthenticationInfo = new SimpleAuthenticationInfo(roleId,"ok",getName());
        }
        // 把用户信息放入缓存
        String token = (String) session.getId();
        session.setAttribute(nameOrPhone, token);
        session.setAttribute(token, userDto);
        return simpleAuthenticationInfo;
    }

    /**
     *  切换账号权限
     * @param session 事务缓存
     * @param nameOrPhone 账号
     * @param loginType 登录端
     * @return SimpleAuthenticationInfo
     */
    private SimpleAuthenticationInfo chooseRole(Session session,String nameOrPhone,Object loginType){
        Object roleIds = session.getAttribute("roleIds");

        Integer roleId = Integer.valueOf(roleIds.toString());
        SimpleAuthenticationInfo simpleAuthenticationInfo = null;
        UserDto userDto = this.userService.getUserInfoByPhoneOrUserAccount(nameOrPhone,null);
        if (userDto == null) {
            throw new UnknownAccountException("用户不存在");
        }
        //判断是否有传权限id
        userDto.setUserRoleDto(authRoleService.getById(roleId));

        simpleAuthenticationInfo = new SimpleAuthenticationInfo(roleId,"ok",getName());
        // 把用户信息放入缓存
        String token = (String) session.getId();
        session.setAttribute(nameOrPhone, token);
        session.setAttribute(token, userDto);
        return simpleAuthenticationInfo;

    }


}
