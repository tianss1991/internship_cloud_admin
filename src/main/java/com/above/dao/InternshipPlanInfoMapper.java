package com.above.dao;

import com.above.dto.*;
import com.above.po.InternshipPlanInfo;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.InternshipApplicationVo;
import com.above.vo.InternshipPlanInfoVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 实习计划表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface InternshipPlanInfoMapper extends BaseMapper<InternshipPlanInfo> {

    /**
    *@author: GG
    *@data: 2022/7/15 10:45
    *@function:获取实习计划分配情况列表
    */
    List<InternshipPlanInfoDto> getAllotInternshipPlanInfoList(InternshipPlanInfoVo vo);

    /**
    *@author: GG
    *@data: 2022/7/15 15:19
    *@function:获取实习计划列表
    */
    List<InternshipPlanInfoDto> getInternshipInfoList(InternshipPlanInfoVo vo);

    /**
     *@author: GG
     *@data: 2022/7/18 9:45
     *@function:获取实习计划分配情况列表总数
     */
    Integer getAllotInternshipPlanInfoListCount(InternshipPlanInfoVo vo);

    /**
     *@author: GG
     *@data: 2022/7/18 9:45
     *@function:获取实习计划列表总数
     */
    Integer getInternshipInfoListCount(InternshipPlanInfoVo vo);



    /**
    *@author: GG
    *@data: 2022/7/15 16:19
    *@function:通过实习计划id拿到内容
    */
    InternshipPlanInfoDto getInternshipInfoById(InternshipPlanInfoVo vo);

    /**
     *@author: GG
     *@data: 2022/7/18 10:32
     *@function:获取实习分配列表中已分配教师学生列表
     */
    List<AllotInternshipPlanInfoDto> getAllotInternshipPlanTeacherAndStudentInfoList(InternshipPlanInfoVo vo);

    /**
     *  获取简单的实习计划信息
     * @param vo
     * @return
     */
    SimplePlanInfoDto getPlanInfoByStudent(BaseVo vo);

}
