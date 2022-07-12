package com.above.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.above.config.sms.SmsConfig;
import com.above.constans.exception.RedisException;
import com.above.dao.StudentInfoMapper;
import com.above.dao.TeacherInfoMapper;
import com.above.dao.UserMapper;
import com.above.dto.UserDto;
import com.above.dto.UserRoleDto;
import com.above.po.*;
import com.above.service.*;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.utils.PasswordCryptoTool;
import com.above.vo.BaseVo;
import com.above.vo.user.UpdateUserVo;
import com.above.vo.user.UserVo;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>
 * 用户信息表（只存储用户状态与密码） 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final transient String SUCCESS = "SUCCESS";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private AuthRoleService authRoleService;
    @Autowired
    private AuthUserRoleService authUserRoleService;
    @Autowired
    private SchoolTeacherRelationService schoolTeacherRelationService;
    @Autowired
    private DepartmentTeacherRelationService departmentTeacherRelationService;
    @Autowired
    private DepartmentInfoService departmentInfoService;
    @Autowired
    private SchoolInfoService schoolInfoService;
    @Autowired
    private TeacherInfoMapper teacherInfoMapper;
    @Autowired
    private StudentInfoMapper studentInfoMapper;
    @Autowired
    private  ClassTeacherRelationService classTeacherRelationService;
    @Autowired
    private InternshipInfoByStudentService infoByStudentService;
    @Autowired
    private InternshipPlanInfoService planInfoService;

    /**
     * @Decription: 获取验证码
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/10 14:02
     */
    @Override
    public CommonResult<Object> getCode(UserVo userVo) {

        // 判断是否有手机号，手机号是否符合格式
        if (userVo == null || userVo.getTelephone() == null || !MyStringUtils.isTelephone(userVo.getTelephone())) {
            return CommonResult.error(500, "请输入正确的手机号");
        }
        // 生成验证码，并且放入redis
        String phoneCode = MyStringUtils.getPhoneCode(4);
//         调用发送短信的sdk发送给用户
        String messageForCode = this.getMessageForCode(userVo.getTelephone(), phoneCode);
//         判断是否发送成功
        if (messageForCode.equalsIgnoreCase(SmsConfig.ERROR_STATUS)) {
            return CommonResult.error(500, "短信发送失败");
        }
        // TODO 设置固定的值 测试
//        String phoneCode = "9999";
        // 放入缓存
        Map<String, Object> map = new HashMap<>(16);
        map.put("code", phoneCode);
        map.put("time", System.currentTimeMillis());
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute(SmsConfig.REDIS_CODE + userVo.getTelephone(), map);

        log.info(session.getAttribute(SmsConfig.REDIS_CODE + userVo.getTelephone()).toString());
        log.info("sessionid"+session.getId());
        // 返回参数
        return CommonResult.success("发送成功", session.getId());
    }

    @Override
    public CommonResult<Object> login(UserVo userVo) {
        // 判断登录方式
        if (userVo == null) {
            return CommonResult.error(500, "请选择一种登录方式");
        }
        // 获取shiro对象
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = null;
        Session session = subject.getSession();
        Map<String, Object> returnMap = new HashMap<>(16);
        //登录端类型
        Integer loginType = userVo.getLoginType();
        session.setAttribute("loginType", loginType);
        // 使用手机验证码登录
        if (!StringUtils.isBlank(userVo.getTelephone()) && !StringUtils.isBlank(userVo.getCode())) {
            token = new UsernamePasswordToken(userVo.getTelephone(),userVo.getCode());
            // 将验证码丢入缓存中
            session.setAttribute(userVo.getTelephone(),userVo.getCode());
            // 是否是验证码
            session.setAttribute("flag",true);
            subject.login(token);
            // 获取通过后的参数
            String redisToken = (String) session.getAttribute(userVo.getTelephone());
            UserDto userDto = (UserDto) session.getAttribute(redisToken);
            session.setAttribute(redisToken, userDto);
            returnMap.put("userInfo", userDto);
            returnMap.put("token", redisToken);

        } else if (!StringUtils.isBlank(userVo.getAccountNum()) && !StringUtils.isBlank(userVo.getPassword())) {
            token = new UsernamePasswordToken(userVo.getAccountNum(), userVo.getPassword());
            session.setAttribute(userVo.getAccountNum(), userVo.getPassword());
            subject.login(token);
            String redisToken = (String) session.getAttribute(userVo.getAccountNum());
            UserDto userDto = (UserDto) session.getAttribute(redisToken);
            session.setAttribute(redisToken, userDto);
            returnMap.put("userInfo", userDto);
            returnMap.put("token", redisToken);
        } else if (!StringUtils.isBlank(userVo.getTelephone()) && !StringUtils.isBlank(userVo.getPassword())) {
            token = new UsernamePasswordToken(userVo.getTelephone(), userVo.getPassword());
            session.setAttribute(userVo.getTelephone(), userVo.getPassword());
            subject.login(token);
            String redisToken = (String) session.getAttribute(userVo.getTelephone());
            UserDto userDto = (UserDto) session.getAttribute(redisToken);
            session.setAttribute(redisToken, userDto);
            returnMap.put("userInfo", userDto);
            returnMap.put("token", redisToken);
        } else {
            return CommonResult.error(500, "请选择一种登录方式");
        }
        //登录完成后判断权限是否对于登录的端
        UserDto userDto = (UserDto) returnMap.get("userInfo");
        Boolean permission = userDto.getUserRoleDto().getPermission();
        if (!permission){
            //若权限不匹配则退出登录后返回500
            session.removeAttribute(userDto.getTelephone());
            session.removeAttribute(userDto.getAccountNumber());
            session.removeAttribute(returnMap.get("token"));
            SecurityUtils.getSubject().logout();
            return loginType != null && loginType == 1 ? CommonResult.error(500,"管理员账号请从管理端入口登录") : CommonResult.error(500,"非管理员账号无法登录");
        }
        if (userDto.getUserRoleDto().getRoleCode().equals(AuthRole.TEACHER)){
            //若权限不匹配则退出登录后返回500
            session.removeAttribute(userDto.getTelephone());
            session.removeAttribute(userDto.getAccountNumber());
            session.removeAttribute(returnMap.get("token"));
            SecurityUtils.getSubject().logout();
            return CommonResult.error(500,"该用户未分配权限，请联系管理员");
        }
        return CommonResult.success("登录成功", returnMap);
    }

    /**
     * 根据token 获取用户信息
     *
     * @param request 请求
     * @return 通用返回类
     */
    @Override
    public CommonResult<Object> getUserInfoByToken(HttpServletRequest request) {
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //判断权限设置跳转
        if (userDto.getUserRoleDto().getRoleCode().equals(AuthRole.ADMIN) ||
                userDto.getUserRoleDto().getRoleCode().equals(AuthRole.SCHOOL_ADMIN)||
                userDto.getUserRoleDto().getRoleCode().equals(AuthRole.DEPARTMENT_ADMIN)) {
            userDto.setJumpType(0);
        }else {
            userDto.setJumpType(2);
        }

        if (userDto.getUserType().equals(UserDto.STUDENT)){
            if (userDto.getSchoolInfo() != null){
                SchoolInfo schoolInfo = schoolInfoService.getById(userDto.getStudentInfo().getSchoolId());
                userDto.setSchoolInfo(schoolInfo);
            }

            if (userDto.getInternshipPlanInfo() != null){
                StudentInfo studentInfo = userDto.getStudentInfo();
                Date now = new Date();
                /*学生获取当前实习信息*/
                LambdaQueryWrapper<InternshipPlanInfo> planInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
                planInfoLambdaQueryWrapper.eq(InternshipPlanInfo::getDepartmentId,studentInfo.getDepartmentId())
                        .eq(InternshipPlanInfo::getMajorId,studentInfo.getMajorId())
                        .eq(InternshipPlanInfo::getGradeId,studentInfo.getGradeId())
                        .le(InternshipPlanInfo::getStartTime,now).eq(InternshipPlanInfo::getEndTime,now);
                InternshipPlanInfo planInfo = planInfoService.getOne(planInfoLambdaQueryWrapper);
                userDto.setInternshipPlanInfo(planInfo);

                if (planInfo != null){
                    //查找当前学生已通过的实习信息
                    LambdaQueryWrapper<InternshipInfoByStudent> internshipInfoByStudentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    internshipInfoByStudentLambdaQueryWrapper.eq(InternshipInfoByStudent::getRelationStudentId,userDto.getStudentInfo().getId())
                            .eq(InternshipInfoByStudent::getRelationPlanId,planInfo.getId())
                            .in(InternshipInfoByStudent::getInternshipType,1,2)
                            .eq(InternshipInfoByStudent::getStatus,3)
                            .eq(InternshipInfoByStudent::getDeleted,BaseVo.UNDELETE);
                    List<InternshipInfoByStudent> list = infoByStudentService.list(internshipInfoByStudentLambdaQueryWrapper);
                    userDto.setInternshipInfo(list.get(0));
                }
            }


        }

        if (userDto.getUserType().equals(UserDto.TEACHER)){
            if (userDto.getSchoolInfo() != null){
                SchoolInfo schoolInfo = schoolInfoService.getById(userDto.getTeacherInfo().getSchoolId());
                userDto.setSchoolInfo(schoolInfo);
            }
        }

        return CommonResult.success("查询成功" + MyStringUtils.getRequestToken(request), userDto);
    }

    /**
     * @Decription: 获取用户信息
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/10 14:58
     */
    @Override
    public UserDto getUserInfoByPhoneOrUserAccount(String phoneOrAccount,Object loginType) {
        UserDto userInfoByPhoneOrUserAccount = this.userMapper.getUserInfoByPhoneOrUserAccount(phoneOrAccount);
        // 获取role
        if (userInfoByPhoneOrUserAccount != null) {
            // 获取类型并且进行查询对应角色信息
            AuthRole role = this.getRole(userInfoByPhoneOrUserAccount.getId(),loginType);
            userInfoByPhoneOrUserAccount.setUserRoleDto(role);
            //查询教师或者学生
            if (userInfoByPhoneOrUserAccount.getUserType().equals(UserVo.TEACHER)){
                LambdaQueryWrapper<TeacherInfo> teacherInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
                teacherInfoLambdaQueryWrapper.eq(TeacherInfo::getUserId,userInfoByPhoneOrUserAccount.getId());
                userInfoByPhoneOrUserAccount.setTeacherInfo(teacherInfoMapper.selectOne(teacherInfoLambdaQueryWrapper));
                /*管理员的相关数据*/
                //校级管理员
                if (role.getRoleCode().equals(AuthRole.SCHOOL_ADMIN)){
                    LambdaQueryWrapper<SchoolTeacherRelation> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(SchoolTeacherRelation::getRelationType,1).eq(SchoolTeacherRelation::getDeleted,BaseVo.UNDELETE)
                            .eq(SchoolTeacherRelation::getTeacherId,userInfoByPhoneOrUserAccount.getTeacherInfo().getId());
                    List<SchoolTeacherRelation> list = schoolTeacherRelationService.list(queryWrapper);
                    Collection<Integer> schoolIds = new HashSet<>();
                    list.forEach(info -> {schoolIds.add(info.getSchoolId());});
                    userInfoByPhoneOrUserAccount.setSchoolIds(new ArrayList<>(schoolIds));
                }
                //二级管理员
                if (role.getRoleCode().equals(AuthRole.DEPARTMENT_ADMIN)){
                    LambdaQueryWrapper<DepartmentTeacherRelation> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(DepartmentTeacherRelation::getRelationType,1).eq(DepartmentTeacherRelation::getDeleted,BaseVo.UNDELETE)
                            .eq(DepartmentTeacherRelation::getTeacherId,userInfoByPhoneOrUserAccount.getTeacherInfo().getId());
                    List<DepartmentTeacherRelation> list = departmentTeacherRelationService.list(queryWrapper);
                    Collection<Integer> departmentIds = new HashSet<>();
                    list.forEach(info -> {departmentIds.add(info.getDepartmentId());});
                    userInfoByPhoneOrUserAccount.setDepartmentIds(new ArrayList<>(departmentIds));
                }
                //辅导员
                if (role.getRoleCode().equals(AuthRole.INSTRUCTOR)){
                    LambdaQueryWrapper<ClassTeacherRelation> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(ClassTeacherRelation::getRelationType,1).eq(ClassTeacherRelation::getDeleted,BaseVo.UNDELETE)
                            .eq(ClassTeacherRelation::getTeacherId,userInfoByPhoneOrUserAccount.getTeacherInfo().getId());
                    List<ClassTeacherRelation> list = classTeacherRelationService.list(queryWrapper);
                    Collection<Integer> classIds = new HashSet<>();
                    list.forEach(info -> {classIds.add(info.getClassId());});
                    userInfoByPhoneOrUserAccount.setClassIds(new ArrayList<>(classIds));
                }
            }
            if (userInfoByPhoneOrUserAccount.getUserType().equals(UserVo.STUDENT)){
                userInfoByPhoneOrUserAccount.setStudentInfo(studentInfoMapper.selectStudentByUserId(userInfoByPhoneOrUserAccount.getId()));
            }

            return userInfoByPhoneOrUserAccount;
        }

        return null;
    }

    /**
     * 手机号用户注册
     *
     * @param phoneOrAccount 用户账号
     * @return 返回用户的注册信息
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class,readOnly = true)
    public UserDto registerUser(String phoneOrAccount) {
        try {
            //查询账号是否存在
            LambdaQueryWrapper<UserAccount> userAccountLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userAccountLambdaQueryWrapper.eq(UserAccount::getTelephone,phoneOrAccount).eq(UserAccount::getDeleted,BaseVo.UNDELETE);
            int count = userAccountService.count(userAccountLambdaQueryWrapper);
            if (count > 0){
                return null;
            }

            //创建用户信息
            User user = new User();
            user.setUserType(UserDto.VISITOR);
            user.setUserName("游客"+UUID.randomUUID().toString().substring(0,6));
            //存入数据库
            boolean saveUser = super.baseMapper.insert(user) > 0;
            if (!saveUser){
                throw new RuntimeException("保存user出错");
            }
            //创建用户账号信息
            UserAccount account = new UserAccount();
            account.setTelephone(phoneOrAccount);
            account.setUserId(user.getId());
            //存入数据库
            boolean saveAccount = userAccountService.getBaseMapper().insert(account) > 0;
            if (!saveAccount){
                throw new RuntimeException("保存account出错");
            }
            AuthUserRole role = new AuthUserRole();
            role.setRoleId(8);
            role.setUserId(user.getId());
            boolean saveRole = authUserRoleService.getBaseMapper().insert(role) > 0;
            if (!saveRole){
                throw new RuntimeException("保存account出错");
            }
            return this.getUserInfoByPhoneOrUserAccount(phoneOrAccount, null);
        }catch (RuntimeException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * @Decription: 查询是否是管理员(获取schoolid和departmentid)
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/12 18:28
     */
    public Map<String, Object> getIdsByTeacherId(Integer teacherId) {
        QueryWrapper<SchoolTeacherRelation> schoolTeacherRelationQueryWrapper = new QueryWrapper<>();
        schoolTeacherRelationQueryWrapper.eq("teacher_id", teacherId).eq("deleted", BaseVo.UNDELETE);
        QueryWrapper<DepartmentTeacherRelation> departmentTeacherRelationQueryWrapper = new QueryWrapper<>();
        departmentTeacherRelationQueryWrapper.eq("teacher_id", teacherId).eq("deleted", BaseVo.UNDELETE);
        // 查询
        List<SchoolTeacherRelation> list = this.schoolTeacherRelationService.list(schoolTeacherRelationQueryWrapper);
        List<DepartmentTeacherRelation> departmentTeacherRelationList = this.departmentTeacherRelationService.list(departmentTeacherRelationQueryWrapper);
        Map<String, Object> returnMap = new HashMap<>(16);

        List<Integer> schoolIds = new ArrayList<>();
        List<Integer> departmentIds = new ArrayList<>();
        for (SchoolTeacherRelation schoolTeacherRelation : list) {
            schoolIds.add(schoolTeacherRelation.getSchoolId());
        }
        for (DepartmentTeacherRelation departmentTeacherRelation : departmentTeacherRelationList) {
            departmentIds.add(departmentTeacherRelation.getDepartmentId());
        }
        returnMap.put("schoolIds", schoolIds);
        returnMap.put("departmentIds", departmentIds);
        return returnMap;
    }

    /**
     * @Decription: 根据userId查询用户的role
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/12 11:28
     */
    public AuthRole getRole(Integer userId,Object loginType) {
        // 逻辑删除：0=未删除，1=删除
        Integer delete = 0;
        // 根据userid获取参数
        if (userId == null) {
            return null;
        }
        List<AuthUserRole> foreachList = new ArrayList<>();

        QueryWrapper<AuthUserRole> authUserRoleQueryWrapper = new QueryWrapper<>();
        authUserRoleQueryWrapper.eq("user_id", userId).eq("deleted", delete);
        List<AuthUserRole> list = authUserRoleService.list(authUserRoleQueryWrapper);

        //用户登录权限是否正确，若权限正确返回true 不正确时返回false
        boolean permission = true;

        if (loginType != null){
            int type = Integer.parseInt(loginType.toString());
            if (type == 1){
                authUserRoleQueryWrapper.in("role_id",4,5,6,7,8);
            }
            if (type == 2){
                authUserRoleQueryWrapper.in("role_id",1,2,3);
            }
            List<AuthUserRole> authUserRoles = authUserRoleService.list(authUserRoleQueryWrapper);
            if (authUserRoles != null && authUserRoles.size() > 0){
                foreachList.addAll(authUserRoles);
            }else {
                permission = false;
            }
        }
        //如果为空则按原逻辑
        if (foreachList.size() == 0){
            foreachList.addAll(list);
        }

        // 取最大的角色信息
        List<Integer> roleIds = new ArrayList<>();
        for (AuthUserRole authUserRole : foreachList) {
            roleIds.add(authUserRole.getRoleId());
        }
        if (roleIds.size()>0){
            // 查询所有的角色信息
            QueryWrapper<AuthRole> authRoleQueryWrapper = new QueryWrapper<>();
            authRoleQueryWrapper.in("id", roleIds).orderByAsc("parent_id");
            List<AuthRole> authRoles = authRoleService.list(authRoleQueryWrapper);

            if (authRoles == null || authRoles.isEmpty()) {
                return null;
            }
            AuthRole returnRole = null;
            if (authRoles.size()>1){
                for (AuthRole role:authRoles) {
                    //赋值
                    if (returnRole == null){
                        returnRole = role;
                    }
                    //判断权限取最小
                    if (role.getId()<returnRole.getId()){
                        returnRole = role;
                    }
                }
            }else {
                returnRole = authRoles.get(0);
            }
            //放入对象中用于后续判断
            returnRole.setPermission(permission);
            return returnRole;
        }else {
            return null;
        }

    }

    /**
     * @Decription: 修改用户信息
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/11 14:46
     */
    @Override
    public CommonResult<Object> updateUserInfo(UpdateUserVo updateUserVo) {
        if (updateUserVo == null) {
            return CommonResult.error(500, "请输入正确的用户信息");
        }
        Session session = SecurityUtils.getSubject().getSession();
        // 判断用户是否存在
        User user = super.getById(updateUserVo.getId());
        if (user == null) {
            return CommonResult.error(500, "用户不存在");
        }
        if (user.getStatus().equals(User.UNNORMAL) || user.getDeleted().equals(User.DELETED)) {
            return CommonResult.error(500, "用户已被注销");
        }
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", user.getId());
        // 查询
        UserAccount userAccount = userAccountService.getOne(userAccountQueryWrapper);

        // 判断是否是密码的修改
        if (updateUserVo.getNewPassword() != null) {
            if (updateUserVo.getNewPassword().trim().length() < UpdateUserVo.PASSWORD_LENGTH) {
                return CommonResult.error(500, "新密码长度不能小于6位");
            }
            return this.updateUserPassword(user, updateUserVo);
        }
        boolean modify = false;
        // 判断是否有用户的个人信息
        if (!StringUtils.isBlank(updateUserVo.getAddressCode()) && !StringUtils.isBlank(updateUserVo.getAddress())) {
            // 修改用户地址
            userAccount.setAddressCode(Integer.valueOf(updateUserVo.getAddressCode()));
            userAccount.setAddress(updateUserVo.getAddress());
            modify = true;
        }
        //生日
        if (!StringUtils.isBlank(updateUserVo.getBirth())) {
            userAccount.setBirth(updateUserVo.getBirth());
            modify = true;
        }
        //性别
        if (!StringUtils.isBlank(updateUserVo.getGender())) {
            userAccount.setGender(updateUserVo.getGender());
            modify = true;
        }
        //邮箱
        if (!StringUtils.isBlank(updateUserVo.getEmail())) {
            userAccount.setEmail(updateUserVo.getEmail());
            modify = true;
        }
        //个性签名
        if (!StringUtils.isBlank(updateUserVo.getSign())) {
            userAccount.setSign(updateUserVo.getSign());
            modify = true;
        }
        // 判断是否有用户头像的修改
        if (updateUserVo.getUserAvatar() != null) {
            // 修改用户的头像
            user.setUserAvatar(updateUserVo.getUserAvatar());
        }
        // 判断是否有用户名称的修改
        if (updateUserVo.getUserName() != null) {
            user.setUserName(updateUserVo.getUserName());
        }
        if (modify){
            boolean updateById = userAccountService.updateById(userAccount);
            // 重新获取一遍userInfo
            UserDto userDto = this.getUserInfoByPhoneOrUserAccount(userAccount.getAccountNumber(),null);
            session.setAttribute(session.getId(), userDto);
            return updateById ? CommonResult.success("修改成功", null) : CommonResult.error(500, "修改失败");
        }

        if (userAccount == null) {
            return CommonResult.error(500, "用户账号异常");
        }

        // 判断是否有手机号的修改
        if (updateUserVo.getTelephone() != null) {
            // 获取验证码
            if (updateUserVo.getCode() == null) {
                return CommonResult.error(500, "请输入正确的验证码");
            }
            // 获取并且判断验证码是否正确
            CommonResult<Object> objectCommonResult = this.confimCode(updateUserVo);
            if (!objectCommonResult.isSuccess()) {
                return objectCommonResult;
            }
            // 比对成功修改手机号并且删除缓存
            SecurityUtils.getSubject().getSession().removeAttribute(SmsConfig.REDIS_CODE + updateUserVo.getOldTelephone());
            // 修改手机号
            userAccount.setTelephone(updateUserVo.getTelephone());
            // 判断是否已经有该手机号的账户
            QueryWrapper<UserAccount> queryWrapper = new QueryWrapper();
            queryWrapper
                    .eq("telephone", updateUserVo.getTelephone())
                    .eq("deleted", 0);
            UserAccount account = this.userAccountService.getOne(queryWrapper);
            if (account != null && account.getId() != null) {
                return CommonResult.error(500, "该手机号已经绑定其他用户，无法再次绑定");
            }
            boolean updateById = userAccountService.updateById(userAccount);
            // 修改成功后让其重新登录
            if (updateById) {
                session.removeAttribute(session.getId());
                SecurityUtils.getSubject().logout();
                return CommonResult.success("修改成功", null);
            }
            return CommonResult.error(500, "修改失败");
        }
        return CommonResult.error(500, "您没有修改的参数");
    }

    /**
     * @param operator
     * @param vo
     * @Description: 重置密码
     * @Author: LZH
     * @Date: 2022/2/22 10:41
     */
    @Override
    public CommonResult<Object> resetPassword(UserDto operator, UserVo vo) {

        //获取要修改的用户信息
        User user = this.getById(vo.getUserId());

        QueryWrapper<UserAccount> accountQueryWrapper = new QueryWrapper<>();
        accountQueryWrapper.eq("user_id",user.getId()).eq("deleted",0);
        UserAccount one = userAccountService.getOne(accountQueryWrapper);
        if (one == null){
            return CommonResult.error(500,"获取账号信息失败");
        }
        //判断如果是修改老师的密码，只有管理员才有权限
        if (user.getUserType().equals(UserVo.TEACHER)) {
            if (!operator.getUserType().equals(UserVo.ADMIN)){
                return CommonResult.error(500,"教职工只有超级管理员才能重置");
            }
        }

        //修改为默认密码
        user.setPassword(PasswordCryptoTool.getDefaultPassword(one.getAccountNumber()));
        user.setUpdateBy(operator.getId());

        //更新数据库返回
        return this.updateById(user) ? CommonResult.success("重置成功",null) : CommonResult.error(500,"重置失败，请联系管理员");
    }



    /**
     * 用户选择切换身份
     *
     * @param userVo 前端参数
     * @return 通用返回
     */
    @Override
    public CommonResult<Object> chooseRole(HttpServletRequest request, UserVo userVo) {

        // 获取token-旧数据
        String tokens = MyStringUtils.getRequestToken(request);
        Session session = SecurityUtils.getSubject().getSession();
        UserDto userDto = (UserDto) session.getAttribute(tokens);

        //判断是否有传权限id
        LambdaQueryWrapper<AuthUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuthUserRole::getUserId,userDto.getId()).eq(AuthUserRole::getRoleId,userVo.getRoleId())
                .eq(AuthUserRole::getDeleted,BaseVo.UNDELETE);
        AuthUserRole one = authUserRoleService.getOne(queryWrapper);
        if (one == null) {
            return CommonResult.error(500, "未找到该权限");
        }
        AuthRole roleServiceById = authRoleService.getById(userVo.getRoleId());

        userDto.setUserRoleDto(roleServiceById);

        SecurityUtils.getSubject().runAs(new SimplePrincipalCollection(userDto.getUserRoleDto().getId(),userDto.getUserRoleDto().getRoleCode()));

        //放入关联学校和系部
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        if (roleCode.equals(AuthRole.SCHOOL_ADMIN)){
            List<Integer> schoolIds = new ArrayList<>();
            if (userVo.getSchoolId() == null){
                return CommonResult.error(500,"缺少学校id");
            }
            schoolIds.add(userVo.getSchoolId());
            userDto.setSchoolIds(schoolIds);
        }else if (roleCode.equals(AuthRole.DEPARTMENT_ADMIN)){
            List<Integer> departments = new ArrayList<>();
            if (userVo.getDepartmentId() == null){
                return CommonResult.error(500,"缺少二级学院id");
            }
            departments.add(userVo.getDepartmentId());
            userDto.setDepartmentIds(departments);
        }

        // 放入缓存
        session.setAttribute(tokens, userDto);

        Map<String, Object> returnMap = new HashMap<>(16);

        session.setAttribute(tokens, userDto);
        returnMap.put("userInfo", userDto);

        return CommonResult.success("切换成功", returnMap);

    }

    /**
     * @param userDto
     * @param vo
     * @Description: 用户获取权限列表
     * @Author: LZH
     * @Date: 2022/3/3 14:02
     */
    @Override
    public CommonResult<Object> getChooseRoleList(UserDto userDto, UserVo vo) {

        List<UserRoleDto> userRoleDtoList = new ArrayList<>();
        Map<String, Object> returnMap = new HashMap<>(16);

        LambdaQueryWrapper<AuthUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuthUserRole::getUserId,userDto.getId()).eq(AuthUserRole::getDeleted,BaseVo.UNDELETE);
        queryWrapper.orderByDesc(AuthUserRole::getId);
        List<AuthUserRole> list = authUserRoleService.list(queryWrapper);
        //判断是否有权限
        if (list.size()>0){
            //遍历权限列表
            for (AuthUserRole info:list) {
                //查询权限
                AuthRole authRole = authRoleService.getById(info.getRoleId());
                String roleCode = authRole.getRoleCode();
                if (roleCode.equals(AuthRole.SCHOOL_ADMIN)){
                    //校管理、督导
                }else if (roleCode.equals(AuthRole.DEPARTMENT_ADMIN)){
                    //系部管理，督导
                }else if (roleCode.equals(AuthRole.TEACHER)){
                    //教师权限
                }
            }
        }

        returnMap.put("list",userRoleDtoList);

        return CommonResult.success(returnMap);
    }

    /**
     * @Decription: 用户密码的修改
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/11 15:07
     */
    public CommonResult<Object> updateUserPassword(User user, UpdateUserVo updateUserVo) {
        Session session = SecurityUtils.getSubject().getSession();
        //  判断是否输入了原密码或者code
        if (updateUserVo.getOldPassword() != null) {
            // 判断是否一致，一致就去修改
            if (!PasswordCryptoTool.checkPassword(updateUserVo.getOldPassword(), user.getPassword())) {
                return CommonResult.error(500, "原密码错误");
            }
            if (updateUserVo.getNewPassword().length()<PasswordCryptoTool.DEFAULT_PASSWORD_LENGTH){
                return CommonResult.error(500,"密码长度必须大于6位");
            }
            // 修改密码
            String newPassword = PasswordCryptoTool.encryptPassword(updateUserVo.getNewPassword());

            user.setPassword(newPassword);
            boolean updateById = super.updateById(user);
            if (updateById) {
                session.removeAttribute(session.getId());
                SecurityUtils.getSubject().logout();
                return CommonResult.success("修改成功", null);
            }
            return CommonResult.error(500, "修改失败");
        } else if (updateUserVo.getCode() != null) {
            // 获取并且比对验证码
            CommonResult<Object> objectCommonResult = this.confimOldTelephoneCode(updateUserVo);
            if (!objectCommonResult.isSuccess()) {
                return objectCommonResult;
            }
            // 修改密码
            String newPassword = PasswordCryptoTool.encryptPassword(updateUserVo.getNewPassword());
            user.setPassword(newPassword);
            // 删除缓存
            SecurityUtils.getSubject().getSession().removeAttribute(SmsConfig.REDIS_CODE + updateUserVo.getOldTelephone());
            boolean updateById = super.updateById(user);
            if (updateById) {
                session.removeAttribute(session.getId());
                SecurityUtils.getSubject().logout();
                return CommonResult.success("修改成功", null);
            }
            return CommonResult.error(500, "修改失败");
        } else {
            return CommonResult.error(500, "请选择一种修改方式");
        }
    }

    /**
     * @Decription: 验证码的获取并且比对
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/11 15:25
     */
    public CommonResult<Object> confimCode(UpdateUserVo updateUserVo) {
        // 获取验证码
        Map<String, Object> map = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(SmsConfig.REDIS_CODE + updateUserVo.getTelephone());
        // 判断验证码是否过期
        if (map == null) {
            return CommonResult.error(500, "验证码不存在");
        }
        Long time = (Long) map.get("time");
        String code = (String) map.get("code");
        if (RedisException.EXPIRETIME < (System.currentTimeMillis() - time)) {
            return CommonResult.error(500, "验证码过期");
        }
        if (!updateUserVo.getCode().equals(code)) {
            return CommonResult.error(500, "验证码错误");
        }
        return CommonResult.success(SUCCESS, null);
    }

    /**
     * @Decription: 验证码的获取并且比对
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/11 15:25
     */
    public CommonResult<Object> confimOldTelephoneCode(UpdateUserVo updateUserVo) {
        // 获取验证码
        Map<String, Object> map = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(SmsConfig.REDIS_CODE + updateUserVo.getOldTelephone());
        // 判断验证码是否过期
        if (map == null) {
            return CommonResult.error(500, "验证码不存在");
        }
        Long time = (Long) map.get("time");
        String code = (String) map.get("code");
        if (RedisException.EXPIRETIME < (System.currentTimeMillis() - time)) {
            return CommonResult.error(500, "验证码过期");
        }
        if (!updateUserVo.getCode().equals(code)) {
            return CommonResult.error(500, "验证码错误");
        }
        return CommonResult.success(SUCCESS, null);
    }

    /**
     * @Decription:调用sdk获取短信的验证码
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2021/12/13 11:29
     */
    public String getMessageForCode(String phone, String code) {
        //自己账号的AccessKey信息
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", SmsConfig.ACCESS_KEY_ID, SmsConfig.ACCESS_KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        //短信服务的服务接入地址
        request.setSysDomain("dysmsapi.aliyuncs.com");
        //API的版本号
        request.setSysVersion("2017-05-25");
        //API的名称
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        //接收短信的手机号码
        request.putQueryParameter("PhoneNumbers", phone);
        //短信签名名称
        request.putQueryParameter("SignName", SmsConfig.SIGN_NAME);
        //短信模板ID
        request.putQueryParameter("TemplateCode", SmsConfig.TEMPLATE_CODE);
        Map<String, Object> params = new HashMap<>(16);
        params.put("code", code);
        //短信模板变量对应的实际值
        request.putQueryParameter("TemplateParam", JSON.toJSONString(params));
        try {
            CommonResponse response = client.getCommonResponse(request);
            Map<String, Object> reponseMap = (Map<String, Object>) JSON.parse(response.getData());
            return reponseMap.get("Message").toString();
        } catch (ServerException e) {
            e.printStackTrace();
            return "ERROR";
        } catch (ClientException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}
