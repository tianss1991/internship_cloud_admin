package com.above.controller;


import com.above.dto.UserDto;
import com.above.po.AuthRole;
import com.above.po.UserAccount;
import com.above.service.UserAccountService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.UserAccountVo;
import com.above.vo.user.UpdateUserVo;
import com.above.vo.user.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户账号信息表（记录用户关联的手机，邮箱，微信等账号信息） 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Api(tags = {"用户账号信息接口"})
@RestController
@RequestMapping("/userAccount")
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    /**
     * @Description: 实习认证
     * @Author: LZH
     * @Date: 2022/7/5 15:19
     */
    @ApiOperation("实习认证")
    @PostMapping("internshipCertification")
    public CommonResult<Object> internshipCertification( HttpServletRequest request,@RequestBody UserAccountVo vo) {
        String token = MyStringUtils.getRequestToken(request);
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(token);

        try {
            return userAccountService.internshipCertification(userDto,vo,token);
        }catch (RuntimeException e){
            return CommonResult.error(500,e.getMessage());
        }
    }

    @ApiOperation("校验身份")
    @PostMapping("checkRoles")
    public CommonResult<Object> internshipCertification( HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();

        if (subject.hasRole(AuthRole.ADMIN)){
            return CommonResult.success("超级管理员");
        }else
        if (subject.hasRole(AuthRole.SCHOOL_ADMIN)){
            return CommonResult.success("校级管理员");
        }else
        if (subject.hasRole(AuthRole.DEPARTMENT_ADMIN)){
            return CommonResult.success("二级学院管理员");
        }else
        if (subject.hasRole(AuthRole.ADVISER)){
            return CommonResult.success("指导教师");
        }else
        if (subject.hasRole(AuthRole.INSTRUCTOR)){
            return CommonResult.success("辅导员");
        }else
        if (subject.hasRole(AuthRole.VISITOR)){
            return CommonResult.success("游客");
        }else
        if (subject.hasRole(AuthRole.STUDENT)){
            return CommonResult.success("学生");
        }else{
            return CommonResult.success("未知");
        }



    }

}

