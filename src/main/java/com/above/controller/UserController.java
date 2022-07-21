package com.above.controller;


import com.above.config.redis.RedisOperator;
import com.above.config.sms.SmsConfig;
import com.above.dto.UserDto;
import com.above.po.AuthRole;
import com.above.service.UserService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.InternshipPlanInfoVo;
import com.above.vo.user.UpdateUserVo;
import com.above.vo.user.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * <p>
 * 用户信息表（只存储用户状态与密码） 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisOperator redisOperator;

    /**
     * @Decription: 获取验证码
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/10 13:55
     */
    @ApiOperation("获取验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号码")
    })
    @GetMapping("getCode")
    public CommonResult<Object> getCode(@ApiIgnore UserVo userVo) {
        return userService.getCode(userVo);
    }

    /**
     * @Description: 登录
     * @Author: LZH
     * @Date: 2022/6/21 14:03
     */
    @ApiOperation("登录")
    @PostMapping("/login")
    public CommonResult<Object> login(HttpServletRequest request,@RequestBody UserVo userVo) {
        return this.userService.login(userVo);
    }

    /**
     * @Description: 根据token获取用户信息
     * @Author: LZH
     * @Date: 2022/7/11 11:39
     */
    @ApiOperation("根据token获取用户信息")
    @GetMapping("getUserInfoByToken")
    public CommonResult<Object> getUserInfoByToken(HttpServletRequest request) {

        return userService.getUserInfoByToken(request);

    }

    /**
    *@author: GG
    *@data: 2022/7/20 11:50
    *@function:拿到教师实习计划并放到token中
    */
    @ApiOperation("拿到教师实习计划并放到token中")
    @GetMapping("getInternshipPlanInfoByTeacher")
    public CommonResult<Object> getInternshipPlanInfoByTeacher(HttpServletRequest request, InternshipPlanInfoVo vo) {
        if(vo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划参数");
        }
        return userService.getInternshipPlanInfoByTeacher(request,vo);
    }


    /**
     * @Description: 修改用户密码
     * @Author: LZH
     * @Date: 2022/7/11 11:39
     */
    @ApiOperation("修改用户信息")
    @PostMapping("updateUserInfo")
    public CommonResult<Object> updateUserInfo(@RequestBody UpdateUserVo updateUserVo, HttpServletRequest request) {
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        updateUserVo.setId(userDto.getId());
        updateUserVo.setOldTelephone(userDto.getTelephone());
        return this.userService.updateUserInfo(updateUserVo);
    }

    /**
     * @Description: 退出登录
     * @Author: LZH
     * @Date: 2022/7/11 11:39
     */
    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public CommonResult<Object> logOut(HttpServletRequest request) {
        // 获取token
        String token = MyStringUtils.getRequestToken(request);
        Session session = SecurityUtils.getSubject().getSession();
        UserDto userDto = (UserDto) session.getAttribute(token);
        if (userDto == null) {
            return CommonResult.error(500, "您已退出登录，无需退出");
        }
        session.removeAttribute(userDto.getTelephone());
        session.removeAttribute(userDto.getAccountNumber());
        session.removeAttribute(token);
        SecurityUtils.getSubject().logout();
        return CommonResult.success("退出成功", null);
    }

    /**
     * @Description: 重置密码接口
     * @Author: LZH
     * @Date: 2022/2/22 10:40
     */
    @ApiOperation("重置密码接口")
    @RequiresRoles(value = {"admin","schoolAdmin" ,"departmentAdmin"}, logical = Logical.OR)
    @PostMapping("/resetPassword")
    public CommonResult<Object> resetPassword(HttpServletRequest request,@RequestBody UserVo vo) {
        // 获取token
        String token = MyStringUtils.getRequestToken(request);
        Session session = SecurityUtils.getSubject().getSession();
        UserDto userDto = (UserDto) session.getAttribute(token);
        //判空
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getUserId() == null){
            return CommonResult.error(500,"缺少参数");
        }

        return this.userService.resetPassword(userDto,vo);
    }



    /**
     * @Description: 用户选择切换身份
     * @Author: LZH
     * @Date: 2022/5/23 11:06
     */
    @ApiOperation("用户选择切换身份")
    @PostMapping("/chooseRole")
    public CommonResult<Object> chooseRole(HttpServletRequest request,@RequestBody UserVo userVo){

        if (userVo.getRoleId() == null) {
            return CommonResult.error(500,"权限不存在");
        }

        return userService.chooseRole(request,userVo);
    }

    /**
     * @Description: 用户获取权限列表
     * @Author: LZH
     * @Date: 2022/5/23 11:18
     */
    @ApiOperation("用户获取权限列表")
    @GetMapping("/getChooseRoleList")
    public CommonResult<Object> getChooseRoleList(HttpServletRequest request,UserVo userVo){
        // 获取token
        String token = MyStringUtils.getRequestToken(request);
        Session session = SecurityUtils.getSubject().getSession();
        UserDto userDto = (UserDto) session.getAttribute(token);
        return userService.getChooseRoleList(userDto,userVo);
    }
}

