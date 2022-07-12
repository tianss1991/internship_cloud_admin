package com.above.dao;

import com.above.dto.LeaderList;
import com.above.po.DepartmentInfo;
import com.above.po.DepartmentTeacherRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 二级学院教师关系表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Repository
public interface DepartmentTeacherRelationMapper extends BaseMapper<DepartmentTeacherRelation> {

    List<LeaderList> getLeaderList(@Param("id")Integer id,Integer relationType);

    List<DepartmentInfo> getTeacherRelationDepartment(@Param("teacherId")Integer teacherId);
}
