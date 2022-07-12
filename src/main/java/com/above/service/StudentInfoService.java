package com.above.service;

import com.above.dto.UserDto;
import com.above.po.StudentInfo;
import com.above.utils.CommonResult;
import com.above.vo.LeaveApplyInfoVo;
import com.above.vo.StudentVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 学生信息表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-06-24
 */
public interface StudentInfoService extends IService<StudentInfo> {

    /**
     * 增加学生(管理员)
     * */
    CommonResult<Object> addStudent(StudentVo vo, UserDto userDto);

    /**
     * 修改学生(管理员)
     * */
    CommonResult<Object> modifyStudent(StudentVo vo, UserDto userDto);

    /**
     * 删除学生(管理员)
     * */
    CommonResult<Object> deleteStudent(StudentVo vo, UserDto userDto);

//    /**
//     * 批量删除学生(管理员)
//     * */
//    CommonResult<Object> deleteStudentByIds(StudentVo vo, UserDto userDto);

    /**
     * 显示学生列表（分页）(管理员)
     * */
    CommonResult<Object> displayStudentListPage(StudentVo vo, UserDto userDto);

    /**
     * 显示学生列表（不分页）(管理员)
     * */
    CommonResult<Object> displayStudentListNoPage(StudentVo vo, UserDto userDto);

    /**
     * @Description: 导入学生信息
     * @Author: GG
     * @Date: 2022/07/04 08:34
     */
    CommonResult<Object> importStudentInfo(UserDto userDto, MultipartFile file);

}
