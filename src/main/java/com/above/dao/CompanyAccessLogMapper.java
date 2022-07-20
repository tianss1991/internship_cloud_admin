package com.above.dao;

import com.above.dto.CompanyAccessLogDto;
import com.above.dto.PlanWithOther;
import com.above.po.CompanyAccessLog;
import com.above.vo.BaseVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * <p>
 * 企业寻访记录表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-07-15
 */
public interface CompanyAccessLogMapper extends BaseMapper<CompanyAccessLog> {


    /**
     *  管理端获取列表
     * @param vo 查询参数
     * @return 返回列表
     */
    List<CompanyAccessLogDto> getListForAdmin(BaseVo vo);

    /**
     *  管理端获取列表总数
     * @param vo 查询参数
     * @return 返回列表
     */
    Integer getListForAdminCount(BaseVo vo);

    /**
     *  获取设置次数岗位列表
     * @param vo 查询参数
     * @return 返回列表
     */
    List<PlanWithOther> getPlanWithOther(BaseVo vo);

    /**
     *  获取设置次数岗位列表总数
     * @param vo 查询参数
     * @return 返回列表
     */
    Integer getPlanWithOtherCount(BaseVo vo);

}
