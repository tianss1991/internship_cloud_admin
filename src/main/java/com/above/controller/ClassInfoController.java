package com.above.controller;


import com.above.dto.UserDto;
import com.above.po.AuthRole;
import com.above.service.ClassInfoService;
import com.above.service.DepartmentInfoService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.ClassVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 班级 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Api(tags = {"班级接口"})
@RestController
@RequestMapping("/classInfo")
public class ClassInfoController {
    @Autowired
    private ClassInfoService classInfoService;
    @Autowired
    private DepartmentInfoService departmentInfoService;

    /**
     * @Description: 添加班级
     * @Author: LZH
     * @Date: 2022/1/12 11:31
     */
    @ApiOperation("添加班级")
    @RequiresRoles(value = {"admin","schoolAdmin" , "departmentAdmin"}, logical = Logical.OR)
    @PostMapping("addClassInfo")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> addClassInfo(HttpServletRequest request, @RequestBody ClassVo classVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (classVo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (userDto.getUserRoleDto().getRoleCode().equals(AuthRole.DEPARTMENT_ADMIN)){
            List<Integer> departmentIds = userDto.getDepartmentIds();
            if (departmentIds == null || departmentIds.size() <= 0){
                return CommonResult.error(500,"未找到关联学校二级学院");
            }
            classVo.setSchoolId(departmentInfoService.getById(departmentIds.get(0)).getSchoolId());
        }
        if (classVo.getSchoolId() == null){
            return CommonResult.error(500,"缺少学校id");
        }
        if (classVo.getDepartmentId() == null){
            return CommonResult.error(500,"缺少二级学院id");
        }
        if (classVo.getGradeId() == null){
            return CommonResult.error(500,"缺少年级id");
        }
        if (classVo.getMajorId() == null){
            return CommonResult.error(500,"缺少专业id");
        }
        if (StringUtils.isEmpty(classVo.getClassName())){
            return CommonResult.error(500,"缺少班级名称");
        }

        try {
            return classInfoService.addClass(userDto,classVo);
        }catch (RuntimeException e){
            return CommonResult.error(500, e.getMessage());
        }

    }

    /**
     * @Description: 修改班级
     * @Author: LZH
     * @Date: 2022/1/12 11:31
     */
    @ApiOperation("修改班级")
    @RequiresRoles(value = {"admin", "departmentAdmin","schoolAdmin"}, logical = Logical.OR)
    @PostMapping("modifyClassInfo")
    public CommonResult<Object> modifyClassInfo(HttpServletRequest request,@RequestBody ClassVo classVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (classVo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (classVo.getClassId() == null){
            return CommonResult.error(500,"缺少班级id");
        }

        return classInfoService.modifyClass(userDto,classVo);
    }

    /**
     * @Description: 删除班级
     * @Author: LZH
     * @Date: 2022/1/12 11:31
     */
    @ApiOperation("删除班级")
    @RequiresRoles(value = {"admin", "departmentAdmin","schoolAdmin"}, logical = Logical.OR)
    @PostMapping("deleteClassInfo")
    public CommonResult<Object> deleteClassInfo(HttpServletRequest request,@RequestBody ClassVo classVo){

        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (classVo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (classVo.getClassIdList() == null){
            return CommonResult.error(500,"缺少班级id");
        }
        try {
            return classInfoService.deleteClass(userDto,classVo);
        }catch (RuntimeException e){
            return CommonResult.error(500, "删除异常");
        }
    }

    /**
     * @Description: 获取班级列表
     * @Author: LZH
     * @Date: 2022/1/12 11:30
     */
    @ApiOperation("获取班级列表")
    @RequiresRoles(value = {"admin","schoolAdmin", "departmentAdmin", "teacher", "instructorSchool","instructorDepartment"}, logical = Logical.OR)
    @GetMapping("getClassList")
    public CommonResult<Object> getClassList(HttpServletRequest request, ClassVo classVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (classVo == null){
            return CommonResult.error(500,"缺少参数");
        }

        /*若page==0为不分页*/
        if (classVo.getPage()==0){
            //不分页
            return classInfoService.getClassWithoutPage(userDto,classVo);
        }else {
            //分页
            return classInfoService.getClassPageList(userDto,classVo);
        }
    }

    /**
     * @Description: 教师获取二级学院-班级列表
     * @Author: LZH
     * @Date: 2022/4/6 8:34
     */
    @ApiOperation("教师获取二级学院-班级列表")
    @RequiresRoles(value = {"admin","schoolAdmin", "departmentAdmin", "teacher", "instructorSchool","instructorDepartment"}, logical = Logical.OR)
    @GetMapping("getDepartmentAndClass")
    public CommonResult<Object> getDepartmentAndClass(HttpServletRequest request, ClassVo classVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        return classInfoService.getDepartmentAndClass(userDto,classVo);
    }

    /**
     * @Description: 二级学院-班级信息导入
     * @Author: LZH
     * @Date: 2022/1/17 10:02
     */
    @ApiOperation("班级信息导入")
    @RequiresRoles(value = {"departmentAdmin"}, logical = Logical.OR)
    @PostMapping("import")
    public CommonResult<Object> importSchoolInfo(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (org.springframework.util.StringUtils.isEmpty(file)){
            return CommonResult.error(500,"缺少文件");
        }
        if (file.isEmpty()){
            return CommonResult.error(500,"缺少文件");
        }
        try {
            return classInfoService.importClass(userDto, file);
        } catch (RuntimeException e) {
            return CommonResult.error(500, "导入异常，请检查学校、二级学院信息是否为当前管理的二级学院");
        }
    }

}

