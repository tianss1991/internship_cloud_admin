package com.above.controller;

import com.above.service.*;
import com.above.utils.CommonResult;
import com.above.vo.user.UserVo;
import com.above.vo.user.WeChatVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * @Description: 微信接口
 * @Author: LZH
 * @Date: 2022/2/21 9:23
 */
@Api(tags = {"微信登录"})
@RequestMapping("/wechat")
@RestController
public class WeChatController {


    @Autowired
    private WechatUserService wechatUserService;


    @ApiOperation("微信小程序登录接口")
    @RequestMapping(value = "/appletLogin", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code",value = "密钥",dataType = "String",required = true),
            @ApiImplicitParam(name = "iv",value = "偏移量",dataType = "String",required = true),
            @ApiImplicitParam(name = "encryptedData",value = "用户类型",dataType = "String",required = true),
    })
    public CommonResult<Object> getOpenId(HttpServletRequest request, @RequestBody WeChatVo vo) {

        if (vo.getCode() == null){
            return CommonResult.error(500,"缺少code");
        }
        if (vo.getIv() == null){
            return CommonResult.error(500,"缺少iv");
        }
        if (vo.getEncryptedData() == null){
            return CommonResult.error(500,"缺少EncryptedData");
        }
        return wechatUserService.weChatLogin(request,vo);
    }


    /**
     * @Description: 微信绑定账号
     * @Author: LZH
     * @Date: 2022/2/21 11:46
     */
    @ApiOperation("微信绑定账号")
    @PostMapping("/wechatBingAccount")
    public CommonResult<Object> wechatBingAccount(HttpServletRequest request,@RequestBody UserVo vo){

        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getAccountNum() == null){
            return CommonResult.error(500,"缺少账号");
        }
        if (vo.getPassword() == null){
            return CommonResult.error(500,"缺少密码");
        }
        if (vo.getOpenid() == null){
            return CommonResult.error(500,"缺少openId");
        }

        return wechatUserService.weChatBindAccount(request, vo);
    }

}
