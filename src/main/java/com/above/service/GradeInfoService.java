package com.above.service;

import com.above.dto.UserDto;
import com.above.po.GradeInfo;
import com.above.utils.CommonResult;
import com.above.vo.GradeVo;
import com.above.vo.InternshipApplicationVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 年级 服务类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface GradeInfoService extends IService<GradeInfo> {
    /**
     * 新建年级
     * */
    CommonResult<Object> addGradeInfo(GradeVo vo, UserDto userDto);
    /**
     * 删除年级
     * */
    CommonResult<Object> deleteGradeInfo(GradeVo vo, UserDto userDto);
    /**
     * 修改年级
     * */
    CommonResult<Object> modifyGradeInfo(GradeVo vo, UserDto userDto);
    /**
     * 通过年级id查询年级
     * */
    CommonResult<Object> queryGradeInfo(GradeVo vo, UserDto userDto);
    /**
     * 通过学校查询所有年级
     * */
    CommonResult<Object> queryGradeList(GradeVo vo, UserDto userDto);
}
