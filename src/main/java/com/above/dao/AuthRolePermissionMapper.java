package com.above.dao;

import com.above.dto.LeaderList;
import com.above.po.AuthRolePermission;
import com.above.utils.CommonResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 角色与权限关联表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Repository
public interface AuthRolePermissionMapper extends BaseMapper<AuthRolePermission> {
    /**
     * @Description: 根据教师id授予权限
     * @Author:
     * @Date: 2022/3/24 15:24
     */

}
