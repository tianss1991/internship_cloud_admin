package com.above.service.impl;

import com.above.po.AuthRole;
import com.above.dao.AuthRoleMapper;
import com.above.service.AuthRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Service
@Lazy
public class AuthRoleServiceImpl extends ServiceImpl<AuthRoleMapper, AuthRole> implements AuthRoleService {

}
