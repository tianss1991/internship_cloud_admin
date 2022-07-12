package com.above.service;

import com.above.dto.UserDto;
import com.above.po.SchoolInfo;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.SchoolVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

/**
 * <p>
 * 学校信息 服务类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
public interface SchoolInfoService extends IService<SchoolInfo> {

    /**
     * @Description: 添加学校
     * @Author: LZH
     * @Date: 2022/1/10 11:34
     */
    CommonResult<Object> addSchoolInfo(UserDto userDto, SchoolVo schoolVo);
    /**
     * @Description: 修改学校
     * @Author: LZH
     * @Date: 2022/1/10 11:34
     */
    CommonResult<Object> modifySchoolInfo(UserDto userDto, SchoolVo schoolVo);
    /**
     * @Description: 删除学校
     * @Author: LZH
     * @Date: 2022/1/10 16:55
     */
    CommonResult<Object> deleteSchoolInfo(UserDto userDto, SchoolVo schoolVo);
    /**
     * @Description: 获取学校列表不分页
     * @Author: LZH
     * @Date: 2022/1/10 19:21
     */
    CommonResult<Object> getSchoolWithoutPage(UserDto userDto, BaseVo vo);
    /**
     * @Description:
     * @Author: LZH
     * @Date: 2022/1/10 19:22
     */
    CommonResult<Object> getSchoolPageList(UserDto userDto, BaseVo vo);

    /**
     * @Description: 删除校领导
     * @Author: LZH
     * @Date: 2022/1/12 17:34
     */
    CommonResult<Object> deleteSchoolLeader(Integer updateBy,Collection<Integer> deleteTeacherIdList, Integer schoolId, Integer relationType);

    /**
     * @Description: 添加校领导
     * @Author: LZH
     * @Date: 2022/1/12 17:34
     */
    CommonResult<Object> addSchoolLeader(Integer createBy,Collection<Integer> TeacherIdList, Integer schoolId, Integer relationType);
    /**
     * @Description: 刪除领导判断权限
     * @Author: LZH
     * @Date: 2022/1/13 8:48
     */
    CommonResult<Object> deleteSchoolRole(Integer teacherId, Integer relationId);

    /**
     * @Description: 院校信息导入
     * @Author: LZH
     * @Date: 2022/1/17 10:06
     */
    CommonResult<Object> importSchoolInfo(UserDto userDto, MultipartFile file);

}
