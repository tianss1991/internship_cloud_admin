package com.above.dao;

import com.above.dto.LeaveApplyInfoDto;
import com.above.po.LeaveApplyInfo;
import com.above.vo.LeaveApplyInfoVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 申请请假表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface LeaveApplyInfoMapper extends BaseMapper<LeaveApplyInfo> {
    /**
     * @Description: 辅导员端请假申请列表
     * @Author: GG
     * @Date: 2022/06/30 11:28
     */
    List<LeaveApplyInfoDto> leaveApplyCheckList(LeaveApplyInfoVo vo);

    /**
     *@author: GG
     *@data: 2022/7/6 10:15
     *@function:辅导员端请假申请列表数量
     */
    Integer countLeaveApplyCheckList(LeaveApplyInfoVo vo);

    //关联日志表，后续开发如果要请假流程可以直接使用
//    /**
//    *@author: GG
//    *@data: 2022/7/6 9:59
//    *@function:辅导员端请假申请列表中已审核
//    */
//    List<LeaveApplyInfoDto> leaveApplyListChecked(LeaveApplyInfoVo vo);
//
//    /**
//    *@author: GG
//    *@data: 2022/7/6 10:29
//    *@function:辅导员端请假申请列表中已审核数量
//    */
//    Integer countLeaveApplyListChecked(LeaveApplyInfoVo vo);
//
//    /**
//     * @Description: 辅导员端请假申请列表中未审核数量
//     * @Author: GG
//     * @Date: 2022/06/30 11:35
//     */
//    Integer unCheckleaveApplyCount(LeaveApplyInfoVo vo);

    /**
     *@author: GG
     *@data: 2022/7/5 15:58
     *@function:通过请假单id拿到请假单内容
     */
    LeaveApplyInfoDto getLeaveApplyByIdUnCheck(LeaveApplyInfoVo vo);

//    /**
//     *@author: GG
//     *@data: 2022/7/5 15:58
//     *@function:通过请假单id拿到请假单内容(已审核)；后续需要流程才会用到
//     */
//    LeaveApplyInfoDto getLeaveApplyByIdChecked(LeaveApplyInfoVo vo);

    /**
    *@author: GG
    *@data: 2022/7/8 15:20
    *@function:通过学生id拿到请假单列表
    */
    List<LeaveApplyInfoDto> getLeaveApplyListByUserId(LeaveApplyInfoVo vo);

    /**
     *@author: GG
     *@data: 2022/7/8 15:20
     *@function:通过学生id拿到请假单列表数量
     */
   Integer getLeaveApplyListByUserIdCount(LeaveApplyInfoVo vo);
}
