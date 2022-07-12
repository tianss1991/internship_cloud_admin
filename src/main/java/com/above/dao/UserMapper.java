package com.above.dao;

import com.above.dto.UserDto;
import com.above.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 用户信息表（只存储用户状态与密码） Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
    /**
     * 获取用户信息
     * @param phoneOrAccount
     * @return
     */
    UserDto getUserInfoByPhoneOrUserAccount(@Param("phoneOrAccount") String phoneOrAccount);

    /**
     * @Description: 获取时间内用户注册
     * @Author: LZH
     * @Date: 2022/1/21 14:30
     */
    Integer getRegisterUser(@Param("start")String start, @Param("end") String end);

    List<UserDto> getAll();

    /**
     * @Description: 根据id获取信息
     * @Author: LZH
     * @Date: 2022/5/9 15:03
     */
    UserDto getUserDtoById(@Param("id") Integer id);
}
