package com.above.controller;


import com.above.dto.UserDto;
import com.above.service.InternshipInfoByStudentService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.DepartmentVo;
import com.above.vo.InternshipApplicationVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 学生实习信息表 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Api(tags = {"实习申请接口"})
@RestController
@RequestMapping("/internshipInfoByStudent")
public class InternshipInfoByStudentController {
    @Autowired
    private InternshipInfoByStudentService internshipInfoByStudentService;

    /**
     * @Description: 提交实习申请与修改实习申请
     * @Author: GG
     * @Date: 2022/06/21 16:07
     */
    @ApiOperation("提交实习申请与修改实习申请")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @PostMapping("internshipApplySubmit")
    public CommonResult<Object> internshipApplySubmit(HttpServletRequest request, @RequestBody InternshipApplicationVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
//        Session session = SecurityUtils.getSubject().getSession();
//        UserDto userDto2 = (UserDto) session.getAttribute(session.getId());
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if(vo.getRelationPlanId()==null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        if(vo.getCompanyName()==null){
            return CommonResult.error(500,"实习单位名称不能为空");
        }
        if(vo.getSocialCode()==null){
            return CommonResult.error(500,"社会统一信用代码不能为空");
        }
        if(vo.getCompanyScale()==null){
            return CommonResult.error(500,"企业规模不能为空");
        }
        if(vo.getCompanyTelephone()==null){
            return CommonResult.error(500,"企业联系电话不能为空");
        }
        if(vo.getCompanyNature()==null){
            return CommonResult.error(500,"公司性质不能为空");
        }
        if(vo.getCompanyIndustry()==null){
            return CommonResult.error(500,"所属行业不能为空");
        }
        if(vo.getCompanyArea()==null){
            return CommonResult.error(500,"所属区域不能为空");
        }
        if(vo.getCompanyAddress()==null){
            return CommonResult.error(500,"公司详细地址不能为空");
        }
        if(vo.getJobDepartment()==null){
            return CommonResult.error(500,"部门名称不能为空");
        }
        if(vo.getJobName()==null){
            return CommonResult.error(500,"岗位名称不能为空");
        }
        if(vo.getCompanyTeacher()==null){
            return CommonResult.error(500,"企业老师不能为空");
        }
        if(vo.getCompanyTeacherTelephone()==null){
            return CommonResult.error(500,"企业老师电话不能为空");
        }
        if(vo.getJobType()==null){
            return CommonResult.error(500,"岗位类别不能为空");
        }
        if(vo.getJobArea()==null){
            return CommonResult.error(500,"岗位所在区域不能为空");
        }
        if(vo.getJobAddress()==null){
            return CommonResult.error(500,"岗位地址不能为空");
        }
        if(vo.getStartTime()==null){
            return CommonResult.error(500,"开始时间不能为空");
        }
        if(vo.getEndTime()==null){
            return CommonResult.error(500,"结束时间不能为空");
        }
        if(vo.getApproach()==null){
            return CommonResult.error(500,"实习方式不能为空");
        }
        if(vo.getIsAboral()==null){
            return CommonResult.error(500,"专业对口与否不能为空");
        }
        if(vo.getIsSpecial()==null){
            return CommonResult.error(500,"是否有特殊专业情况不能为空");
        }
        try{
            return internshipInfoByStudentService.internshipApplySubmitOrUpdate(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"提交或编辑失败");
        }
    }


    /**
     * @Description: 删除实习申请
     * @Author: GG
     * @Date: 2022/06/22 13:47
     */
//    @ApiOperation("删除实习申请")
//    @RequiresRoles(value = {"student"}, logical = Logical.OR)
//    @PostMapping("internshipApplyDelete")
//    public CommonResult<Object> internshipApplyDelete(HttpServletRequest request, @RequestBody InternshipApplicationVo vo){
//        //从session获取user
//        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
//        //判断参数
//        if (vo == null){
//            return CommonResult.error(500,"缺少参数");
//        }
//        if (vo.getInternshipId() == null){
//            return CommonResult.error(500,"缺少实习申请id");
//        }
//        return internshipInfoByStudentService.internshipApplyDelete(vo,userDto);
//    }

    /**
     * @Description: 展示单条实习申请
     * @Author: GG
     * @Date: 2022/06/22 13:47
     */
    @ApiOperation("展示单条实习申请")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @GetMapping("internshipApplyDisplaySingle")
    public CommonResult<Object> internshipApplyDisplaySingle(HttpServletRequest request, InternshipApplicationVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getInternshipId() == null){
            return CommonResult.error(500,"缺少实习申请id");
        }
        try{
            return internshipInfoByStudentService.internshipApplyDisplaySingle(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"展示单条实习申请出错");
        }
    }

