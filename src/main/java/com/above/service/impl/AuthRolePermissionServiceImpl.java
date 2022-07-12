package com.above.service.impl;

import com.above.dao.AuthPermissionMapper;
import com.above.dto.AuthRolePermissionDto;
import com.above.po.AuthRolePermission;
import com.above.dao.AuthRolePermissionMapper;
import com.above.service.AuthRolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色与权限关联表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Service
public class AuthRolePermissionServiceImpl extends ServiceImpl<AuthRolePermissionMapper, AuthRolePermission> implements AuthRolePermissionService {

    @Autowired
    private AuthPermissionMapper authPermissionMapper;

    @Override
    public List<AuthRolePermissionDto> getPermission(Integer roleId) {

        return authPermissionMapper.getPermission(roleId);
    }
}
