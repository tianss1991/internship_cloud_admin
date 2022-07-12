package com.above.service.impl;

import com.above.dto.UserDto;
import com.above.po.SignApplyInfo;
import com.above.dao.SignApplyInfoMapper;
import com.above.service.SignApplyInfoService;
import com.above.utils.CommonResult;
import com.above.vo.SignApplyInfoVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 签到申请管理表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Service
public class SignApplyInfoServiceImpl extends ServiceImpl<SignApplyInfoMapper, SignApplyInfo> implements SignApplyInfoService {

    /**
     * 补卡申请
     * */
    @Override
    public CommonResult<Object> saveSignApplyInfoAgain(SignApplyInfoVo vo, UserDto userDto) {
        return null;
    }
}
