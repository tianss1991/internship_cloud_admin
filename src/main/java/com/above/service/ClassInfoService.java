package com.above.service;

import com.above.bean.theory.ClassInfoDto;
import com.above.dto.UserDto;
import com.above.po.ClassInfo;
import com.above.utils.CommonResult;
import com.above.vo.ClassVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 班级 服务类
 * </p>
 *
 * @author mp
 * @since 2022-04-02
 */
public interface ClassInfoService extends IService<ClassInfo> {

    /**
     * @Description: 添加班级
     * @Author: LZH
     * @Date: 2022/1/12 11:32
     */
    CommonResult<Object> addClass(UserDto userDto, ClassVo classVo);
    /**
     * @Description: 修改班级
     * @Author: LZH
     * @Date: 2022/1/12 11:32
     */
    CommonResult<Object> modifyClass(UserDto userDto, ClassVo classVo);

    /**
     * @Description: 批量删除班级
     * @Author: LZH
     * @Date: 2022/1/12 11:32
     */
    CommonResult<Object> deleteClass(UserDto userDto,ClassVo classVo);
    /**
     * @Description: 获取班级列表（分页）
     * @Author: LZH
     * @Date: 2022/1/12 11:32
     */
    CommonResult<Object> getClassPageList(UserDto userDto,ClassVo classVo);
    /**
     * @Description: 获取班级列表（不分页）
     * @Author: LZH
     * @Date: 2022/1/12 11:32
     */
    CommonResult<Object> getClassWithoutPage(UserDto userDto,ClassVo classVo);


    /**
     *  获取班级-二级学院列表
     * @param userDto  用户信息
     * @param classVo 前端传入参数
     * @return 返回类
     */
    CommonResult<Object> getDepartmentAndClass(UserDto userDto,ClassVo classVo);

    /**
     * 获取所有未删除的班级列表
     * @return
     */
    List<ClassInfoDto> getClassInfoDtoList();

    /**
     * @Description:
     * @Author: LZH
     * @Date: 2022/3/8 10:48
     */
    CommonResult<Object> importClass(UserDto userDto, MultipartFile file);

}
