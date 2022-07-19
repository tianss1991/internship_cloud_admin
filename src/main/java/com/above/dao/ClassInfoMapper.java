package com.above.dao;

import com.above.dto.ClassDto;
import com.above.dto.ClassInfoDto;
import com.above.po.ClassInfo;
import com.above.po.ClassTeacherRelation;
import com.above.vo.BaseVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 班级 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-04-02
 */
@Repository
public interface ClassInfoMapper extends BaseMapper<ClassInfo> {
    /**
     * @Description: 获取班级列表
     * @Author: LZH
     * @Date: 2022/1/18 15:20
     */
    List<ClassInfoDto> getClassListWithOther(@Param("vo") BaseVo vo);

    /**
     * @Description: 获取班级列表总数
     * @Author: LZH
     * @Date: 2022/7/18 16:50
     */
    Integer getClassListWithOtherCount(@Param("vo") BaseVo vo);

    /**
     * @Description: 班级列表总数
     * @Author: LZH
     * @Date: 2022/3/8 21:14
     */
    Integer getCount(BaseVo vo);
    /**
     * 获取所有未删除的班级列表
     * @return
     */
    List<ClassInfoDto> getClassInfoDtoList();

    /**
     * 获取某班级班主任列表
     * @return 列表
     */
    List<ClassTeacherRelation> getClassTeacherList(@Param("classId")Integer classId);
    /**
     * 获取某班级辅导员列表
     * @return 列表
     */
    List<ClassTeacherRelation> getClassLeaderList(@Param("classId")Integer classId);

}
