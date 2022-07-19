package com.above.controller;


import com.above.dto.UserDto;
import com.above.po.AuthRole;
import com.above.service.MajorInfoService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.ClassVo;
import com.above.vo.MajorInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 专业 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Api(tags = {"专业接口"})
@RestController
@RequestMapping("/majorInfo")
public class MajorInfoController {

    @Autowired
    private MajorInfoService majorInfoService;

    /**
     * @Description: 添加专业
     * @Author: YJH
     * @Date: 2022/6/22 15:18
     */
    @ApiOperation("添加专业")
    @RequiresRoles(value = {"admin","schoolAdmin" , "departmentAdmin"}, logical = Logical.OR)
    @PostMapping("addMajorInfo")
    public CommonResult<Object> addMajorInfo(HttpServletRequest request, @RequestBody MajorInfoVo majorInfoVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        //判断参数
        if (majorInfoVo == null){
            return CommonResult.error(500,"缺少参数");
        }

        if (userDto.getUserRoleDto().getRoleCode().equals(AuthRole.DEPARTMENT_ADMIN)){
            List<Integer> departmentIds = userDto.getDepartmentIds();
            if (departmentIds == null || departmentIds.size() <= 0){
                return CommonResult.error(500,"未找到关联学校二级学院");
            }
            majorInfoVo.setSchoolId(majorInfoService.getById(departmentIds.get(0)).getSchoolId());
        }
        if (majorInfoVo.getSchoolId() == null){
            return CommonResult.error(500,"缺少学校id");
        }
        if (majorInfoVo.getDepartmentId() == null){
            return CommonResult.error(500,"缺少二级学院id");
        }
        if (majorInfoVo.getMajorId() == null){
            return CommonResult.error(500,"缺少专业id");
        }
        try {
            return majorInfoService.addMajor(userDto,majorInfoVo);
        }catch (RuntimeException e){
            return CommonResult.error(500, e.getMessage());
        }

    }

    /**
     * @Description: 修改专业
     * @Author: YJH
     * @Date: 2022/6/23 09:48
     */
    @ApiOperation("修改专业")
    @RequiresRoles(value = {"admin", "departmentAdmin","schoolAdmin"}, logical = Logical.OR)
    @PostMapping("modifyMajorInfo")
    public CommonResult<Object> modifyMajorInfo(HttpServletRequest request,@RequestBody MajorInfoVo majorInfoVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (majorInfoVo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (majorInfoVo.getDepartmentId() == null){
            return CommonResult.error(500,"缺少二级学院id");
        }

        return majorInfoService.modifyMajInfo(userDto,majorInfoVo);
    }

    /**
     * @Description: 删除专业
     * @Author: YJH
     * @Date: 2022/6/23 10:30
     */
    @ApiOperation("删除专业")
    @RequiresRoles(value = {"admin", "departmentAdmin","schoolAdmin"}, logical = Logical.OR)
    @PostMapping("deleteMajorInfo")
    public CommonResult<Object> deleteMajorInfo(HttpServletRequest request,@RequestBody MajorInfoVo majorInfoVo){

        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (majorInfoVo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (majorInfoVo.getMajorIdList() == null){
            return CommonResult.error(500,"缺少二级学院id");
        }
        try {
            return majorInfoService.deleteMajor(userDto,majorInfoVo);
        }catch (RuntimeException e){
            return CommonResult.error(500, "删除异常");
        }
    }

    /**
     * @Description: 获取专业列表
     * @Author: YJH
     * @Date: 2022/6/23 11:28
     */
    @ApiOperation("获取专业列表")
    @RequiresRoles(value = {"admin","schoolAdmin", "departmentAdmin", "instructor","adviser","teacher", "student","visitor"}, logical = Logical.OR)
    @GetMapping("getMajorList")
    public CommonResult<Object> getMajorList(HttpServletRequest request, MajorInfoVo majorInfoVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (majorInfoVo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if(userDto.getUserType().equals(0)){
            if(majorInfoVo.getSchoolId()==null){
                return CommonResult.error(500,"缺少学校id");
            }
            if(majorInfoVo.getDepartmentId()==null){
                return CommonResult.error(500,"缺少二级学院id");
            }
        }

        /*若page==0为不分页*/
        if (majorInfoVo.getPage()==0){
            //不分页
            return majorInfoService.getmajorWithoutPage(userDto,majorInfoVo);
        }else {
            //分页
            return majorInfoService.getmajorPageList(userDto,majorInfoVo);
        }
    }

}

