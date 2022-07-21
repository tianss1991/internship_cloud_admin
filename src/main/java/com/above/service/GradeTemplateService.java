package com.above.service;

import com.above.dto.UserDto;
import com.above.po.GradeTemplate;
import com.above.utils.CommonResult;
import com.above.utils.CommonUtil;
import com.above.vo.BaseVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 成绩模板表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-07-14
 */
public interface GradeTemplateService extends IService<GradeTemplate> {

    /**
     *  根据实习计划id查询模板
     * @param vo  前端参数
     * @param userDto 用户
     * @return 返回值
     */
    CommonResult<Object> getGradeTemplateByPlanId(BaseVo vo, UserDto userDto);

    /**
     *  获取实习计划列表
     * @param vo  前端参数
     * @param userDto 用户
     * @return 返回值
     */
    CommonResult<Object> getPlanWithTemplate(BaseVo vo, UserDto userDto);
}
