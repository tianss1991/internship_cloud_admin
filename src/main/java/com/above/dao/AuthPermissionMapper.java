package com.above.dao;

import com.above.dto.AuthRolePermissionDto;
import com.above.po.AuthPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Repository
public interface AuthPermissionMapper extends BaseMapper<AuthPermission> {
    /**
     *  获取权限信息
     * @param roleId
     * @return
     */
    List<AuthRolePermissionDto> getPermission(@Param("roleId")Integer roleId);

}
