package com.above.service;

import com.above.dto.UserDto;
import com.above.po.User;
import com.above.utils.CommonResult;
import com.above.vo.user.UpdateUserVo;
import com.above.vo.user.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户信息表（只存储用户状态与密码） 服务类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
public interface UserService extends IService<User> {
    /**
     *  获取验证码
     * @param userVo
     * @return 返回是否成功
     */
    CommonResult<Object> getCode(UserVo userVo);

    /**
     *  登录
     * @param userVo
     * @return
     */
    CommonResult<Object> login(UserVo userVo);


    /**
     *  根据token 获取用户信息
     * @return 通用返回类
     */
    CommonResult<Object> getUserInfoByToken(HttpServletRequest request);

    /**
     *  获取用户信息
     * @param phoneOrAccount
     * @return
     */
    UserDto getUserInfoByPhoneOrUserAccount(String phoneOrAccount,Object loginType);

    /**
     *  手机号用户注册
     * @param phoneOrAccount 用户账号
     * @return 返回用户的注册信息
     */
    UserDto registerUser(String phoneOrAccount);

    /**
     * 修改用户信息
     * @param updateUserVo
     * @return
     */
    CommonResult<Object> updateUserInfo(UpdateUserVo updateUserVo);

    /**
     * @Description: 重置密码
     * @Author: LZH
     * @Date: 2022/2/22 10:41
     */
    CommonResult<Object> resetPassword(UserDto operator,UserVo vo);

    /**
     * 用户选择切换身份
     * @param userVo 前端参数
     * @return 通用返回
     */
    CommonResult<Object> chooseRole(HttpServletRequest request, UserVo userVo);

    /**
     * @Description: 用户获取权限列表
     * @Author: LZH
     * @Date: 2022/3/3 14:02
     */
    CommonResult<Object> getChooseRoleList(UserDto userDto,UserVo vo);
}
