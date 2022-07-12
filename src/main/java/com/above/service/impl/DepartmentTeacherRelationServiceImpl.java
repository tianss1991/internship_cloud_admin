package com.above.service.impl;

import com.above.dto.LeaderList;
import com.above.po.DepartmentInfo;
import com.above.po.DepartmentTeacherRelation;
import com.above.dao.DepartmentTeacherRelationMapper;
import com.above.service.DepartmentTeacherRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 二级学院教师关系表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Service
public class DepartmentTeacherRelationServiceImpl extends ServiceImpl<DepartmentTeacherRelationMapper, DepartmentTeacherRelation> implements DepartmentTeacherRelationService {

    @Autowired
    private DepartmentTeacherRelationMapper relationMapper;

    /**
     * @Description: 获取二级学院管理员
     * @Author: LZH
     * @Date: 2022/1/12 16:05
     */
    @Override
    public List<LeaderList> getLeaderListByDepartmentId(Integer id,Integer relationType) {

        return relationMapper.getLeaderList(id,relationType);
    }

    /**
     * @param teacherId
     * @Description: 获取用户关联的所有学校
     * @Author: LZH
     * @Date: 2022/5/23 11:57
     */
    @Override
    public List<DepartmentInfo> getTeacherRelationDepartment(Integer teacherId) {
        return relationMapper.getTeacherRelationDepartment(teacherId);
    }
}
