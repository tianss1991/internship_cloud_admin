package com.above.controller;


import com.above.dto.UserDto;
import com.above.service.ReportWithStudentService;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.vo.ReportWithStudentVo;
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
 * 学生报告表 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Api(tags = {"日报/周报/月报/实习总结接口"})
@RestController
@RequestMapping("/reportWithStudent")
public class ReportWithStudentController {

    @Autowired
    private ReportWithStudentService reportWithStudentService;

    /**
     * @Description: 查询学生日报/周报/月报/实习总结(学生端/教师端)
     * @Author: YJH
     * @Date: 2022/7/8 14:00
     */
    @ApiOperation("查询学生日报/周报/月报/实习总结(学生端/教师端)")
    @RequiresRoles(value = {"student","adviser"}, logical = Logical.OR)
    @GetMapping("getDailyPaperList")
    public CommonResult<Object> getDailyPaperList(HttpServletRequest request,ReportWithStudentVo vo) {
        //从session获取user
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo==null){
            return CommonResult.error(500,"缺少参数");
        }

        try {
            return reportWithStudentService.getDailyPaperList(userDto, vo);
        } catch (RuntimeException e) {
            return CommonResult.error(500, e.getMessage());
        }

    }


    /**
     * @Description: 新增日报/周报/月报/实习总结
     * @Author: YJH
     * @Date: 2022/7/8 10:30
     */
    @ApiOperation("新增日报/周报/月报/实习总结")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @PostMapping("addDailyPaper")
    public CommonResult<Object> addDailyPaper(HttpServletRequest request, @RequestBody ReportWithStudentVo vo) {
        //从session获取user
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

       if (vo==null){
           return CommonResult.error(500,"缺少参数");
       }

        try {
            return reportWithStudentService.addDailyPaper(userDto, vo);
        } catch (RuntimeException e) {
            return CommonResult.error(500, e.getMessage());
        }

    }

    /**
     * @Description: 撤回
     * @Author: YJH
     * @Date: 2022/7/8 10:30
     */
    @ApiOperation("撤回")
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @PostMapping("revocationDailyPaper")
    public CommonResult<Object> revocationDailyPaper(HttpServletRequest request, @RequestBody ReportWithStudentVo vo) {
        //从session获取user
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo==null){
            return CommonResult.error(500,"缺少参数");
        }

        try {
            return reportWithStudentService.revocationDailyPaper(userDto, vo);
        } catch (RuntimeException e) {
            return CommonResult.error(500, e.getMessage());
        }

    }




    /**
     * @Description: //修改/编辑(学生端)  驳回/批阅日报（教师端）
     * @Author: YJH
     * @Date: 2022/7/11 08:53
     */
    @ApiOperation("//修改/编辑(学生端)  驳回/批阅日报（教师端）")
    @RequiresRoles(value = {"student","adviser"}, logical = Logical.OR)
    @PostMapping("updateDailyPaper")
    public CommonResult<Object> updateDailyPaper(HttpServletRequest request, @RequestBody ReportWithStudentVo vo) {
        //从session获取user
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo==null){
            return CommonResult.error(500,"缺少参数");
        }

        try {
            return reportWithStudentService.updateDailyPaper(userDto, vo);
        } catch (RuntimeException e) {
            return CommonResult.error(500, e.getMessage());
        }

    }

    /**
     * @Description: 查看日报/周报/月报/实习总结详情（学生端/教师端）
     * @Author: YJH
     * @Date: 2022/7/8 14:00
     */
    @ApiOperation("查看日报/周报/月报/实习总结详情(学生端/教师端)")
    @RequiresRoles(value = {"student","adviser"}, logical = Logical.OR)
    @GetMapping("getDailyPaperStudent")
    public CommonResult<Object> getDailyPaperStudent(HttpServletRequest request,ReportWithStudentVo vo) {
        //从session获取user
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo==null){
            return CommonResult.error(500,"缺少参数");
        }

        try {
            return reportWithStudentService.getDailyPaperStudent(userDto, vo);
        } catch (RuntimeException e) {
            return CommonResult.error(500, e.getMessage());
        }

    }


    /**
     * @Description: 统计日报-未写/已写(教师端)
     * @Author: YJH
     * @Date: 2022/7/12 14:00
     */
    @ApiOperation("统计日报-未写/已写(教师端)")
    @RequiresRoles(value = {"adviser"}, logical = Logical.OR)
    @PostMapping("getDailyPaperStatisticsList")
    public CommonResult<Object> getDailyPaperStatisticsList(HttpServletRequest request,@RequestBody ReportWithStudentVo vo) {
        //从session获取user
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo==null){
            return CommonResult.error(500,"缺少参数");
        }

        try {
            return reportWithStudentService.getDailyPaperStatisticsList(userDto, vo);
        } catch (RuntimeException e) {
            return CommonResult.error(500, e.getMessage());
        }
    }

    /**
     * @Description: 判断是否已阅(教师端)
     * @Author: YJH
     * @Date: 2022/7/12 14:00
     */
    @ApiOperation("判断是否已阅(教师端)")
    @RequiresRoles(value = {"adviser"}, logical = Logical.OR)
    @PostMapping("getHaveRead")
    public CommonResult<Object> getHaveRead(HttpServletRequest request,@RequestBody ReportWithStudentVo vo) {
        //从session获取user
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));

        if (vo==null){
            return CommonResult.error(500,"缺少参数");
        }

        try {
            return reportWithStudentService.getHaveRead(userDto, vo);
        } catch (RuntimeException e) {
            return CommonResult.error(500, e.getMessage());
        }
    }




}