    /**
     * @Description: 教师审核
     * @Author: GG
     * @Date: 2022/06/22 13:47
     */
    @ApiOperation("教师审核")
    @RequiresRoles(value = {"teacher"}, logical = Logical.OR)
    @PostMapping("internshipInfoCheck")
    public CommonResult<Object> internshipInfoCheck(HttpServletRequest request, @RequestBody InternshipApplicationVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getInternshipId() == null){
            return CommonResult.error(500,"缺少实习申请id");
        }
        if (vo.getInternshipStatus() == null){
            return CommonResult.error(500,"缺少审核状态");
        }
        if (vo.getCheckStatus() == null){
            return CommonResult.error(500,"缺少修改类别");
        }
        if (vo.getFailReason() == null){
            return CommonResult.error(500,"缺少失败理由");
        }
        try{
            return internshipInfoByStudentService.internshipInfoCheck(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"教师审核失败");
        }

    }

    /**
     * @Description: 实习岗位列表
     * @Author: GG
     * @Date: 2022/06/22 13:47
     */
    @ApiOperation("实习岗位列表（学生）")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @GetMapping("internshipApplyDisplayList")
    public CommonResult<Object> internshipApplyDisplayList(HttpServletRequest request, InternshipApplicationVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getRelationPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        try{
            return internshipInfoByStudentService.internshipApplyDisplayList(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"实习岗位列表展示出错");
        }

    }

    /**
     * @Description: 提交免实习申请与修改实习申请
     * @Author: GG
     * @Date: 2022/06/22 16:07
     */
    @ApiOperation("提交免实习申请与修改实习申请")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @PostMapping("dismissInternshipApplySubmit")
    public CommonResult<Object> dismissInternshipApplySubmit(HttpServletRequest request, @RequestBody InternshipApplicationVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getReason() == null){
            return CommonResult.error(500,"缺少申请原因");
        }
        if (vo.getDispositon() == null){
            return CommonResult.error(500,"缺少去向");
        }
        if (vo.getFileUrl() == null){
            return CommonResult.error(500,"缺少证明文件");
        }
        if(vo.getRelationPlanId()==null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        try{
            return internshipInfoByStudentService.dismissInternshipApplySubmit(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"提交免实习申请与修改实习申请失败");
        }
    }

    /**
     * @Description: 删除免实习申请
     * @Author: GG
     * @Date: 2022/06/22 16:07
     */
