package com.above.dao;

import com.above.bean.theory.ClassInfoDto;
import com.above.dto.ClassListWithOther;
import com.above.po.ClassInfo;
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
    List<ClassListWithOther> getClassListWithOther(@Param("vo") BaseVo vo);
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
}
