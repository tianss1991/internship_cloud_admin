package com.above.dao;

import com.above.dto.SignAndApplyDto;
import com.above.po.SignInfoByStudent;
import com.above.vo.BaseVo;
import com.above.vo.SignInfoVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 签到记录表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Repository
public interface SignInfoByStudentMapper extends BaseMapper<SignInfoByStudent> {

    /**
     *  获取某天签到列表
     * @param signInfoVo 查询参数
     * @return 返回列表
     */
    List<SignAndApplyDto> getSignWithApplyIdListByDate(SignInfoVo signInfoVo);

    /**
     *  获取一段时间内的签到列表
     * @param signInfoVo 查询参数
     * @return 返回列表
     */
    List<SignAndApplyDto> getSignWithApplyListByStartAndEndTime(SignInfoVo signInfoVo);

    /**
     *  签到详情列表
     * @param signInfoVo 查询参数
     * @return 返回列表
     */
    List<SignAndApplyDto> getSignListDetail(SignInfoVo signInfoVo);

    /**
     *  签到详情
     * @param signInfoVo 查询参数
     * @return 返回列表
     */
    Integer getSignListDetailTotalCount(SignInfoVo signInfoVo);
}