//    @ApiOperation("删除免实习申请")
//    @RequiresRoles(value = {"student"}, logical = Logical.OR)
//    @PostMapping("dismissInternshipApplyDelete")
//    public CommonResult<Object> dismissInternshipApplyDelete(HttpServletRequest request, @RequestBody InternshipApplicationVo vo){
//        //从session获取user
//        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
//        //判断参数
//        if (vo == null){
//            return CommonResult.error(500,"缺少参数");
//        }
//        if (vo.getInternshipId() == null){
//            return CommonResult.error(500,"缺少实习申请id");
//        }
//        return internshipInfoByStudentService.dismissInternshipApplyDelete(vo,userDto);
//    }

    /**
     * @Description: 展示单条免实习申请
     * @Author: GG
     * @Date: 2022/06/22 16:07
     */
    @ApiOperation("展示单条免实习申请")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @GetMapping("dismissInternshipApplyDisplaySingle")
    public CommonResult<Object> dismissInternshipApplyDisplaySingle(HttpServletRequest request, InternshipApplicationVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getInternshipId() == null){
            return CommonResult.error(500,"缺少实习申请id");
        }
        try{
            return internshipInfoByStudentService.dismissInternshipApplyDisplaySingle(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"展示单条免实习申请出错");
        }
    }


