package com.above.controller;


import com.above.dto.UserDto;
import com.above.service.LeaveApplyInfoService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.GradeVo;
import com.above.vo.LeaveApplyInfoVo;
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
 * 申请请假表 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Api(tags = {"请假申请接口"})
@RestController
@RequestMapping("/leaveApplyInfo")
public class LeaveApplyInfoController {
    @Autowired
    private LeaveApplyInfoService leaveApplyInfoService;

    /**
     * @Description: 提交请假申请(学生)
     * @Author: GG
     * @Date: 2022/06/30 10:30
     */
    @ApiOperation("提交请假申请")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @PostMapping("submitLeaveApplyInfo")
    public CommonResult<Object> submitLeaveApplyInfo(HttpServletRequest request, @RequestBody LeaveApplyInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getStartTime() == null){
            return CommonResult.error(500,"缺少开始时间");
        }
        if (vo.getEndTime() == null){
            return CommonResult.error(500,"缺少结束时间");
        }
        if (vo.getDuration() == null){
            return CommonResult.error(500,"缺少时长");
        }
        if (vo.getType() == null){
            return CommonResult.error(500,"缺少请假类型");
        }
        if (vo.getReason() == null){
            return CommonResult.error(500,"缺少请假事由");
        }
        try{
            return leaveApplyInfoService.submitLeaveApplyInfo(vo,userDto);
        }catch (Exception e){
            return CommonResult.error(500,"提交请假申请失败");
        }
    }

    /**
     * @Description: 编辑请假申请(学生)
     * @Author: GG
     * @Date: 2022/06/30 10:40
     */
    @ApiOperation("编辑请假申请")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @PostMapping("modifyLeaveApplyInfo")
    public CommonResult<Object> modifyLeaveApplyInfo(HttpServletRequest request, @RequestBody LeaveApplyInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getLeaveApplyId() == null){
            return CommonResult.error(500,"缺少请假单id");
        }
        try{
            return leaveApplyInfoService.modifyLeaveApplyInfo(vo,userDto);
        }catch (Exception e){
            return CommonResult.error(500,"编辑请假申请失败");
        }

    }

    /**
     * @Description: 撤回请假申请(学生)
     * @Author: GG
     * @Date: 2022/06/30 10:45
     */
    @ApiOperation("撤回请假申请")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @PostMapping("withdrawLeaveApplyInfo")
    public CommonResult<Object> withdrawLeaveApplyInfo(HttpServletRequest request, @RequestBody LeaveApplyInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getLeaveApplyId() == null){
            return CommonResult.error(500,"缺少请假单id");
        }
        try{
            return leaveApplyInfoService.withdrawLeaveApplyInfo(vo,userDto);
        }catch (Exception e){
            return CommonResult.error(500,"撤回请假申请失败");
        }
    }

    /**
     * @Description: 辅导员审核请假申请(辅导员)
     * @Author: GG
     * @Date: 2022/06/30 10:50
     */
    @ApiOperation("辅导员审核请假申请")
    @RequiresRoles(value = {"instructor","teacher","student"}, logical = Logical.OR)
    @PostMapping("checkLeaveApplyInfo")
    public CommonResult<Object> checkLeaveApplyInfo(HttpServletRequest request, @RequestBody LeaveApplyInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getLeaveApplyId() == null){
            return CommonResult.error(500,"缺少请假单id");
        }
        if (vo.getCheckStatus() == null){
            return CommonResult.error(500,"缺少审核状态");
        }
        if(vo.getCheckStatus() == 0){
            if(vo.getFailReason() == null){
                return CommonResult.error(500,"缺少驳回理由");
            }
        }
        try{
            return leaveApplyInfoService.checkLeaveApplyInfo(vo,userDto);
        }catch (Exception e){
            return CommonResult.error(500,"辅导员审核请假申请失败");
        }
    }

    /**
     * @Description: 通过学生用户id拿到请假单列表(学生)
     * @Author: GG
     * @Date: 2022/06/30 10:55
     */
    @ApiOperation("通过学生用户id拿到请假单列表")
    @RequiresRoles(value = {"student","instructor"}, logical = Logical.OR)
    @GetMapping("getLeaveApplyByUserId")
    public CommonResult<Object> getLeaveApplyByUserId(HttpServletRequest request,LeaveApplyInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
//        try{
            return leaveApplyInfoService.getLeaveApplyByUserId(vo,userDto);
//        }catch (Exception e){
//            return CommonResult.error(500,"通过学生用户id拿到请假单列表失败");
//        }
    }

    /**
    *@author: GG
    *@data: 2022/7/5 15:58
    *@function:通过请假单id拿到请假单内容
    */
    @ApiOperation("通过请假单id拿到请假单信息")
    @RequiresRoles(value = {"student","instructor"}, logical = Logical.OR)
    @GetMapping("getLeaveApplyByApplyId")
    public CommonResult<Object> getLeaveApplyByApplyId(HttpServletRequest request,LeaveApplyInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
//        try{
            return leaveApplyInfoService.getLeaveApplyByApplyId(vo,userDto);
//        }catch (Exception e){
//            return CommonResult.error(500,"通过请假单id拿到请假单信息失败");
//        }
    }

    /**
     *@author: GG
     *@data: 2022/7/7 10:28
     *@function:判断学生是否已经有审核中请假单
     */
    @ApiOperation("判断学生是否已经有审核中请假单")
    @RequiresRoles(value = {"student","instructor"}, logical = Logical.OR)
    @GetMapping("judgeLeaveApply")
    public CommonResult<Object> judgeLeaveApply(HttpServletRequest request,LeaveApplyInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        try{
            return leaveApplyInfoService.judgeLeaveApply(vo,userDto);
        }catch (Exception e){
            return CommonResult.error(500,"判断出错");
        }
    }

    /**
    *@author: GG
    *@data: 2022/7/6 11:27
    *@function:辅导员端请假申请列表
    */
    @ApiOperation("辅导员端请假申请列表")
    @RequiresRoles(value = {"student","instructor"}, logical = Logical.OR)
    @GetMapping("leaveApplyListByInstruct")
    public CommonResult<Object> leaveApplyListByInstruct(HttpServletRequest request,LeaveApplyInfoVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getPage() == null){
            return CommonResult.error(500,"缺少分页参数");
        }
        if (vo.getSize() == null){
            return CommonResult.error(500,"缺少分页参数");
        }
        if (vo.getIsCheck() == null){
            return CommonResult.error(500,"缺少是否审核参数");
        }
//        if (vo.getIsCheck() == 1){
//            if (vo.getCheckStatus() == null){
//                return CommonResult.error(500,"缺少审核状态参数");
//            }
//        }
//        try{
            return leaveApplyInfoService.leaveApplyListByInstruct(vo,userDto);
//        }catch (Exception e){
//            return CommonResult.error(500,"辅导员端请假申请列表显示失败");
//        }
    }
}

