package com.above.controller;


import com.above.dto.UserDto;
import com.above.service.SchoolInfoService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.BaseVo;
import com.above.vo.SchoolVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 学校信息 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Api(tags = {"学校管理接口"})
@RestController
@RequestMapping("/schoolInfo")
public class SchoolInfoController {

    private final SchoolInfoService schoolInfoService;

    public SchoolInfoController(SchoolInfoService schoolInfoService) {
        this.schoolInfoService = schoolInfoService;
    }


    /**
     * @Description: 添加学校以及领导
     * @Author: LZH
     * @Date: 2022/1/10 16:26
     */
    @ApiOperation("添加学校以及领导")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping("addSchoolInfo")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> addSchoolInfo(HttpServletRequest request, @RequestBody SchoolVo schoolVo){

        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (schoolVo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (StringUtils.isBlank(schoolVo.getSchoolName())){
            return CommonResult.error(500,"缺少学校名称");
        }
        try{
            return schoolInfoService.addSchoolInfo(userDto,schoolVo);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException(e.getMessage());
        }

    }


    /**
     * @Description: 修改学校信息
     * @Author: LZH
     * @Date: 2022/1/10 16:26
     */
    @ApiOperation("修改学校信息")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping("modifySchoolInfo")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> modifySchool(HttpServletRequest request, @RequestBody SchoolVo schoolVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (schoolVo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (schoolVo.getSchoolId() == null){
            return CommonResult.error(500,"缺少学校id");
        }
        if (StringUtils.isBlank(schoolVo.getSchoolName())){
            return CommonResult.error(500,"请输入要修改的学校名称");
        }
        try {
            return schoolInfoService.modifySchoolInfo(userDto,schoolVo);
        }catch (RuntimeException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * @Description: 删除学校接口
     * @Author: LZH
     * @Date: 2022/1/10 16:43
     */
    @ApiOperation("删除学校接口")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping("deleteSchoolInfo")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> deleteSchoolBatchById(HttpServletRequest request, @RequestBody SchoolVo schoolVo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断参数
        if (schoolVo == null){
            return CommonResult.error(500,"缺少参数");
        }

        try {
            return schoolInfoService.deleteSchoolInfo(userDto,schoolVo);
        }catch (RuntimeException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @Description: 获取学校列表
     * @Author: LZH
     * @Date: 2022/1/10 19:14
     */
    @ApiOperation("获取学校列表")
    @RequiresRoles(value = {"admin","schoolAdmin", "departmentAdmin", "teacher", "instructorSchool","instructorDepartment"}, logical = Logical.OR)
    @GetMapping("getSchoolList")
    public CommonResult<Object> getSchoolList(HttpServletRequest request,BaseVo vo){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }

        /*若page==0为不分页*/
        if (vo.getPage()==0){
            //不分页
            return schoolInfoService.getSchoolWithoutPage(userDto,vo);
        }else {
            //分页
            return schoolInfoService.getSchoolPageList(userDto,vo);
        }

    }

    /**
     * @Description: 院校信息导入
     * @Author: LZH
     * @Date: 2022/1/17 10:02
     */
    @ApiOperation("院校信息导入")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping("import")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> importSchoolInfo(HttpServletRequest request, @RequestParam("file")MultipartFile file){
        //从session获取user
        UserDto userDto =(UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (file == null){
            return CommonResult.error(500,"缺少文件");
        }
        if (file.isEmpty()){
            return CommonResult.error(500,"缺少文件");
        }
        try {
            return schoolInfoService.importSchoolInfo(userDto, file);
        } catch (RuntimeException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return CommonResult.error(500, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}

