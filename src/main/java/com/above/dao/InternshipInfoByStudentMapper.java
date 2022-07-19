package com.above.dao;

import com.above.dto.InternshipApplyInfoDto;
import com.above.dto.InternshipCheckDto;
import com.above.dto.InternshipInfoFillDto;
import com.above.dto.LeaderList;
import com.above.po.InternshipInfoByStudent;
import com.above.vo.InternshipApplicationVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 学生实习信息表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface InternshipInfoByStudentMapper extends BaseMapper<InternshipInfoByStudent> {
    /**
     * @Description: 实习申请、实习岗位修改与企业变更申请、免实习申请、就业上报已审核列表（教师）
     * @Author: GG
     * @Date: 2022/06/24 15:28
     */
    List<InternshipCheckDto> internshipInfoCheckList(InternshipApplicationVo vo);

    /**
     * @Description: 实习申请、实习岗位修改与企业变更申请、免实习申请、就业上报未审核列表（教师）
     * @Author: GG
     * @Date: 2022/07/05 10:28
     */
    Integer countInternShipInfoUnCheck(InternshipApplicationVo vo);

    /**
     * @Description: 实习岗位已审核列表数量显示
     * @Author: GG
     * @Date: 2022/07/05 10:28
     */
    Integer countInternShipInfoChecked(InternshipApplicationVo vo);

    /**
    *@author: GG
    *@data: 2022/7/6 14:32
    *@function:实习申请、实习岗位修改与企业变更申请、免实习申请、就业上报未审核列表（教师）
    */
    List<InternshipCheckDto> internshipInfoUnCheckList(InternshipApplicationVo vo);

    /**
    *@author: GG
    *@data: 2022/7/12 15:23
    *@function:展示单条实习申请内容
    */
    InternshipApplyInfoDto displaySingleInternshipApplyInfo(InternshipApplicationVo vo);


    /**
    *@author: GG
    *@data: 2022/7/19 9:27
    *@function:获取实习岗位已填报学生列表
    */
    List<InternshipInfoFillDto> getInternshipApplyInfoFilled(InternshipApplicationVo vo);

    /**
     *@author: GG
     *@data: 2022/7/19 9:27
     *@function:获取实习岗位已填报学生数量
     */
    Integer getInternshipApplyInfoFilledCount(InternshipApplicationVo vo);

    /**
    *@author: GG
    *@data: 2022/7/19 9:28
    *@function:获取实习岗位未填报学生列表
    */
    List<InternshipInfoFillDto> getInternshipApplyInfoUnFill(InternshipApplicationVo vo);

    /**
     *@author: GG
     *@data: 2022/7/19 9:28
     *@function:获取实习岗位未填报学生数量
     */
    Integer getInternshipApplyInfoUnFillCount(InternshipApplicationVo vo);




}
