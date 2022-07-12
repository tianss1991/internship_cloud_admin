package com.above.service;

import com.above.dto.UserDto;
import com.above.po.UserAccount;
import com.above.utils.CommonResult;
import com.above.vo.UserAccountVo;
import com.above.vo.user.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户账号信息表（记录用户关联的手机，邮箱，微信等账号信息） 服务类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
public interface UserAccountService extends IService<UserAccount> {

    /**
     *  实习认证绑定
     * @param oldUser 用户用手机号登录的user信息
     * @param vo 传入的学号账号信息
     * @return 返回信息
     */
    CommonResult<Object> internshipCertification(UserDto oldUser, UserAccountVo vo,String token);


}
