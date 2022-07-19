package com.above.controller;


import com.above.dto.UserDto;
import com.above.service.TeacherInfoService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.BaseVo;
import com.above.vo.TeacherVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 教师信息表 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Slf4j
@Api(tags = {"教师管理接口"})
@RestController
@RequestMapping("/teacherInfo")
public class TeacherInfoController {

    @Autowired
    private TeacherInfoService teacherInfoService;

    /**
     * @Description: 添加教师接口
     * @Author: LZH
     * @Date: 2022/1/11 10:32
     */
    @ApiOperation("添加教师接口")
    @RequiresRoles(value = {"admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @PostMapping("addTeacher")
    public CommonResult<Object> addTeacherInfo(HttpServletRequest request, @RequestBody TeacherVo teacherVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断是否有传参数
        if (teacherVo == null) {
            return CommonResult.error(500,"缺少参数");
        }
        if (StringUtils.isBlank(teacherVo.getWorkNumber())) {
            return CommonResult.error(500,"缺少教师工号");
        }
        if (StringUtils.isBlank(teacherVo.getTeacherName())) {
            return CommonResult.error(500,"缺少教师姓名");
        }
        if (teacherVo.getGender() == null) {
            return CommonResult.error(500,"缺少教师性别");
        }


        try {
            return teacherInfoService.insertTeacherInfo(userDto,teacherVo);
        } catch (Exception e) {
            return CommonResult.error(500, "添加异常");
        }
    }

    /**
     * @Description: 修改教师接口
     * @Author: LZH
     * @Date: 2022/1/11 11:27
     */
    @ApiOperation("修改教师接口")
    @RequiresRoles(value = {"admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @PostMapping("modifyTeacher")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> modifyTeacher(HttpServletRequest request,@RequestBody TeacherVo teacherVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        //判断是否有传参数
        if (teacherVo == null) {
            return CommonResult.error(500,"缺少参数");
        }
        if (teacherVo.getTeacherId() == null) {
            return CommonResult.error(500,"缺少教师id");
        }

        try {
            return teacherInfoService.modifyTeacher(userDto,teacherVo);
        } catch (RuntimeException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("异常打印-->"+e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @Description: 删除教师
     * @Author: LZH
     * @Date: 2022/1/11 11:40
     */
    @ApiOperation("删除教师")
    @RequiresRoles(value = {"admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @PostMapping("deleteTeacher")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> deleteTeacher(HttpServletRequest request,@RequestBody TeacherVo teacherVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        //判断是否有传参数
        if (teacherVo == null) {
            return CommonResult.error(500,"缺少参数");
        }
        try {
            //根据传入的不同list执行不同的删除
            if (teacherVo.getTeacherIdList() != null && teacherVo.getTeacherIdList().size() > 0) {
                //根据教师id删除
                return teacherInfoService.deleteTeacher(userDto,teacherVo);
            }else if (teacherVo.getSchoolIdList() != null && teacherVo.getSchoolIdList().size() > 0) {
                //根据学校id删除
                return teacherInfoService.deleteTeacherBySchoolId(userDto,teacherVo);
            }else if (teacherVo.getDepartmentIdList() != null && teacherVo.getDepartmentIdList().size() > 0) {
                //根据二级学院id删除
                return teacherInfoService.deleteTeacherByDepartmentId(userDto,teacherVo);
            }else {
                return CommonResult.error(500,"请选择要删除的教师");
            }
        } catch (RuntimeException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @Description: 获取教师列表
     * @Author: LZH
     * @Date: 2022/1/11 11:55
     */
    @ApiOperation("获取教师列表")
    @RequiresRoles(value = {"admin", "departmentAdmin", "schoolAdmin","teacher", "instructorSchool","instructorDepartment"}, logical = Logical.OR)
    @GetMapping("getTeacherList")
    public CommonResult<Object> getTeacherList(HttpServletRequest request,TeacherVo teacherVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        //判断是否有传参数
        if (teacherVo == null) {
            return CommonResult.error(500,"缺少参数");
        }
        if (teacherVo.getPage() == null) {
            return CommonResult.error(500,"缺少page");
        }
        /*若page为0则默认不分页*/
        if (teacherVo.getPage()==0){
            //不分页
            return teacherInfoService.getTeacherWithoutList(userDto,teacherVo);
        }else {
            //分页
            return teacherInfoService.getTeacherPageList(userDto,teacherVo);
        }

    }

    /**
     * @Description: 教师信息导入接口
     * @Author: LZH
     * @Date: 2022/1/13 16:32
     */
    @ApiOperation("教师信息导入接口")
    @RequiresRoles(value = {"admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @PostMapping("importTeacherInfo")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> importTeacherInfo(HttpServletRequest request, @Param("file")MultipartFile file, BaseVo baseVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (file == null){
            return CommonResult.error(500,"缺少文件");
        }
        if (file.isEmpty()){
            return CommonResult.error(500,"缺少文件");
        }
        try {
            return teacherInfoService.importTeacherInfo(userDto, file,baseVo);
        } catch (RuntimeException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException(e.getMessage());
        }
    }
}

