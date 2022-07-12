package com.above.controller;


import com.above.dto.UserDto;
import com.above.service.StudentInfoService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.InternshipApplicationVo;
import com.above.vo.StudentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 学生信息表 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-06-24
 */
@Api(tags = {"学生信息"})
@RestController
@RequestMapping("/studentInfo")
public class StudentInfoController {

    /**
     * @Description: 添加学生接口
     * @Author: GG
     * @Date: 2022/07/04 09:13
     */
    @ApiOperation("添加学生接口")
    @RequiresRoles(value = {"admin","schoolAdmin", "departmentAdmin", "instructor","student"}, logical = Logical.OR)
    @PostMapping("addStudent")
    public CommonResult<Object> addStudent(HttpServletRequest request, @RequestBody StudentVo studentVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断是否有传参数
        if (studentVo == null) {
            return CommonResult.error(500,"缺少参数");
        }
        if (studentVo.getGradeId() == null) {
            return CommonResult.error(500,"请选择年级");
        }
        if (studentVo.getMajorId() == null) {
            return CommonResult.error(500,"请选择专业");
        }
        if (studentVo.getClassId() == null) {
            return CommonResult.error(500,"请选择班级");
        }
        if (studentVo.getStudentNumber() == null) {
            return CommonResult.error(500,"缺少学生学号");
        }
        if (studentVo.getStudentName() == null) {
            return CommonResult.error(500,"缺少学生姓名");
        }
        if (studentVo.getGender() == null) {
            return CommonResult.error(500,"请选择性别");
        }

        try {
            return studentInfoService.addStudent(studentVo,userDto);
        } catch (Exception e) {
            return CommonResult.error(500, "添加异常");
        }
    }

    /**
     * @Description: 修改学生接口
     * @Author: GG
     * @Date: 2022/07/04 09:19
     */
    @ApiOperation("修改学生接口")
    @RequiresRoles(value = {"admin","schoolAdmin", "departmentAdmin", "instructor","student"}, logical = Logical.OR)
    @PostMapping("modifyStudent")
    public CommonResult<Object> modifyStudent(HttpServletRequest request,@RequestBody StudentVo studentVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        //判断是否有传参数
        if (studentVo == null) {
            return CommonResult.error(500,"缺少参数");
        }
        if (studentVo.getStudentId() == null) {
            return CommonResult.error(500,"缺少学生id");
        }

        try {
            return studentInfoService.modifyStudent(studentVo,userDto);
        } catch (Exception e) {
            return CommonResult.error(500, "修改异常");
        }
    }

    /**
     * @Description: 删除学生
     * @Author: LZH
     * @Date: 2022/1/11 11:40
     */
    @ApiOperation("删除学生")
    @RequiresRoles(value = {"admin","schoolAdmin", "departmentAdmin", "instructor","student"}, logical = Logical.OR)
    @PostMapping("deleteStudent")
    public CommonResult<Object> deleteStudent(HttpServletRequest request,@RequestBody StudentVo studentVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        //判断是否有传参数
        if (studentVo == null) {
            return CommonResult.error(500,"缺少参数");
        }
        if (studentVo.getStudentIds() == null && studentVo.getStudentIds().size()==0) {
            return CommonResult.error(500,"缺少学生id");
        }

        try {
            return studentInfoService.deleteStudent(studentVo,userDto);
        } catch (Exception e) {
            return CommonResult.error(500, "删除异常");
        }
    }

    @Autowired
    private StudentInfoService studentInfoService;

    /**
     * @Description:显示学生列表(管理员)
     * @Author: GG
     * @Date: 2022/07/01 11:27
     */
    @ApiOperation("显示学生列表(管理员)")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @GetMapping("displayStudentList")
    public CommonResult<Object> displayStudentList(HttpServletRequest request, StudentVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getPage() == null){
            return CommonResult.error(500,"缺少分页参数");
        }
        if (vo.getSize() == null){
            return CommonResult.error(500,"缺少分页数量参数");
        }
        /*若page为0则默认不分页*/
        if (vo.getPage()==0){
            //不分页
            return studentInfoService.displayStudentListNoPage(vo,userDto);
        }else {
            //分页
            return studentInfoService.displayStudentListPage(vo,userDto);
        }
    }

}

