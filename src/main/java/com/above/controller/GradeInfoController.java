package com.above.controller;


import com.above.dto.UserDto;
import com.above.service.GradeInfoService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.GradeVo;
import com.above.vo.InternshipApplicationVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 年级 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Api(tags = {"年级接口"})
@RestController
@RequestMapping("/gradeInfo")
public class GradeInfoController {
    @Autowired
    private GradeInfoService gradeInfoService;


    /**
     * @Description: 新建年级
     * @Author: GG
     * @Date: 2022/06/29 14:12
     */
    @ApiOperation("新建年级")
    @RequiresRoles(value = {"student","admin","schoolAdmin","departmentAdmin","instructor","teacher"}, logical = Logical.OR)
    @PostMapping("addGradeInfo")
    public CommonResult<Object> addGradeInfo(HttpServletRequest request,@RequestBody GradeVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getSchoolId() == null){
            return CommonResult.error(500,"缺少学校id");
        }
        if (vo.getGradeYear() == null){
            return CommonResult.error(500,"缺少年级名称");
        }
        try {
            return gradeInfoService.addGradeInfo(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"新建年级失败");
        }
    }


    /**
     * @Description: 删除年级
     * @Author: GG
     * @Date: 2022/06/29 14:12
     */
    @ApiOperation("删除年级")
    @RequiresRoles(value = {"student","admin","schoolAdmin","departmentAdmin","instructor","teacher"}, logical = Logical.OR)
    @PostMapping("deleteGradeInfo")
    public CommonResult<Object> deleteGradeInfo(HttpServletRequest request,@RequestBody GradeVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getGradeId() == null){
            return CommonResult.error(500,"缺少年级id");
        }
        try {
            return gradeInfoService.deleteGradeInfo(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"删除年级失败");
        }
    }

    /**
     * @Description: 修改年级
     * @Author: GG
     * @Date: 2022/06/29 14:12
     */
    @ApiOperation("修改年级")
    @RequiresRoles(value = {"student","admin","schoolAdmin","departmentAdmin","instructor","teacher"}, logical = Logical.OR)
    @PostMapping("modifyGradeInfo")
    public CommonResult<Object> modifyGradeInfo(HttpServletRequest request,@RequestBody GradeVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getGradeId() == null){
            return CommonResult.error(500,"缺少年级id");
        }
        try {
            return gradeInfoService.modifyGradeInfo(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"修改年级失败");
        }

    }

    /**
     * @Description: 通过年级id查询年级
     * @Author: GG
     * @Date: 2022/06/29 14:12
     */
    @ApiOperation("通过年级id查询年级")
    @RequiresRoles(value = {"student","admin","schoolAdmin","departmentAdmin","instructor","teacher"}, logical = Logical.OR)
    @GetMapping("queryGradeInfo")
    public CommonResult<Object> queryGradeInfo(HttpServletRequest request, GradeVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getGradeId() == null){
            return CommonResult.error(500,"缺少年级id");
        }
        try {
            return gradeInfoService.queryGradeInfo(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"通过年级id查询年级失败");
        }
    }

    /**
     * @Description: 通过学校查询所有年级
     * @Author: GG
     * @Date: 2022/06/29 14:12
     */
    @ApiOperation("通过学校查询所有年级")
    @RequiresRoles(value = {"student","admin","schoolAdmin","departmentAdmin","instructor","teacher"}, logical = Logical.OR)
    @GetMapping("queryGradeList")
    public CommonResult<Object> queryGradeList(HttpServletRequest request, GradeVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getSchoolId() == null){
            return CommonResult.error(500,"缺少学校id");
        }
        try {
            return gradeInfoService.queryGradeList(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"通过学校查询所有年级失败");
        }
    }
}

