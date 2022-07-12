package com.above.service.impl;

import com.above.dao.AuthUserRoleMapper;
import com.above.dao.SchoolInfoMapper;
import com.above.dao.UserAccountMapper;
import com.above.dao.UserMapper;
import com.above.dto.UserDto;
import com.above.dto.UserList;
import com.above.po.AuthUserRole;
import com.above.po.SchoolInfo;
import com.above.po.UserAccount;
import com.above.service.UserAccountService;
import com.above.service.UserService;
import com.above.utils.CommonResult;
import com.above.utils.PasswordCryptoTool;
import com.above.vo.BaseVo;
import com.above.vo.UserAccountVo;
import com.above.vo.user.UserVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户账号信息表（记录用户关联的手机，邮箱，微信等账号信息） 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Slf4j
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthUserRoleMapper authUserRoleMapper;
    @Autowired
    private SchoolInfoMapper schoolInfoMapper;

    /**
     *  实习认证绑定
     * @param oldUser 用户用手机号登录的user信息
     * @param vo 传入的学号账号信息
     * @return 返回信息
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class,propagation = Propagation.REQUIRED)
    public CommonResult<Object> internshipCertification(UserDto oldUser, UserAccountVo vo,String token) {
        //用户输入的工号或者学号
        String accountNum = vo.getAccountNum();
        //账号密码
        String name = vo.getName();
        //学校名称
        String schoolName = vo.getSchoolName();
        //当前用户的手机号
        String telephone = oldUser.getTelephone();
        Integer userId = oldUser.getId();

        UserDto userDto = userService.getUserInfoByPhoneOrUserAccount(accountNum, null);

        if (userDto == null){
            return CommonResult.error(500,"用户不存在");
        }

        /*核对信息是否正确*/
        CommonResult<Object> result = checkInfo(userDto, name, schoolName);
        if (!result.isSuccess()){
            return result;
        }

        /*信息无误后进行账号绑定*/
        LambdaQueryWrapper<UserAccount> userAccountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userAccountLambdaQueryWrapper.eq(UserAccount::getUserId,userId).eq(UserAccount::getDeleted,BaseVo.UNDELETE);
        UserAccount userAccount = super.getOne(userAccountLambdaQueryWrapper);

        userAccount.setTelephone(telephone);
        boolean update = super.updateById(userAccount);
        if (!update){
            return CommonResult.error(500,"认证失败");
        }

        try {
            /*手机号绑定成功后删除当前账号信息*/
            boolean removeUser = userService.removeById(userId);
            if (!removeUser){
                throw new RuntimeException("删除用户异常");
            }
            /*删除账号信息*/
            LambdaQueryWrapper<UserAccount> userAccountQueryWrapper = new LambdaQueryWrapper<>();
            userAccountQueryWrapper.eq(UserAccount::getUserId,userId).eq(UserAccount::getDeleted,BaseVo.UNDELETE);
            boolean removeUserAccount = super.remove(userAccountQueryWrapper);
            if (!removeUserAccount){
                throw new RuntimeException("删除账号异常");
            }
            /*删除权限*/
            LambdaQueryWrapper<AuthUserRole> roleQueryWrapper = new LambdaQueryWrapper<>();
            roleQueryWrapper.eq(AuthUserRole::getUserId,userId).eq(AuthUserRole::getDeleted,BaseVo.UNDELETE);
            boolean removeRole = authUserRoleMapper.delete(roleQueryWrapper) > 0;
            if (!removeRole){
                throw new RuntimeException("删除权限异常");
            }
            UserDto userInfoByPhoneOrUserAccount = userService.getUserInfoByPhoneOrUserAccount(accountNum, null);

            SecurityUtils.getSubject().runAs(new SimplePrincipalCollection(userInfoByPhoneOrUserAccount.getUserRoleDto().getId(),userInfoByPhoneOrUserAccount.getUserRoleDto().getRoleCode()));

            // 放入缓存
            SecurityUtils.getSubject().getSession().setAttribute(token, userInfoByPhoneOrUserAccount);

            return CommonResult.success("切换成功");

        }catch (RuntimeException e){
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * 检查信息是否正确
     *
     * @param userDto    用户信息
     * @param name       姓名
     * @param schoolName 学校名称
     * @return 通用返回
     */
    private CommonResult<Object> checkInfo(UserDto userDto, String name, String schoolName) {
        /*核对信息是否正确*/
        //学校
        Integer schoolId = null;
        String nameStack = null;
        Integer userType = userDto.getUserType();
        if (userType.equals(UserVo.STUDENT)) {
            schoolId = userDto.getStudentInfo().getSchoolId();
            nameStack = userDto.getStudentInfo().getStudentName();
        }
        if (userType.equals(UserVo.TEACHER)) {
            schoolId = userDto.getTeacherInfo().getSchoolId();
            nameStack = userDto.getTeacherInfo().getTeacherName();
        }
        SchoolInfo schoolInfo = schoolInfoMapper.selectById(schoolId);
        if (schoolInfo == null || !schoolInfo.getSchoolName().equals(schoolName)){
            return CommonResult.error(500,"学校名称错误");
        }
        //姓名
        if (!name.equals(nameStack)){
            return CommonResult.error(500,"姓名填写错误");
        }

        return CommonResult.success(null);
    }


}
