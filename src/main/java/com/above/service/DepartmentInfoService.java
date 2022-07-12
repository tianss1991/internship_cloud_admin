package com.above.service;

import com.above.dto.UserDto;
import com.above.po.DepartmentInfo;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.DepartmentVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

/**
 * <p>
 * 二级学院 服务类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
public interface DepartmentInfoService extends IService<DepartmentInfo> {


    /**
     * @Description: 添加二级学院
     * @Author: LZH
     * @Date: 2022/1/12 11:32
     */
    CommonResult<Object> addDepartment(UserDto userDto, DepartmentVo departmentVo);

    /**
     * @Description: 修改二级学院
     * @Author: LZH
     * @Date: 2022/1/12 11:32
     */
    CommonResult<Object> modifyDepartment(UserDto userDto, DepartmentVo departmentVo);

    /**
     * @Description: 批量删除二级学院
     * @Author: LZH
     * @Date: 2022/1/12 11:32
     */
    CommonResult<Object> deleteDepartment(UserDto userDto, DepartmentVo departmentVo);

    /**
     * @Description: 获取二级学院列表（分页）
     * @Author: LZH
     * @Date: 2022/1/12 11:32
     */
    CommonResult<Object> getDepartmentPageList(UserDto userDto, DepartmentVo departmentVo);

    /**
     * @Description: 获取二级学院列表（不分页）
     * @Author: LZH
     * @Date: 2022/1/12 11:32
     */
    CommonResult<Object> getDepartmentWithoutPage(UserDto userDto, DepartmentVo departmentVo);

    /**
     * @Description: 删除二级学院管理员
     * @Author: LZH
     * @Date: 2022/1/12 17:34
     */
    CommonResult<Object> deleteDepartmentLeader(Integer updateBy, Collection<Integer> deleteTeacherIdList, Integer departmentId);

    /**
     * @Description: 刪除领导判断权限
     * @Author: LZH
     * @Date: 2022/1/13 8:48
     */
    CommonResult<Object> deleteDepartmentLeaderRole(Integer teacherId, Integer relationId);

    /**
     * @Description: 二级学院导入
     * @Author: LZH
     * @Date: 2022/1/17 17:03
     */
    CommonResult<Object> importDepartmentInfo(UserDto userDto, MultipartFile file) throws RuntimeException;



}
