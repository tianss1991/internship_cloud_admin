package com.above.dao;

import com.above.dto.StudentInfoDto;
import com.above.po.StudentInfo;
import com.above.vo.BaseVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 学生信息表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-06-24
 */
@Repository
public interface StudentInfoMapper extends BaseMapper<StudentInfo> {


    /**
     *  根据id获取学生信息
     * @param studentId 学生id
     * @return 返回学生信息包含基础院校信息
     */
    StudentInfoDto selectStudentById(@Param("studentId") Integer studentId);

    /**
     *  根据userId获取学生信息
     * @param studentId 学生id
     * @return 返回学生信息包含基础院校信息
     */
    StudentInfoDto selectStudentByUserId(@Param("userId") Integer userId);

    /**
     *  根据baseVo查询学生列表
     * @param vo 查询参数对象
     * @return 返回学生信息包含基础院校信息
     */
    List<StudentInfoDto> selectStudentListByBaseVo(BaseVo vo);
}
