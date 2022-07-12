package com.above.controller;


import com.above.dao.SchoolNoticeMapper;
import com.above.dto.UserDto;
import com.above.service.SchoolNoticeService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.SchoolNoticeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 学校公告 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Api(tags = {"学校公告接口"})
@RestController
@RequestMapping("/schoolNotice")
public class SchoolNoticeController {


    @Autowired
    private SchoolNoticeService schoolNoticeService;

    /**
     * @Description: 发布学校公告
     * @Author: YJH
     * @Date: 2022/6/30 11:18
     */
    @ApiOperation("发布学校公告")
    @RequiresRoles(value = {"admin","schoolAdmin"}, logical = Logical.OR)
    @PostMapping("addNotice")
    public CommonResult<Object> addNotice(HttpServletRequest request,@RequestBody SchoolNoticeVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (StringUtils.isEmpty(vo.getTitle())){
            return CommonResult.error(500,"缺少标题");
        }
        if (StringUtils.isEmpty(vo.getContent())){
            return CommonResult.error(500,"内容不能为空");
        }
        if (vo.getPeriodOfValidity()==null){
            return CommonResult.error(500,"请设置公告有效期");
        }
        if (vo.getStatus()==null || vo.getStatus()==0){
            return CommonResult.error(500,"请设置公告状态");
        }

        return schoolNoticeService.addNotice(userDto,vo);
    }


    /**
     * @Description: 获取学校公告
     * @Author: YJH
     * @Date: 2022/6/29 16:40
     */
    @ApiOperation("查看学校公告")
    @RequiresRoles(value = {"admin","schoolAdmin", "student","departmentAdmin", "teacher", "instructorSchool","instructorDepartment"}, logical = Logical.OR)
    @GetMapping("getNoticeList")
    public CommonResult<Object> getNoticeList(HttpServletRequest request,@RequestBody SchoolNoticeVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (StringUtils.isEmpty(vo.getTitle())){
            return CommonResult.error(500,"暂无学校公告");
        }

            return schoolNoticeService.getNoticeList(userDto,vo);
     }

    /**
     * @Description: 公告详情
     * @Author: YJH
     * @Date: 2022/6/30 10:02
     */
    @ApiOperation("公告详情")
    @RequiresRoles(value = {"admin","schoolAdmin", "student","departmentAdmin", "teacher", "instructorSchool","instructorDepartment"}, logical = Logical.OR)
    @GetMapping("getNotice")
    public CommonResult<Object> getNotice(HttpServletRequest request,@RequestBody SchoolNoticeVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        return schoolNoticeService.getNotice(userDto,vo);
    }
}

