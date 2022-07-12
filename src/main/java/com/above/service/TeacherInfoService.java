package com.above.service;

import com.above.dto.UserDto;
import com.above.po.TeacherInfo;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.TeacherVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 教职工信息表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
public interface TeacherInfoService extends IService<TeacherInfo> {

    /**
     * @Description: 添加教职工（生成用户信息）
     * @Author: LZH
     * @Date: 2022/1/11 11:08
     */
    CommonResult<Object> insertTeacherInfo(UserDto userDto, TeacherVo teacherVo);
    /**
     * @Description: 修改教职工信息
     * @Author: LZH
     * @Date: 2022/1/11 11:11
     */
    CommonResult<Object> modifyTeacher(UserDto userDto,TeacherVo teacherVo) throws RuntimeException;
    /**
     * @Description: 删除教师
     * @Author: LZH
     * @Date: 2022/1/11 11:12
     */
    CommonResult<Object> deleteTeacher(UserDto userDto,TeacherVo teacherVo);
    /**
     * @Description: 获取教师列表（分页）
     * @Author: LZH
     * @Date: 2022/1/11 11:13
     */
    CommonResult<Object> getTeacherPageList(UserDto userDto,TeacherVo teacherVo);
    /**
     * @Description: 获取教师列表（不分页）
     * @Author: LZH
     * @Date: 2022/1/11 11:13
     */
    CommonResult<Object> getTeacherWithoutList(UserDto userDto,TeacherVo teacherVo);
    /**
     * @Description: 导入教师信息
     * @Author: LZH
     * @Date: 2022/1/13 16:34
     */
    CommonResult<Object> importTeacherInfo(UserDto userDto, MultipartFile file, BaseVo baseVo);

    /**
     * @Description: 添加教师信息（姓名工号）
     * @Author: LZH
     * @Date: 2022/4/18 10:01
     *
     */
    TeacherInfo addNewTeacher(UserDto userDto, String teacherName, String workNumber);

    /**
     * @Description: 根据学校id删除教职工
     * @Author: LZH
     * @Date: 2022/4/27 10:53
     */
    CommonResult<Object> deleteTeacherBySchoolId(UserDto userDto,TeacherVo teacherVo) throws RuntimeException;

    /**
     * @Description: 根据学校id删除教职工
     * @Author: LZH
     * @Date: 2022/4/27 10:53
     */
    CommonResult<Object> deleteTeacherByDepartmentId(UserDto userDto,TeacherVo teacherVo) throws RuntimeException;
}
