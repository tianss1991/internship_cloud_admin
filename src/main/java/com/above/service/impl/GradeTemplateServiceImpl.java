package com.above.service.impl;

import com.above.dto.GradeTemplateDto;
import com.above.dto.PlanWithOther;
import com.above.dto.UserDto;
import com.above.po.AuthRole;
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

        Map<String, Object> returnMap = new HashMap<>(16);

        returnMap.put(BaseVo.LIST,gradePidById);

        return CommonResult.success(returnMap);
    }

    /**
     * 获取实习计划列表
     *
     * @param vo      前端参数
     * @param userDto 用户
     * @return 返回值
     */
    @Override
    public CommonResult<Object> getPlanWithTemplate(BaseVo vo, UserDto userDto) {
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        //根据不同权限放入筛选条件
        switch (roleCode){
            case AuthRole.SCHOOL_ADMIN :
                if (vo.getSchoolId() == null){
                    vo.setSchoolIdList(userDto.getSchoolIds());
                }
                break;
            case AuthRole.DEPARTMENT_ADMIN :
                if (vo.getDepartmentId() == null){
                    vo.setDepartmentIdList(userDto.getDepartmentIds());
                }
                break;
            default:break;
        }
        //获取列表
        List<PlanWithOther> list = super.baseMapper.getPlanWithGradeList(vo);
        Integer totalCount = super.baseMapper.getPlanWithGradeTotalCount(vo);
        //返回对象
        Map<String, Object> returnMap = new HashMap<>(16);

        returnMap.put(BaseVo.LIST,list);
        returnMap.put(BaseVo.PAGE,BaseVo.calculationPages(vo.getSize(),totalCount));
        returnMap.put(BaseVo.TOTAL,totalCount);

        return CommonResult.success(returnMap);
    }
}
