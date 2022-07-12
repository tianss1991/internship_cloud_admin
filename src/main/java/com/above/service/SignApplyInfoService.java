package com.above.service;

import com.above.dto.UserDto;
import com.above.po.SignApplyInfo;
import com.above.utils.CommonResult;
import com.above.vo.SignApplyInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 签到申请管理表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface SignApplyInfoService extends IService<SignApplyInfo> {
    /**
     * 补签申请
     * */
    CommonResult<Object> saveSignApplyInfoAgain(SignApplyInfoVo vo, UserDto userDto);

}
