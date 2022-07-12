package com.above.service;

import com.above.po.AuthUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户与角色关联表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
public interface AuthUserRoleService extends IService<AuthUserRole> {


    /**
     * 判断该用户是否有该权限
     * @param userId 用户id
     * @param roleId 角色id 1-超管 2-校管 3-二级学院管理 4-辅导员 5-指导老师 6-普通教师 7-学生
     * @return true-有权限 false-无权限
     */
    boolean checkUserRole(Integer userId,Integer roleId);


}
