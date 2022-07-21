package com.above.dao;

import com.above.dto.GradeTemplateDto;
import com.above.dto.PlanWithOther;
import com.above.po.GradeTemplate;
import com.above.vo.BaseVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 成绩模板表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-07-14
 */
public interface GradeTemplateMapper extends BaseMapper<GradeTemplate> {

    /**
     *  获取父级模板
     * @param vo  参数
     * @return 返回父级
     */
    List<GradeTemplateDto> getGradePidById(BaseVo vo);

    /**
     *  获取父级模板
     * @param id  父级id
     * @return 返回父级
     */
    GradeTemplateDto getGradeChildrenById(@Param("id") Integer id);

    /**
     *  获取实习计划成绩比例列表
     * @param vo 前端参数
     * @return 返回列表
     */
    List<PlanWithOther> getPlanWithGradeList(BaseVo vo);
    /**
     *  获取实习计划成绩比例列表总数
     * @param vo 前端参数
     * @return 返回列表
     */
    Integer getPlanWithGradeTotalCount(BaseVo vo);


}
