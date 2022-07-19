package com.above.dao;

import com.above.dto.SignApplyWithStudentDto;
import com.above.po.SignApplyInfo;
import com.above.vo.SignInfoVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 签到申请管理表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface SignApplyInfoMapper extends BaseMapper<SignApplyInfo> {

    /**
     *  获取补签审核列表
     * @param vo 前端参数
     * @return 返回列表
     */
    List<SignApplyWithStudentDto> getSignApplyList(SignInfoVo vo);
    /**
     *  获取补签审核列表总数
     * @param vo 前端参数
     * @return 返回列表
     */
    Integer getSignApplyListTotalCount(SignInfoVo vo);

}
