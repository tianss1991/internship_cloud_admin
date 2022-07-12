package com.above.service.impl;

import com.above.dto.LeaderList;
import com.above.po.SchoolInfo;
import com.above.po.SchoolTeacherRelation;
import com.above.dao.SchoolTeacherRelationMapper;
import com.above.service.SchoolTeacherRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 学校教师关系表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Service
public class SchoolTeacherRelationServiceImpl extends ServiceImpl<SchoolTeacherRelationMapper, SchoolTeacherRelation> implements SchoolTeacherRelationService {

    /**
     * @Description: 根据学校id获取领导
     * @Author: LZH
     * @Date: 2022/1/10 19:54
     */
    @Override
    public List<LeaderList> getLeaderListBySchoolId(Integer id,Integer relationType) {

        return this.getBaseMapper().getLeaderList(id,relationType);
    }

    /**
     * @param teacherId
     * @Description: 获取用户关联的所有学校
     * @Author: LZH
     * @Date: 2022/5/23 11:57
     */
    @Override
    public List<SchoolInfo> getTeacherRelationSchool(Integer teacherId) {
        return this.getBaseMapper().getTeacherRelationSchool(teacherId);
    }
}
