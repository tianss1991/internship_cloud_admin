package com.above.service.impl;

import com.above.dto.GradeTemplateDto;
import com.above.dto.UserDto;
import com.above.po.GradeTemplate;
import com.above.dao.GradeTemplateMapper;
import com.above.service.GradeTemplateService;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 成绩模板表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-07-14
 */
@Service
public class GradeTemplateServiceImpl extends ServiceImpl<GradeTemplateMapper, GradeTemplate> implements GradeTemplateService {

    /**
     * 根据实习计划id查询模板
     *
     * @param vo      前端参数
     * @param userDto 用户
     * @return 返回值
     */
    @Override
    public CommonResult<Object> getGradeTemplateByPlanId(BaseVo vo, UserDto userDto) {

        List<GradeTemplateDto> gradePidById = super.baseMapper.getGradePidById(vo);

        Map<String, Object> returnMap = new HashMap<>(4);

        returnMap.put(BaseVo.LIST,gradePidById);

        return CommonResult.success(returnMap);
    }
}
