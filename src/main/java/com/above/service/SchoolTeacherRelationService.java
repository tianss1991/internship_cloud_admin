package com.above.service;

import com.above.dto.LeaderList;
import com.above.po.SchoolInfo;
import com.above.po.SchoolTeacherRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 学校教师关系表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
public interface SchoolTeacherRelationService extends IService<SchoolTeacherRelation> {

    List<LeaderList> getLeaderListBySchoolId(Integer id,Integer relationType);

    /**
     * @Description: 获取用户关联的所有学校
     * @Author: LZH
     * @Date: 2022/5/23 11:57
     */
    List<SchoolInfo> getTeacherRelationSchool(Integer teacherId);
}
