package com.above.service;

import com.above.dto.UserDto;
import com.above.po.MajorInfo;
import com.above.utils.CommonResult;
import com.above.vo.MajorInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 专业 服务类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface MajorInfoService extends IService<MajorInfo> {

    /**
     * @Description:
     * @Author: YJH
     * @Date: 2022/6/22 15:18
     */
    CommonResult<Object> addMajor(UserDto userDto, MajorInfoVo majorInfoVo);

    /**
     * @Description: 修改专业
     * @Author: YJH
     * @Date: 2022/6/23 09:48
     */
    CommonResult<Object> modifyMajInfo(UserDto userDto, MajorInfoVo majorInfoVo);

    /**
    * @Description: 批量删除专业
    * @Author: YJH
    * @Date: 2022/6/23 10:30
    */

    CommonResult<Object> deleteMajor(UserDto userDto, MajorInfoVo majorInfoVo);

    /**
     * @Description: 获取班级列表（分页）
     * @Author: YJH
     * @Date: 2022/6/23 11:28
     */
    CommonResult<Object> getmajorWithoutPage(UserDto userDto, MajorInfoVo majorInfoVo);

    /**
     * @Description: 获取班级列表（不分页）
     * @Author: YJH
     * @Date: 2022/6/23 11:28
     */
    CommonResult<Object> getmajorPageList(UserDto userDto, MajorInfoVo majorInfoVo);

}
