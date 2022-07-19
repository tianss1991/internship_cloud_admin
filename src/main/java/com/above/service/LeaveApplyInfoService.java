package com.above.service;

import com.above.dto.UserDto;
import com.above.exception.OptionDateBaseException;
import com.above.po.LeaveApplyInfo;
import com.above.utils.CommonResult;
import com.above.vo.LeaveApplyInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.text.ParseException;

/**
 * <p>
 * 申请请假表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface LeaveApplyInfoService extends IService<LeaveApplyInfo> {

    /**
     * 提交请假申请(学生)
     * */
    CommonResult<Object> submitLeaveApplyInfo(LeaveApplyInfoVo vo, UserDto userDto) throws ParseException;

    /**
     * 编辑请假申请(学生)
     * */
    CommonResult<Object> modifyLeaveApplyInfo(LeaveApplyInfoVo vo, UserDto userDto) throws ParseException;

    /**
     * 撤回请假申请(学生)
     * */
    CommonResult<Object> withdrawLeaveApplyInfo(LeaveApplyInfoVo vo, UserDto userDto);

    /**
     * 辅导员审核请假申请(辅导员)
     * */
    CommonResult<Object> checkLeaveApplyInfo(LeaveApplyInfoVo vo, UserDto userDto) throws OptionDateBaseException;

    /**
     * 通过学生用户id拿到请假单列表(学生)
     * */
    CommonResult<Object> getLeaveApplyByUserId(LeaveApplyInfoVo vo, UserDto userDto);

    /**
    *@author: GG
    *@data: 2022/7/5 15:54
    *@function:通过请假单id拿到请假单内容
    */
    CommonResult<Object> getLeaveApplyByApplyId(LeaveApplyInfoVo vo, UserDto userDto);

    /**
     *@author: GG
     *@data: 2022/7/5 15:58
     *@function:通过请假单id拿到请假单内容
     */
    CommonResult<Object> judgeLeaveApply(LeaveApplyInfoVo vo, UserDto userDto);


    /**
     * 辅导员端请假申请列表(辅导员)
     * */
    CommonResult<Object> leaveApplyListByInstruct(LeaveApplyInfoVo vo, UserDto userDto);

    /**
     * 辅导员端请假申请列表中未审核数量(辅导员)
     * */
    CommonResult<Object> unCheckleaveApplyCount(LeaveApplyInfoVo vo, UserDto userDto);



}

