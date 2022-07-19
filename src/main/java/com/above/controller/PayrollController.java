package com.above.controller;


import com.above.dto.UserDto;
import com.above.service.PayrollService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.PayrollVo;
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
 * 工资单 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Api(tags = {"工资单接口"})
@RestController
@RequestMapping("/payroll")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    /**
     * @Description: 新增工资单
     * @Author: YJH
     * @Date: 2022/7/4 8:30
     */
    @ApiOperation("新增工资单")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @PostMapping("addPayroll")
    public CommonResult<Object> addPayroll(HttpServletRequest request, @RequestBody PayrollVo vo) {
        //从session获取user
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        //判断参数
        if (vo == null) {
            return CommonResult.error(500, "缺少参数");
        }
        if (StringUtils.isEmpty(vo.getCompanyName())) {
            return CommonResult.error(500, "实习单位不能为空");
        }
        if (StringUtils.isEmpty(vo.getJobName())) {
            return CommonResult.error(500, "实习岗位不能为空");
        }
        if (vo.getSalary() == null) {
            return CommonResult.error(500, "请输入实习工资");
        }
        if (vo.getDateTime() == null) {
            return CommonResult.error(500, "请选择时间");
        }

        try {
            return payrollService.addPayroll(userDto, vo);
        } catch (RuntimeException e) {
            return CommonResult.error(500, e.getMessage());
        }

    }

    /**
     * @Description: 查询工资单列表-学生端
     * @Author: YJH
     * @Date: 2022/7/4 10:52
     */
    @ApiOperation("查看工资单-学生端")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @GetMapping("getStudentPayrollList")
    public CommonResult<Object> getStudentPayrollList(HttpServletRequest request, PayrollVo vo) {
        //从session获取user
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        //判断参数
        if (vo == null) {
            return CommonResult.error(500, "缺少参数");
        }
        try {
            return payrollService.getStudentPayrollList(userDto, vo);
        } catch (RuntimeException e) {
            return CommonResult.error(500, e.getMessage());
        }

    }

    /**
     * @Description: 查询工资单列表-辅导员端
     * @Author: YJH
     * @Date: 2022/7/4 16:00
     */
    @ApiOperation("查看工资单-辅导员端")
    @RequiresRoles(value = {"instructor"}, logical = Logical.OR)
    @GetMapping("getInstructorPayrollList")
    public CommonResult<Object> getInstructorPayrollList(HttpServletRequest request,PayrollVo vo) {
        //从session获取user
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (StringUtils.isEmpty(userDto.getAccountNumber())) {
            return CommonResult.error(500, "该辅导员不存在");
        }
        try {
            return payrollService.getInstructorPayrollList(userDto, vo);
        } catch (RuntimeException e) {
            return CommonResult.error(500, e.getMessage());
        }

    }

    /**
     * @Description: 查询工资单详情
     * @Author: YJH
     * @Date: 2022/7/6 14:20
     */
    @ApiOperation("查询工资单详情")
    @RequiresRoles(value = {"student", "instructor"}, logical = Logical.OR)
    @GetMapping("getPayrollDetail")
    public CommonResult<Object> getPayrollDetail(HttpServletRequest request,PayrollVo vo) {
        //从session获取user
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        try {
            return payrollService.getPayrollDetail(userDto, vo);
        } catch (RuntimeException e) {
            return CommonResult.error(500, e.getMessage());
        }
    }

    /**
     * @Description: pc端工资单管理
     * @Author: YJH
     * @Date:
     */
    @ApiOperation("pc端工资单管理")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @GetMapping("Payroll")
    public CommonResult<Object> Payroll(HttpServletRequest request,@RequestBody PayrollVo vo) {
        //从session获取user
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        try {
            return payrollService.Payroll(userDto, vo);
        } catch (RuntimeException e) {
            return CommonResult.error(500, e.getMessage());
        }
    }


}

