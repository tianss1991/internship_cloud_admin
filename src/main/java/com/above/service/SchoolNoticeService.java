package com.above.service;

import com.above.dto.UserDto;
import com.above.po.SchoolNotice;
import com.above.utils.CommonResult;
import com.above.vo.SchoolNoticeVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 学校公告 服务类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface SchoolNoticeService extends IService<SchoolNotice> {

    /**
     * @Description: 发布学校公告
     * @Author: YJH
     * @Date: 2022/6/30 11:18
     */
    CommonResult<Object> addNotice(UserDto userDto, SchoolNoticeVo vo);

    /**
     * @Description: 获取学校公告
     * @Author: YJH
     * @Date: 2022/6/29 16:40
     */
    CommonResult<Object> getNoticeList(UserDto userDto,SchoolNoticeVo vo);

    /**
     * @Description: 公告详情
     * @Author: YJH
     * @Date: 2022/6/30 10:02
     */
    CommonResult<Object> getNotice(UserDto userDto, SchoolNoticeVo vo);


}
