package com.above.controller;


import com.above.dto.UserDto;
import com.above.service.SignApplyInfoService;
import com.above.service.SignInfoByStudentService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.SignInfoVo;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * <p>
 * 签到记录表 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Api(tags = {"签到接口"})
@RestController
@RequestMapping("/signInfoByStudent")
public class SignInfoByStudentController {

    @Autowired
    private SignInfoByStudentService signInfoByStudentService;


    /**
     * @Description: 学生获取今天打卡列表
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    @ApiOperation("学生获取今天打卡列表")
    @GetMapping("getTodaySignList")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "时间（yyyy-MM-dd）,不传默认当天",name = "date",required = false),
            @ApiImplicitParam(value = "学生账号登录可不传",name = "studentId",required = false),
            @ApiImplicitParam(value = "若userInfo信息中存在可不传",name = "planId",required = false),
    })
    @RequiresRoles(value = {"student"},logical = Logical.OR)
    public CommonResult<Object> getTodaySignList(HttpServletRequest request,@ApiIgnore SignInfoVo vo){

        //获取当前用户
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (userDto == null){
            return CommonResult.error(401,"未登录");
        }

        try {
            return signInfoByStudentService.getTodaySignList(userDto,vo);
        } catch (ParseException e) {
            return CommonResult.error(500,"时间转化错误");
        }

    }

    /**
     * @Description: 学生获取本月打卡列表
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    @ApiOperation("学生获取本月打卡列表")
    @GetMapping("getMonthSignList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "month",value = "月份",required = true),
    })
    @RequiresRoles(value = {"student"},logical = Logical.OR)
    public CommonResult<Object> getMonthSignList(HttpServletRequest request,@ApiIgnore SignInfoVo vo){

        //获取当前用户
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getMonth() == null){
            return CommonResult.error(500,"缺少月份参数");
        }

        try {
            return signInfoByStudentService.getMonthSignList(userDto,vo);
        } catch (ParseException e) {
            return CommonResult.error(500,"时间转化错误");
        }
    }

    /**
     * @Description: 学生签到明细列表
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    @ApiOperation("学生签到明细列表")
    @GetMapping("getStudentSignDetailList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页数",required = true),
            @ApiImplicitParam(name = "size",value = "每页个数",required = true),
    })
    @RequiresRoles(value = {"student"},logical = Logical.OR)
    public CommonResult<Object> getStudentSignDetailList(HttpServletRequest request,@ApiIgnore SignInfoVo vo){

        //获取当前用户
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo.getPage() == null){
            return CommonResult.error(500,"缺少分页参数");
        }

        return signInfoByStudentService.getStudentSignDetailList(userDto,vo);
    }

    /**
     * @Description: 学生签到明细列表
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    @ApiOperation("学生打卡")
    @PostMapping("studentSignIn")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vo",value = "json参数",dataType = "String",paramType = "body",required = true
                    ,defaultValue = "{\"signId\":0,\"address\":\"\",\"latitude\":\"\",\"longitude\":\"\",\"remark\",\"\"}"),
            @ApiImplicitParam(name = "signId",value = "签到记录id" ,dataType = "Integer"),
            @ApiImplicitParam(name = "address",value = "打卡地点",dataType = "String"),
            @ApiImplicitParam(name = "latitude",value = "经度（用于判断打卡距离）",dataType = "String"),
            @ApiImplicitParam(name = "longitude",value = "纬度（用于判断打卡距离）",dataType = "String"),
            @ApiImplicitParam(name = "remark",value = "备注",dataType = "String"),
    })
    @RequiresRoles(value = {"student"},logical = Logical.OR)
    public CommonResult<Object> studentSignIn(HttpServletRequest request,@RequestBody @ApiIgnore SignInfoVo vo){

        //获取当前用户
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getSignId() == null){
            return CommonResult.error(500,"缺少签到id");
        }
        if (StringUtils.isBlank(vo.getAddress())){
            return CommonResult.error(500,"缺少打卡地点");
        }

        return signInfoByStudentService.studentSignIn(userDto,vo);
    }

    /**
     * @Description: 学生签到明细列表
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    @ApiOperation("获取签到和未签到学生列表")
    @GetMapping("getUnSignStudentList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页数",required = true),
            @ApiImplicitParam(name = "size",value = "每页个数",required = true),
    })
    @RequiresRoles(value = {"adviser"},logical = Logical.OR)
    public CommonResult<Object> getUnSignStudentList(HttpServletRequest request,@ApiIgnore SignInfoVo vo){

        //获取当前用户
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo.getPage() == null){
            return CommonResult.error(500,"缺少分页参数");
        }

        try {
            return signInfoByStudentService.getUnSignStudentList(userDto,vo);
        } catch (ParseException e) {
            return CommonResult.error(500,"时间格式解析错误");
        }
    }


}

