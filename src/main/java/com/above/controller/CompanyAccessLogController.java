package com.above.controller;


import com.above.dto.UserDto;
import com.above.po.AuthRole;
import com.above.service.CompanyAccessLogService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.CompanyAccessVo;
import com.above.vo.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * <p>
 * 企业寻访记录表 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-07-15
 */
@Api(tags = {" 企业寻访接口"})
@RestController
@RequestMapping("/companyAccessLog")
public class CompanyAccessLogController {

    @Autowired
    private CompanyAccessLogService companyAccessLogService;

    /**
     * @Description: 新增/修改寻坊记录
     * @Author: LZH
     * @Date: 2022/7/15 14:10
     */
    @ApiOperation("新增/修改寻坊记录")
    @RequiresRoles(value = {"admin","schoolAdmin"}, logical = Logical.OR)
    @PostMapping("addAndEdit")
    public CommonResult<Object> addCompanyAccessLog(HttpServletRequest request, @RequestBody CompanyAccessVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }

        if (vo.getId() == null){
            if (StringUtils.isBlank(vo.getCompanyName())){
                return CommonResult.error(500,"缺少公司名称");
            }
            if (StringUtils.isBlank(vo.getTitle())){
                return CommonResult.error(500,"缺少标题");
            }
            if (StringUtils.isBlank(vo.getContent())){
                return CommonResult.error(500,"缺少内容");
            }
            if (StringUtils.isBlank(vo.getAddress())){
                return CommonResult.error(500,"缺少公司地址");
            }
            if (StringUtils.isBlank(vo.getImgUrl())){
                return CommonResult.error(500,"缺少图片名称");
            }

            return companyAccessLogService.addCompanyAccessLog(userDto,vo);
        }else {
            return companyAccessLogService.editCompanyAccessLog(userDto,vo);
        }

    }

    /**
     * @Description: 获取寻坊记录列表
     * @Author: LZH
     * @Date: 2022/7/15 14:10
     */
    @ApiOperation("获取寻坊记录列表")
    @RequiresRoles(value = {"admin","schoolAdmin","departmentAdmin","instructor","adviser"}, logical = Logical.OR)
    @GetMapping("getCompanyAccessList")
    public CommonResult<Object> getCompanyAccessList(HttpServletRequest request, CompanyAccessVo vo){
        //从session获取user
        Subject subject = SecurityUtils.getSubject();
        UserDto userDto =(UserDto) subject.getSession().getAttribute(MyStringUtils.getRequestToken(request));
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        boolean isAdmin = false;
        //根据不同角色获取不同列表
        switch (roleCode){
            case AuthRole.ADMIN :{
                isAdmin = true;
                break;
            }
            case AuthRole.SCHOOL_ADMIN: {
                isAdmin = true;
                vo.setSchoolIdList(userDto.getSchoolIds());
                break;
            }
            case AuthRole.DEPARTMENT_ADMIN: {
                isAdmin = true;
                vo.setDepartmentIdList(userDto.getDepartmentIds());
                break;
            }
            default : break;
        }
        //根据不同端获取列表
        if (isAdmin){
            return companyAccessLogService.getAllCompanyAccess(userDto,vo);
        }else {
            return companyAccessLogService.getMyCompanyAccess(userDto,vo);
        }

    }


    /**
     * @Description: 设置寻坊次数
     * @Author: LZH
     * @Date: 2022/7/15 14:10
     */
    @ApiOperation("设置寻坊次数")
    @RequiresRoles(value = {"admin","schoolAdmin"}, logical = Logical.OR)
    @PostMapping("setSize")
    public CommonResult<Object> setSize(HttpServletRequest request, @RequestBody CompanyAccessVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }

        if (vo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        if ( vo.getInstructorSize() == null){
            return CommonResult.error(500,"请设置辅导员寻坊次数");
        }
        if ( vo.getAdviserSize() == null){
            return CommonResult.error(500,"请设置指导教师寻坊次数");
        }

        return companyAccessLogService.setSize(userDto,vo);

    }

    /**
     * @Description: 获取设置寻坊次数列表
     * @Author: LZH
     * @Date: 2022/7/15 14:10
     */
    @ApiOperation("获取设置寻坊次数列表")
    @RequiresRoles(value = {"admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @GetMapping("getSetSizeList")
    public CommonResult<Object> getSetSizeList(HttpServletRequest request, CompanyAccessVo vo){
        //从session获取user
        Subject subject = SecurityUtils.getSubject();
        UserDto userDto =(UserDto) subject.getSession().getAttribute(MyStringUtils.getRequestToken(request));
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }

        //根据不同角色获取不同列表
        switch (roleCode){
            case AuthRole.SCHOOL_ADMIN: {
                vo.setSchoolIdList(userDto.getSchoolIds());
                break;
            }
            case AuthRole.DEPARTMENT_ADMIN: {
                vo.setDepartmentIdList(userDto.getDepartmentIds());
                break;
            }
            default : break;
        }

        return companyAccessLogService.getSizeWithPlan(userDto,vo);


    }

}

