package com.above.service;

import com.above.dto.AuthRolePermissionDto;
import com.above.po.AuthRolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色与权限关联表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
public interface AuthRolePermissionService extends IService<AuthRolePermission> {

    /**
     *  获取权限信息
     * @param roleId
     * @return
     */
    List<AuthRolePermissionDto> getPermission(Integer roleId);

}
