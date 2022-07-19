package com.above.service;

import com.above.dto.UserDto;
import com.above.po.InternshipPlanInfo;
import com.above.utils.CommonResult;
import com.above.vo.InternshipPlanInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 实习计划表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface InternshipPlanInfoService extends IService<InternshipPlanInfo> {

    /**
    *@author: GG
    *@data: 2022/7/12 8:53
    *@function:新增实习计划
    */
    CommonResult<Object> addandmodifyInternshipPlanInfo(InternshipPlanInfoVo vo, UserDto userDto);

    /**
    *@author: GG
    *@data: 2022/7/12 8:54
    *@function:根据实习计划id拿到内容
    */
    CommonResult<Object> getInternshipPlanInfoById(InternshipPlanInfoVo vo, UserDto userDto);

    /**
    *@author: GG
    *@data: 2022/7/12 8:54
    *@function:审核实习计划
    */
    CommonResult<Object> checkInternshipPlanInfo(InternshipPlanInfoVo vo, UserDto userDto);

    /**
    *@author: GG
    *@data: 2022/7/12 8:58
    *@function:实习计划列表
    */
    CommonResult<Object> internshipPlanInfoList(InternshipPlanInfoVo vo, UserDto userDto);


    /**
    *@author: GG
    *@data: 2022/7/13 14:16
    *@function:撤回实习计划
    */
    CommonResult<Object> withdrawInternshipPlanInfoList(InternshipPlanInfoVo vo, UserDto userDto);

    /**
    *@author: GG
    *@data: 2022/7/13 14:16
    *@function:删除实习计划
    */
    CommonResult<Object> deleteInternshipPlanInfo(InternshipPlanInfoVo vo, UserDto userDto);

    /**
    *@author: GG
    *@data: 2022/7/14 10:14
    *@function:分配实习计划
    */
    CommonResult<Object> allotInternshipPlanInfo(InternshipPlanInfoVo vo, UserDto userDto);

    /**
    *@author: GG
    *@data: 2022/7/14 11:56
    *@function:删除实习计划分配
    */
    CommonResult<Object> deleteAllotInternshipPlanInfo(InternshipPlanInfoVo vo, UserDto userDto);

    /**
    *@author: GG
    *@data: 2022/7/14 14:19
    *@function:编辑实习计划分配
    */
    CommonResult<Object> modifyAllotInternshipPlanInfo(InternshipPlanInfoVo vo, UserDto userDto);

    /**
    *@author: GG
    *@data: 2022/7/15 10:44
    *@function:获取实习计划分配情况列表
    */
    CommonResult<Object> getAllotInternshipPlanInfoList(InternshipPlanInfoVo vo, UserDto userDto);

    /**
    *@author: GG
    *@data: 2022/7/18 10:32
    *@function:获取实习分配列表中已分配教师学生列表
    */
    CommonResult<Object> getAllotInternshipPlanTeacherAndStudentInfoList(InternshipPlanInfoVo vo, UserDto userDto);
}
