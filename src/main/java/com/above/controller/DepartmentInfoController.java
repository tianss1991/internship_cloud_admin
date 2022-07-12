package com.above.controller;

import com.above.dto.UserDto;
import com.above.po.AuthRole;
import com.above.service.DepartmentInfoService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.BaseVo;
import com.above.vo.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 二级学院 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Api(tags = {"二级学院管理接口"})
@RestController
@RequestMapping("/departmentInfo")
public class DepartmentInfoController {

    @Autowired
    private DepartmentInfoService departmentInfoService;

    /**
     * @Description: 添加二级学院
     * @Author: LZH
     * @Date: 2022/1/12 11:31
     */
    @ApiOperation("添加二级学院")
    @RequiresRoles(value = {"admin","schoolAdmin"}, logical = Logical.OR)
    @PostMapping("addDepartment")
    public CommonResult<Object> addDepartment(HttpServletRequest request,@RequestBody DepartmentVo departmentVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (departmentVo == null){
            return CommonResult.error(500,"缺少参数");
        }
        //根据角色分配
        if (userDto.getUserRoleDto().getRoleCode().equals(AuthRole.SCHOOL_ADMIN)){
            if (userDto.getSchoolIds() == null || userDto.getSchoolIds().size()<=0){
                return CommonResult.error(500,"角色无关联学校");
            }
            departmentVo.setSchoolId(userDto.getSchoolIds().get(0));
        }
        if (departmentVo.getSchoolId() == null){
            return CommonResult.error(500,"缺少学校id");
        }
        if (StringUtils.isBlank(departmentVo.getDepartmentName())){
            return CommonResult.error(500,"缺少二级学院名称");
        }

        try {
            return departmentInfoService.addDepartment(userDto,departmentVo);
        }catch (RuntimeException e){
            return CommonResult.error(500, "删除异常");
        }
    }

    /**
     * @Description: 修改二级学院
     * @Author: LZH
     * @Date: 2022/1/12 11:31
     */
    @ApiOperation("修改二级学院")
    @RequiresRoles(value = {"admin","schoolAdmin"}, logical = Logical.OR)
    @PostMapping("modifyDepartment")
    public CommonResult<Object> modifyDepartment(HttpServletRequest request,@RequestBody DepartmentVo departmentVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (departmentVo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (departmentVo.getDepartmentId() == null){
            return CommonResult.error(500,"缺少二级学院id");
        }

//        try {
            return departmentInfoService.modifyDepartment(userDto,departmentVo);
//        }catch (RuntimeException e){
//            return CommonResult.error(500, "修改异常");
//        }
    }

    /**
     * @Description: 删除二级学院
     * @Author: LZH
     * @Date: 2022/1/12 11:31
     */
    @ApiOperation("删除二级学院")
    @RequiresRoles(value = {"admin","schoolAdmin"}, logical = Logical.OR)
    @PostMapping("deleteDepartment")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> deleteDepartment(HttpServletRequest request,@RequestBody DepartmentVo departmentVo){

        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (departmentVo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (departmentVo.getDepartmentIdList() == null){
            return CommonResult.error(500,"缺少二级学院id");
        }
        //捕获异常
        try {
            return departmentInfoService.deleteDepartment(userDto,departmentVo);
        }catch (RuntimeException e){
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * @Description: 获取二级学院列表
     * @Author: LZH
     * @Date: 2022/1/12 11:30
     */
    @ApiOperation("获取二级学院列表")
    @RequiresRoles(value = {"admin","schoolAdmin", "departmentAdmin", "teacher", "instructorSchool","instructorDepartment"}, logical = Logical.OR)
    @GetMapping("getDepartmentList")
    public CommonResult<Object> getDepartmentList(HttpServletRequest request, DepartmentVo departmentVo){

        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (departmentVo == null){
            return CommonResult.error(500,"缺少参数");
        }

        /*若page==0为不分页*/
        if (departmentVo.getPage()==0){
            //不分页
            return departmentInfoService.getDepartmentWithoutPage(userDto,departmentVo);
        }else {
            //分页
            return departmentInfoService.getDepartmentPageList(userDto,departmentVo);
        }
    }

    /**
     * @Description: 二级学院-班级信息导入
     * @Author: LZH
     * @Date: 2022/1/17 10:02
     */
    @ApiOperation("班级信息导入")
    @RequiresRoles(value = {"schoolAdmin"}, logical = Logical.OR)
    @PostMapping("import")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
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
            return departmentInfoService.importDepartmentInfo(userDto, file);
        } catch (RuntimeException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException(e.getMessage());
//            return CommonResult.error(500, "导入异常,请检查学校名称等信息是否填写正确");
        }
    }


}