//    /**
//     * @Description: 免实习岗位列表
//     * @Author: GG
//     * @Date: 2022/06/22 16:47
//     */
//    @ApiOperation("免实习岗位列表")
//    @RequiresRoles(value = {"student"}, logical = Logical.OR)
//    @GetMapping("dismissInternshipApplyDisplayList")
//    public CommonResult<Object> dismissInternshipApplyDisplayList(HttpServletRequest request, InternshipApplicationVo vo){
//        //从session获取user
//        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
//        //判断参数
//        if (vo == null){
//            return CommonResult.error(500,"缺少参数");
//        }
//        if (vo.getRelationPlanId() == null){
//            return CommonResult.error(500,"缺少实习计划id");
//        }
//        if (vo.getRelationStudentId() == null){
//            return CommonResult.error(500,"缺少学生id");
//        }
//        try{
//            return internshipInfoByStudentService.dismissInternshipApplyDisplayList(vo,userDto);
//        }catch (RuntimeException e){
//            return CommonResult.error(500,"免实习岗位列表展示出错");
//        }
//    }

    /**
     * @Description: 学生撤回申请
     * @Author: GG
     * @Date: 2022/06/23 09:47
     */
    @ApiOperation("学生撤回申请")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @PostMapping("withdrawApply")
    public CommonResult<Object> withdrawApply(HttpServletRequest request, @RequestBody InternshipApplicationVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getInternshipId() == null){
            return CommonResult.error(500,"缺少实习申请id");
        }
        try{
            return internshipInfoByStudentService.withdrawApply(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"学生撤回申请失败");
        }

    }

    /**
     * @Description: 实习岗位修改与企业变更申请
     * @Author: GG
     * @Date: 2022/06/24 11:37
     */
    @ApiOperation("实习岗位修改与企业变更申请")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @PostMapping("jobModifyAndCompanyModify")
    public CommonResult<Object> jobModifyAndCompanyModify(HttpServletRequest request, @RequestBody InternshipApplicationVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getInternshipId() == null){
            return CommonResult.error(500,"缺少实习申请id");
        }
        try{
            return internshipInfoByStudentService.jobModifyAndCompanyModify(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"实习岗位修改与企业变更申请失败");
        }

    }

    /**
     * @Description: 实习申请、实习岗位修改与企业变更申请、免实习申请、就业上报审核列表（教师）
     * @Author: GG
     * @Date: 2022/06/24 16:37
     */
    @ApiOperation("实习申请、实习岗位修改与企业变更申请、免实习申请、就业上报审核列表（教师）")
    @RequiresRoles(value = {"teacher","student","instructor"}, logical = Logical.OR)
    @GetMapping("jobModifyAndCompanyModifyCheckList")
    public CommonResult<Object> jobModifyAndCompanyModifyCheckList(HttpServletRequest request, InternshipApplicationVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        if(vo.getIsCheck() == null){
            return CommonResult.error(500,"缺少是否已审核状态");
        }
        if(vo.getInternshipType() == null){
            return CommonResult.error(500,"缺少申请类型");
        }
//        try{
            return internshipInfoByStudentService.internshipInfoCheckedList(vo,userDto);
//        }catch (RuntimeException e){
//            return CommonResult.error(500,"实习审核列表展示失败");
//        }

    }

    /**
     * @Description: 展示就业上报内容
     * @Author: GG
     * @Date: 2022/06/29 10:12
     */
    @ApiOperation("展示就业上报内容")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @GetMapping("employmentReportedDisplay")
    public CommonResult<Object> employmentReportedDisplay(HttpServletRequest request, InternshipApplicationVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getInternshipId() == null){
            return CommonResult.error(500,"缺少实习申请id");
        }
        try{
            return internshipInfoByStudentService.employmentReportedDisplay(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"展示就业上报内容出错");
        }

    }

    /**
     * @Description: 就业上报列表审核列表（教师端）
     * @Author: GG
     * @Date: 2022/06/29 10:12
     */
    @ApiOperation("就业上报列表审核列表（教师端）")
    @RequiresRoles(value = {"student","teacher"}, logical = Logical.OR)
    @GetMapping("employmentReportedDisplayList")
    public CommonResult<Object> employmentReportedDisplayList(HttpServletRequest request, InternshipApplicationVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getRelationPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
//
            return internshipInfoByStudentService.employmentReportedDisplayList(vo,userDto);
//        }catch (RuntimeException e){
//            return CommonResult.error(500,"展示就业上报列表出错");        try{
//        }
    }

    /**
     * @Description: 提交就业上报
     * @Author: GG
     * @Date: 2022/06/22 16:07
     */
    @ApiOperation("提交就业上报")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @PostMapping("employmentReportedSubmitOrUpdate")
    public CommonResult<Object> employmentReportedSubmitOrUpdate(HttpServletRequest request, @RequestBody InternshipApplicationVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if(vo.getRelationPlanId()==null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        if(vo.getJobCategory()==null){
            return CommonResult.error(500,"缺少就业类别");
        }
        if(vo.getCompanyName()==null){
            return CommonResult.error(500,"实习单位名称不能为空");
        }
        if(vo.getCompanyNature()==null){
            return CommonResult.error(500,"公司性质不能为空");
        }
        if(vo.getSocialCode()==null){
            return CommonResult.error(500,"社会统一信用代码不能为空");
        }
        if(vo.getCompanyScale()==null){
            return CommonResult.error(500,"企业规模不能为空");
        }
        if(vo.getCompanyTelephone()==null){
            return CommonResult.error(500,"企业联系电话不能为空");
        }
        if(vo.getCompanyIndustry()==null){
            return CommonResult.error(500,"所属行业不能为空");
        }
        if(vo.getCompanyArea()==null){
            return CommonResult.error(500,"所属区域不能为空");
        }
        if(vo.getJobName()==null){
            return CommonResult.error(500,"岗位名称不能为空");
        }
        if(vo.getJobType()==null){
            return CommonResult.error(500,"岗位类别不能为空");
        }
        if(vo.getIsAboral()==null){
            return CommonResult.error(500,"专业对口与否不能为空");
        }
        try{
            return internshipInfoByStudentService.employmentReportedSubmitOrUpdate(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"提交就业上报失败");
        }

    }

    /**
     *@author: GG
     *@data: 2022/7/5 11:26
     *@function:实习岗位中未审核数量，已审核的通过数量、未通过数量
     */
    @ApiOperation("实习岗位数量显示")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @GetMapping("countInternshipInfoCheck")
    public CommonResult<Object> countInternshipInfoCheck(HttpServletRequest request,InternshipApplicationVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getRelationPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        if(vo.getIsCheck()==null){
            return CommonResult.error(500,"缺少是否审核字段");
        }else if(vo.getIsCheck().equals(1)){
            //审核通过需要传审核状态
            if(vo.getInternshipStatus() == null){
                return CommonResult.error(500,"缺少是否审核状态");
            }
        }
        try{
            return internshipInfoByStudentService.countInternshipInfoCheck(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"实习岗位数量显示异常");
        }

    }

}

