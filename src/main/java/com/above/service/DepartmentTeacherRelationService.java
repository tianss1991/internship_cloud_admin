package com.above.service;

import com.above.dto.LeaderList;
import com.above.po.DepartmentInfo;
import com.above.po.DepartmentTeacherRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 二级学院教师关系表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
public interface DepartmentTeacherRelationService extends IService<DepartmentTeacherRelation> {

    List<LeaderList> getLeaderListByDepartmentId(Integer id,Integer relationType);

    /**
     * @Description: 获取用户关联的所有学校
     * @Author: LZH
     * @Date: 2022/5/23 11:57
     */
    List<DepartmentInfo> getTeacherRelationDepartment(Integer teacherId);

}
