package com.above.controller;


import cn.hutool.core.util.NumberUtil;
import com.above.dto.UserDto;
import com.above.service.GradeTemplateService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.BaseVo;
import com.above.vo.GradeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * <p>
 * 成绩模板表 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-07-14
 */
@Api(tags = {"成绩模板表"})
@RestController
@RequestMapping("/gradeTemplate")
public class GradeTemplateController {

    @Autowired
    private GradeTemplateService gradeTemplateService;


    /**
     * @Description: 根据实习计划id查询模板
     * @Author: LZH
     * @Date: 2022/7/14 9:10
     */
    @ApiOperation("根据实习计划id查询模板")
    @RequiresRoles(value = {"student","admin","schoolAdmin","departmentAdmin","instructor","teacher"}, logical = Logical.OR)
    @GetMapping("getGradeTemplateByPlanId")
    public CommonResult<Object> getGradeTemplateByPlanId(HttpServletRequest request, Optional<BaseVo> vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (!vo.isPresent()){
            return CommonResult.error(500,"缺少参数");
        }

        if (vo.map(BaseVo::getPlanId).orElse(1).equals(1)){
            return CommonResult.error(500,"缺少实习id");
        }

        return gradeTemplateService.getGradeTemplateByPlanId(vo.get(),userDto);

    }

}

