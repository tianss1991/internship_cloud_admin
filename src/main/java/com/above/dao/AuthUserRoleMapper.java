package com.above.dao;

import com.above.po.AuthUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户与角色关联表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Repository
public interface AuthUserRoleMapper extends BaseMapper<AuthUserRole> {

}
