package com.above.controller;


import com.above.dto.UserDto;
import com.above.po.InternshipPlanInfo;
import com.above.service.InternshipPlanInfoService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.BaseVo;
import com.above.vo.InternshipApplicationVo;
import com.above.vo.InternshipPlanInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 实习计划表 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Api(tags = {"实习计划接口"})
@RestController
@RequestMapping("/internshipPlanInfo")
public class InternshipPlanInfoController {
    @Autowired
    private InternshipPlanInfoService internshipPlanInfoService;

    /**
     * @Description: 新增或修改实习计划
     * @Author: GG
     * @Date: 2022/7/12 15:25
     */
    @ApiOperation("新增或修改实习计划(PC)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @PostMapping("employmentReportedSubmitOrUpdate")
    public CommonResult<Object> employmentReportedSubmitOrUpdate(HttpServletRequest request, @RequestBody InternshipPlanInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //获取vo参数
        String planTitle = vo.getPlanTitle();
        Integer gradeId = vo.getGradeId();
        Integer majorId = vo.getMajorId();
        String gradation = vo.getGradation();
        Date startTime = vo.getStartTime();
        Date endTime = vo.getEndTime();
        String purpose = vo.getPurpose();
        String required = vo.getRequired();
        String content = vo.getContent();
        Integer signSet = vo.getSignSet();
        Integer dailyCount = vo.getDailyCount();
        Integer dailyWordCount = vo.getDailyWordCount();
        Integer weekCount = vo.getWeekCount();
        Integer weekWordCount = vo.getWeekWordCount();
        Integer monthCount = vo.getMonthCount();
        Integer monthWordCount = vo.getMonthWordCount();
        Integer summarizeCount = vo.getSummarizeCount();
        Integer summarizeWordCount = vo.getSummarizeWordCount();
        Integer signTimes = vo.getSignTimes();
        Integer isMustImage = vo.getIsMustImage();

        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if(userDto.getUserRoleDto().getRoleCode().equals("admin")){
            if (vo.getSchoolId() == null){
                return CommonResult.error(500,"缺少学校id");
            }
            if (vo.getDepartmentId() == null){
                return CommonResult.error(500,"缺少系部id");
            }
        }else if(userDto.getUserRoleDto().getRoleCode().equals("schoolAdmin")){
            if (vo.getDepartmentId() == null){
                return CommonResult.error(500,"缺少系部id");
            }
        }
        if (planTitle == null){
            return CommonResult.error(500,"缺少计划名称");
        }
        if (gradeId == null){
            return CommonResult.error(500,"缺少年级");
        }
        if (majorId == null){
            return CommonResult.error(500,"缺少专业");
        }
        if (gradation == null){
            return CommonResult.error(500,"缺少层次");
        }
        if (startTime == null){
            return CommonResult.error(500,"缺少开始时间");
        }
        if (endTime == null){
            return CommonResult.error(500,"缺少结束时间");
        }
        if (purpose == null){
            return CommonResult.error(500,"缺少实习目的");
        }
        if (required == null){
            return CommonResult.error(500,"缺少实习要求");
        }
        if (content == null){
            return CommonResult.error(500,"缺少实习内容");
        }
        if (signSet == null){
            return CommonResult.error(500,"缺少签到天数");
        }
        if (dailyCount == null){
            return CommonResult.error(500,"缺少日报篇数");
        }
        if (dailyWordCount == null){
            return CommonResult.error(500,"缺少日报字数");
        }
        if (weekCount == null){
            return CommonResult.error(500,"缺少周报篇数");
        }
        if (weekWordCount == null){
            return CommonResult.error(500,"缺少周报字数");
        }
        if (monthCount == null){
            return CommonResult.error(500,"缺少月报篇数");
        }
        if (monthWordCount == null){
            return CommonResult.error(500,"缺少月报字数");
        }
        if (summarizeCount == null){
            return CommonResult.error(500,"缺少总结篇数");
        }
        if (summarizeWordCount == null){
            return CommonResult.error(500,"缺少总结字数");
        }
        if (signTimes == null){
            return CommonResult.error(500,"缺少打卡次数");
        }
        if (isMustImage == null){
            return CommonResult.error(500,"缺少图片是否必传");
        }
        try{
            return internshipPlanInfoService.addandmodifyInternshipPlanInfo(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"新增或修改实习计划出错");
        }

    }


    /**
    *@author: GG
    *@data: 2022/7/13 15:25
    *@function:根据实习计划id拿到内容
    */
    @ApiOperation("根据实习计划id拿到内容(通用)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @GetMapping("getInternshipPlanInfoById")
    public CommonResult<Object> getInternshipPlanInfoById(HttpServletRequest request, InternshipPlanInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //获取vo参数
        if(vo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        if(vo.getTeacherId() == null){
            return CommonResult.error(500,"缺少教师id");
        }
//        try{
            return internshipPlanInfoService.getInternshipPlanInfoById(vo,userDto);
//        }catch (RuntimeException e){
//            return CommonResult.error(500,"根据实习计划id拿到内容出错");
//        }

    }

    /**
     * @Description: 审核实习计划
     * @Author: GG
     * @Date: 2022/7/13 15:25
     */
    @ApiOperation("审核实习计划(PC)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @PostMapping("checkInternshipPlanInfo")
    public CommonResult<Object> checkInternshipPlanInfo(HttpServletRequest request, @RequestBody InternshipPlanInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //获取vo参数
        if(vo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        try{
            return internshipPlanInfoService.checkInternshipPlanInfo(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"审核实习计划出错");
        }

    }

    /**
     * @Description: 撤回实习计划
     * @Author: GG
     * @Date: 2022/7/13 15:25
     */
    @ApiOperation("撤回实习计划(PC)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @PostMapping("withdrawInternshipPlanInfoList")
    public CommonResult<Object> withdrawInternshipPlanInfoList(HttpServletRequest request, @RequestBody InternshipPlanInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //获取vo参数
        if(vo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        try{
            return internshipPlanInfoService.withdrawInternshipPlanInfoList(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"撤回实习计划出错");
        }

    }

    /**
     * @Description: 删除实习计划
     * @Author: GG
     * @Date: 2022/7/13 15:25
     */
    @ApiOperation("删除实习计划(PC)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @PostMapping("deleteInternshipPlanInfo")
    public CommonResult<Object> deleteInternshipPlanInfo(HttpServletRequest request, @RequestBody InternshipPlanInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //获取vo参数
        if(vo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        try{
            return internshipPlanInfoService.deleteInternshipPlanInfo(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"删除实习计划出错");
        }

    }

    /**
     * @Description: 实习计划列表
     * @Author: GG
     * @Date: 2022/7/13 15:25
     */
    @ApiOperation("实习计划列表(PC)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @GetMapping("internshipPlanInfoList")
    public CommonResult<Object> internshipPlanInfoList(HttpServletRequest request, InternshipPlanInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
//        try{
            return internshipPlanInfoService.internshipPlanInfoList(vo,userDto);
//        }catch (RuntimeException e){
//            return CommonResult.error(500,"实习计划列表出错");
//        }
    }

    /**
    *@author: GG
    *@data: 2022/7/14 10:36
    *@function:分配实习计划
    */
    @ApiOperation("分配实习计划(PC)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @PostMapping("allotInternshipPlanInfo")
    public CommonResult<Object> allotInternshipPlanInfo(HttpServletRequest request, @RequestBody InternshipPlanInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //获取vo参数
        if(vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if(vo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        if(vo.getTeacherId() == null){
            return CommonResult.error(500,"缺少分配教师id");
        }
        if(vo.getStudentList() == null){
            return CommonResult.error(500,"缺少分配学生id集合");
        }
        try{
            return internshipPlanInfoService.allotInternshipPlanInfo(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"分配实习计划出错");
        }
    }

    /**
    *@author: GG
    *@data: 2022/7/14 13:52
    *@function:删除实习计划分配
    */
    @ApiOperation("删除实习计划分配(PC)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @PostMapping("deleteAllotInternshipPlanInfo")
    public CommonResult<Object> deleteAllotInternshipPlanInfo(HttpServletRequest request, @RequestBody InternshipPlanInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //获取vo参数
        if(vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if(vo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        if(vo.getTeacherId() == null){
            return CommonResult.error(500,"缺少分配教师id");
        }
        if(vo.getStudentList() == null){
            return CommonResult.error(500,"缺少分配学生id集合");
        }
        try{
            return internshipPlanInfoService.deleteAllotInternshipPlanInfo(vo,userDto);
        }catch (RuntimeException e){
            return CommonResult.error(500,"删除实习计划分配出错");
        }
    }

    /**
    *@author: GG
    *@data: 2022/7/14 15:13
    *@function:编辑实习计划分配
    */
    @ApiOperation("编辑实习计划分配(PC)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @PostMapping("modifyAllotInternshipPlanInfo")
    public CommonResult<Object> modifyAllotInternshipPlanInfo(HttpServletRequest request, @RequestBody InternshipPlanInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //获取vo参数
        if(vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if(vo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        if(vo.getTeacherId() == null){
            return CommonResult.error(500,"缺少分配教师id");
        }
        if(vo.getStudentList() == null){
            return CommonResult.error(500,"缺少分配学生id集合");
        }
        if(vo.getOldTeacherId() == null){
            return CommonResult.error(500,"缺少旧的分配教师id");
        }
        if(vo.getOldStudentList() == null){
            return CommonResult.error(500,"缺少旧的分配学生id集合");
        }
//        try{
            return internshipPlanInfoService.modifyAllotInternshipPlanInfo(vo,userDto);
//        }catch (RuntimeException e){
//            return CommonResult.error(500,"删除实习计划分配出错");
//        }
    }

    /**
     *@author: GG
     *@data: 2022/7/15 17:13
     *@function:获取实习计划分配列表
     */
    @ApiOperation("获取实习计划分配列表(PC)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @GetMapping("getAllotInternshipPlanInfoList")
    public CommonResult<Object> getAllotInternshipPlanInfoList(HttpServletRequest request,InternshipPlanInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //获取vo参数
        if(vo == null){
            return CommonResult.error(500,"缺少参数");
        }
//        try{
        return internshipPlanInfoService.getAllotInternshipPlanInfoList(vo,userDto);
//        }catch (RuntimeException e){
//            return CommonResult.error(500,"获取实习计划分配列表出错");
//        }
    }

    /**
     *@author: GG
     *@data: 2022/7/15 17:13
     *@function:获取实习分配列表中已分配教师学生列表
     */
    @ApiOperation("获取实习分配列表中已分配教师学生列表(PC)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @GetMapping("getAllotInternshipPlanTeacherAndStudentInfoList")
    public CommonResult<Object> getAllotInternshipPlanTeacherAndStudentInfoList(HttpServletRequest request,InternshipPlanInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //获取vo参数
        if(vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if(vo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划参数");
        }
//        try{
        return internshipPlanInfoService.getAllotInternshipPlanTeacherAndStudentInfoList(vo,userDto);
//        }catch (RuntimeException e){
//            return CommonResult.error(500,"获取实习分配列表中已分配教师学生列表出错");
//        }
    }


    /**
    *@author: GG
    *@data: 2022/7/20 10:08
    *@function:根据教师id拿到实习计划列表
    */
    @ApiOperation("根据教师id拿到实习计划列表(APP)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @GetMapping("getPlanInfoByTeacher")
    public CommonResult<Object> getPlanInfoByTeacher(HttpServletRequest request, BaseVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //获取vo参数
        if(vo == null){
            return CommonResult.error(500,"缺少参数");
        }
//        try{
        return internshipPlanInfoService.getPlanInfoByTeacher(vo,userDto);
//        }catch (RuntimeException e){
//            return CommonResult.error(500,"根据教师id拿到实习计划列表出错");
//        }
    }

    /**
     *@author: GG
     *@data: 2022/7/20 10:08
     *@function:根据学生id拿到个人资料()
     */
    @ApiOperation("根据学生id拿到个人资料(APP)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @GetMapping("getStudentInfoByStudent")
    public CommonResult<Object> getStudentInfoByStudent(HttpServletRequest request, BaseVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //获取vo参数
        if(vo == null){
            return CommonResult.error(500,"缺少参数");
        }
//        try{
        return internshipPlanInfoService.getStudentInfoByStudent(vo,userDto);
//        }catch (RuntimeException e){
//            return CommonResult.error(500,"根据教师id拿到实习计划列表出错");
//        }
    }

    /**
     *@author: GG
     *@data: 2022/7/20 10:08
     *@function:根据学生id拿到实习计划列表
     */
    @ApiOperation("根据学生id拿到实习计划列表(APP)")
    @RequiresRoles(value = {"student","adviser","admin","schoolAdmin","departmentAdmin"}, logical = Logical.OR)
    @GetMapping("getPlanInfoByStudentId")
    public CommonResult<Object> getPlanInfoByStudentId(HttpServletRequest request, BaseVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //获取vo参数
        if(vo == null){
            return CommonResult.error(500,"缺少参数");
        }
//        try{
        return internshipPlanInfoService.getPlanInfoByStudentId(vo,userDto);
//        }catch (RuntimeException e){
//            return CommonResult.error(500,"根据教师id拿到实习计划列表出错");
//        }
    }

}

