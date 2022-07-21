package com.above.controller;


import com.above.dto.UserDto;
import com.above.exception.OptionDateBaseException;
import com.above.service.SignApplyInfoService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.SignInfoVo;
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
 * 签到申请管理表 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Api(tags = {"补签申请接口"})
@RestController
@RequestMapping("/signApplyInfo")
public class SignApplyInfoController {

    @Autowired
    private SignApplyInfoService applyInfoService;


    /**
     * @Description: 学生补签申请
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    @ApiOperation("学生提交补签申请")
    @PostMapping("submitSignApply")
    @RequiresRoles(value = {"student"},logical = Logical.OR)
    public CommonResult<Object> submitSignApply(HttpServletRequest request, @RequestBody SignInfoVo vo) {

        //获取当前用户
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo == null) {
            return CommonResult.error(500, "缺少参数");
        }
        if (vo.getSignId() == null) {
            return CommonResult.error(500, "缺少签到id");
        }
        if (StringUtils.isBlank(vo.getReason())) {
            return CommonResult.error(500, "缺少补卡原因");
        }

        if (vo.getSignDateTime() == null) {
            return CommonResult.error(500, "缺少补卡时间");
        }
        /*用applyId判断是否为修改 实习指导*/
        if (vo.getApplyId() == null) {
            return applyInfoService.submitSignApply(userDto, vo);
        } else {
            return applyInfoService.modifySignApply(userDto, vo);
        }

    }


    /**
     * @Description: 学生补签申请
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    @ApiOperation("撤回补签申请")
    @PostMapping("withdrawSignApply")
    @RequiresRoles(value = {"student"},logical = Logical.OR)
    public CommonResult<Object> withdrawSignApply(HttpServletRequest request, @RequestBody SignInfoVo vo){

        //获取当前用户
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getApplyId() == null){
            return CommonResult.error(500,"缺少补卡申请id");
        }

        return applyInfoService.withdrawSignApply(userDto,vo);
    }

    /**
     * @Description: 审核补签申请
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    @ApiOperation("审核补签申请")
    @PostMapping("auditSignApply")
    @RequiresRoles(value = {"instructor"},logical = Logical.OR)
    public CommonResult<Object> auditSignApply(HttpServletRequest request, @RequestBody SignInfoVo vo){

        //获取当前用户
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getApplyId() == null){
            return CommonResult.error(500,"缺少补卡申请id");
        }
        if (vo.getAuditType() == null){
            return CommonResult.error(500,"缺少审核状态");
        }

        try {
            return applyInfoService.auditSignApply(userDto,vo);
        } catch (OptionDateBaseException e) {
            return CommonResult.error(500,e.getMessage());
        }
    }

    /**
     * @Description: 根据applyId获取审核详情
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    @ApiOperation("根据applyId获取审核详情")
    @GetMapping("getSignApplyInfo")
    public CommonResult<Object> getSignApplyInfo(HttpServletRequest request,  SignInfoVo vo){

        //获取当前用户
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getApplyId() == null){
            return CommonResult.error(500,"缺少补卡申请id");
        }

        return applyInfoService.getSignApplyInfo(userDto,vo);
    }

    /**
     * @Description: 获取补签申请列表
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    @ApiOperation("获取补签申请列表")
    @GetMapping("getSignApplyList")
    public CommonResult<Object> getSignApplyList(HttpServletRequest request,  SignInfoVo vo){

        //获取当前用户
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getPlanId() == null){
            return CommonResult.error(500,"实习计划id");
        }

        return applyInfoService.getSignApplyList(userDto,vo);
    }

}

