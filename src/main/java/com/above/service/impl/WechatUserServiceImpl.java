package com.above.service.impl;

import com.above.config.wechat.WeChatConfig;
import com.above.dao.WechatUserMapper;
import com.above.dto.UserDto;
import com.above.po.WechatUser;
import com.above.service.UserService;
import com.above.service.WechatUserService;
import com.above.utils.CommonResult;
import com.above.utils.CommonUtil;
import com.above.utils.PasswordCryptoTool;
import com.above.vo.user.UserVo;
import com.above.vo.user.WeChatVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 微信用户表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-02-20
 */
@Slf4j
@Service
public class WechatUserServiceImpl extends ServiceImpl<WechatUserMapper, WechatUser> implements WechatUserService {



    @Autowired
    private UserService userService;

    private static final String APPID = WeChatConfig.APPID;
    private static final String APPSECRECT = WeChatConfig.APPSECRECT;
    private static final String GRANTTYPE = WeChatConfig.GRANTTYPE;
    /**
     * @param request
     * @Description: 微信小程序登录
     * @Author: LZH
     * @Date: 2022/2/21 14:06
     */
    @Override
    public CommonResult<Object> weChatLogin(HttpServletRequest request, WeChatVo vo) {
        String WX_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";
        String code = vo.getCode();
        String iv = vo.getCode();
        String encryptedData = vo.getEncryptedData();
        log.info(iv);
        // 获取shiro对象
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = null;
        Session session = subject.getSession();
        Map<String, Object> returnMap = new HashMap<>(16);
        try {
            if (StringUtils.isBlank(code)) {
                return CommonResult.error(500,"code不能为空");
            }else  {
                String requestUrl = WX_URL.replace("APPID", APPID).replace("SECRET", APPSECRECT).replace("JSCODE", code).replace("authorization_code", GRANTTYPE);
                /*jsonObject ==> {"unionid":"xxxxxxxxxxxxxxxxxxxxx","openid":"xxxxxxxxxxxxxxxxx","session_key":"xxxxxxxxxxxxxxxx"}*/
                JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
                if (jsonObject != null) {
                    try {
                        // 业务操作
                        if (!StringUtils.isBlank(jsonObject.getString("errcode"))){
                            //判断状态码
                            switch (jsonObject.getString("errcode")){
                                case "-1":
                                    return CommonResult.error(500,"系统繁忙，请稍候再试");
                                case "40029":
                                    return CommonResult.error(500,"code已失效,请重新获取");
                                case "45011":
                                    return CommonResult.error(500,"请求太过频繁,请稍后再试");
                                default:
                            }
                        }
                        String openId = jsonObject.getString("openid");
                        //微信在一个平台的唯一标识
                        String unionId = jsonObject.getString("unionid");
                        String sessionKey = jsonObject.getString("session_key");
                        /* 获取用户微信信息 需要公众号绑定才有
                            JSONObject object = WXUtils.getUserInfo(encryptedData,sessionKey,iv);
                        */
                        //查询该用户是否有绑定
                        WechatUser wechatUser = this.getWeChatUserByOpenId(openId);
                        if (wechatUser != null){
                            if (wechatUser.getAccountNumber() != null){
                                // 获取用户信息
                                UserDto user = userService.getUserInfoByPhoneOrUserAccount(wechatUser.getAccountNumber(),null);

                                if (user ==null){
                                    return CommonResult.error(500,"获取用户信息失败");
                                }
                                //将用户信息放入session中
                                token = new UsernamePasswordToken(wechatUser.getAccountNumber(),"ok");
                                session.setAttribute("wechat",true);
                                subject.login(token);
                                //通过后获取参数
                                String redisToken = (String) session.getAttribute(wechatUser.getAccountNumber());
                                UserDto userDto = (UserDto) session.getAttribute(redisToken);
                                session.setAttribute(redisToken, userDto);
                                returnMap.put("token", redisToken);
                                returnMap.put("userInfo", userDto);
                                return CommonResult.success("登录成功", returnMap);
                            }else {
                                //将用户信息放入session中
                                token = new UsernamePasswordToken(openId,"ok");
                                session.setAttribute("wechat",true);
                                session.setAttribute("first",true);
                                subject.login(token);
                                //通过后获取参数
                                String redisToken = (String) session.getAttribute(openId);
                                UserDto userDto = (UserDto) session.getAttribute(redisToken);
                                session.setAttribute(redisToken, userDto);
                                returnMap.put("userInfo", userDto);
                                returnMap.put("openid",openId);
                                return CommonResult.success("登录成功", returnMap);
                            }

                        }else{
                            WechatUser wechatUser1 = new WechatUser();
                            wechatUser1.setOpenid(openId);
                            wechatUser1.setUnionid(unionId);
                            boolean save = this.save(wechatUser1);
                            if(!save){
                                throw new RuntimeException("保存微信信息失败");
                            }
                            //将用户信息放入session中
                            token = new UsernamePasswordToken(openId,"ok");
                            session.setAttribute("wechat",true);
                            session.setAttribute("first",true);
                            subject.login(token);
                            //通过后获取参数
                            String redisToken = (String) session.getAttribute(openId);
                            UserDto userDto = (UserDto) session.getAttribute(redisToken);
                            session.setAttribute(redisToken, userDto);
                            returnMap.put("openid",openId);
                            returnMap.put("userInfo", userDto);
                            return CommonResult.success("登录成功", returnMap);
                        }
                    } catch (Exception e) {

                        return CommonResult.error(500,"业务操作失败");
                    }
                } else {
                    return CommonResult.error(500,"code无效");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("异常打印-->"+e.getMessage());
        }
        return CommonResult.error(500,"服务器繁忙");
    }

    /**
     * @param request
     * @param vo
     * @Description: 微信绑定用户登录
     * @Author: LZH
     * @Date: 2022/2/21 14:09
     */
    @Override
    public CommonResult<Object> weChatBindAccount(HttpServletRequest request, UserVo vo) {
        // 获取参数
        String openid = vo.getOpenid();
        String accountNum = vo.getAccountNum();
        String password = vo.getPassword();

        // 获取shiro对象
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = null;
        Session session = subject.getSession();
        Map<String, Object> returnMap = new HashMap<>(16);

        //根据账号获取用户信息
        UserDto user = userService.getUserInfoByPhoneOrUserAccount(accountNum,null);
        //判断用户是否存在
        if (user == null){
            return CommonResult.error(500,"账号不存在");
        }
        // 判断密码是否一致
        if(!PasswordCryptoTool.checkPassword(password,user.getPassword())){
            return CommonResult.error(500,"密码错误");
        }else {
            //密码验证正确后进行绑定
            WechatUser wechatUser = this.getWeChatUserByOpenId(openid);
            String unionid = wechatUser.getUnionid();
            wechatUser.setAccountNumber(accountNum);
            boolean update = this.updateById(wechatUser);

            /*绑定后进行登录操作*/

            //将用户信息放入session中
            token = new UsernamePasswordToken(user.getAccountNumber() ,"ok");
            session.setAttribute("wechat",true);
            subject.login(token);
            //通过后获取参数
            String redisToken = (String) session.getAttribute(user.getAccountNumber());
            UserDto userDto = (UserDto) session.getAttribute(redisToken);
            session.setAttribute(redisToken, userDto);
            returnMap.put("userInfo", userDto);
            returnMap.put("token", redisToken);

            return update?CommonResult.success("登录成功",returnMap):CommonResult.error(500,"绑定失败");
        }
    }

    public WechatUser getWeChatUserByUnionId(String unionId){
        QueryWrapper<WechatUser> wechatUserQueryWrapper = new QueryWrapper<>();
        wechatUserQueryWrapper.eq("unionid",unionId);
        return this.getOne(wechatUserQueryWrapper);
    }
    public WechatUser getWeChatUserByOpenId(String openId){
        QueryWrapper<WechatUser> wechatUserQueryWrapper = new QueryWrapper<>();
        wechatUserQueryWrapper.eq("openid",openId);
        return this.getOne(wechatUserQueryWrapper);
    }

}
