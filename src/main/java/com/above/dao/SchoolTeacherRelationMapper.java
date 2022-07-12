package com.above.dao;

import com.above.dto.LeaderList;
import com.above.po.SchoolInfo;
import com.above.po.SchoolTeacherRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 学校教师关系表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Repository
public interface SchoolTeacherRelationMapper extends BaseMapper<SchoolTeacherRelation> {
    /**
     * @Description: 根据学校id获取校领导
     * @Author: LZH
     * @Date: 2022/1/12 16:02
     */
    List<LeaderList> getLeaderList(@Param("id")Integer id ,@Param("relationType")Integer relationType);

    List<SchoolInfo> getTeacherRelationSchool(@Param("teacherId") Integer teacherId);
}
