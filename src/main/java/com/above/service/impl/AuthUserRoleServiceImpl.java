package com.above.service.impl;

import com.above.po.AuthUserRole;
import com.above.dao.AuthUserRoleMapper;
import com.above.service.AuthUserRoleService;
import com.above.vo.BaseVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户与角色关联表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Service
public class AuthUserRoleServiceImpl extends ServiceImpl<AuthUserRoleMapper, AuthUserRole> implements AuthUserRoleService {

    @Override
    public boolean checkUserRole(Integer userId, Integer roleId) {
        QueryWrapper<AuthUserRole> authUserRoleQueryWrapper = new QueryWrapper<>();
        authUserRoleQueryWrapper.eq("user_id",userId).eq("role_id",roleId).eq("deleted", BaseVo.UNDELETE);
        int count2 = super.count(authUserRoleQueryWrapper);
        return count2 > 0;
    }
}



