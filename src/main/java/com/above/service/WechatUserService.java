package com.above.service;

import com.above.po.WechatUser;
import com.above.utils.CommonResult;
import com.above.vo.user.UserVo;
import com.above.vo.user.WeChatVo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 微信用户表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-02-20
 */
public interface WechatUserService extends IService<WechatUser> {

    /**
     * @Description: 微信小程序登录
     * @Author: LZH
     * @Date: 2022/2/21 14:06
     */
    CommonResult<Object> weChatLogin(HttpServletRequest request, WeChatVo vo);

    /**
     * @Description: 微信绑定用户登录
     * @Author: LZH
     * @Date: 2022/2/21 14:09
     */
    CommonResult<Object> weChatBindAccount(HttpServletRequest request, UserVo vo);

}
