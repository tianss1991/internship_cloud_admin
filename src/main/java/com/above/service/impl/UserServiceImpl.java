package com.above.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.above.config.sms.SmsConfig;
import com.above.constans.exception.RedisException;
import com.above.dao.InternshipPlanInfoMapper;
import com.above.dao.StudentInfoMapper;
import com.above.dao.TeacherInfoMapper;
import com.above.dao.UserMapper;
import com.above.dto.SimplePlanInfoDto;
import com.above.dto.UserDto;
import com.above.dto.UserRoleDto;
import com.above.po.*;
import com.above.service.*;
import com.above.utils.CommonResult;
import com.above.utils.MyStringUtils;
import com.above.utils.PasswordCryptoTool;
import com.above.utils.RedisUtils;
import com.above.vo.BaseVo;
import com.above.vo.InternshipPlanInfoVo;
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
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * <p>
 * ??????????????????????????????????????????????????? ???????????????
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
    @Autowired
    private InternshipPlanInfoMapper internshipPlanInfoMapper;

    /**
     * @Decription: ???????????????
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/10 14:02
     */
    @Override
    public CommonResult<Object> getCode(UserVo userVo) {

        // ??????????????????????????????????????????????????????
        if (userVo == null || userVo.getTelephone() == null || !MyStringUtils.isTelephone(userVo.getTelephone())) {
            return CommonResult.error(500, "???????????????????????????");
        }
        // ??????????????????????????????redis
        String phoneCode = MyStringUtils.getPhoneCode(4);
//         ?????????????????????sdk???????????????
        String messageForCode = this.getMessageForCode(userVo.getTelephone(), phoneCode);
//         ????????????????????????
        if (messageForCode.equalsIgnoreCase(SmsConfig.ERROR_STATUS)) {
            return CommonResult.error(500, "??????????????????");
        }
        // TODO ?????????????????? ??????
//        String phoneCode = "9999";
        // ????????????
        Map<String, Object> map = new HashMap<>(16);
        map.put("code", phoneCode);
        map.put("time", System.currentTimeMillis());
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute(SmsConfig.REDIS_CODE + userVo.getTelephone(), JSON.toJSONString(map));

        RedisUtils.set(SmsConfig.REDIS_CODE + userVo.getTelephone(),phoneCode,300);

        log.info(session.getAttribute(SmsConfig.REDIS_CODE + userVo.getTelephone()).toString());
        log.info("sessionid"+session.getId());
        // ????????????
        return CommonResult.success("????????????", session.getId());
    }

    @Override
    public CommonResult<Object> login(UserVo userVo) {
        // ??????????????????
        if (userVo == null) {
            return CommonResult.error(500, "???????????????????????????");
        }
        // ??????shiro??????
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = null;
        Session session = subject.getSession();
        Map<String, Object> returnMap = new HashMap<>(16);
        //???????????????
        Integer loginType = userVo.getLoginType();
        session.setAttribute("loginType", loginType);
        // ???????????????????????????
        if (!StringUtils.isBlank(userVo.getTelephone()) && !StringUtils.isBlank(userVo.getCode())) {
            token = new UsernamePasswordToken(userVo.getTelephone(),userVo.getCode());
            // ???????????????????????????
            session.setAttribute(userVo.getTelephone(),userVo.getCode());
            // ??????????????????
            session.setAttribute("flag",true);
            subject.login(token);
            // ????????????????????????
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
            return CommonResult.error(500, "???????????????????????????");
        }
        //???????????????????????????????????????????????????
        UserDto userDto = (UserDto) returnMap.get("userInfo");
        Boolean permission = userDto.getUserRoleDto().getPermission();
        if (!permission){
            //??????????????????????????????????????????500
            session.removeAttribute(userDto.getTelephone());
            session.removeAttribute(userDto.getAccountNumber());
            session.removeAttribute(returnMap.get("token"));
            SecurityUtils.getSubject().logout();
            return loginType != null && loginType == 1 ? CommonResult.error(500,"??????????????????????????????????????????") : CommonResult.error(500,"??????????????????????????????");
        }
        if (userDto.getUserRoleDto().getRoleCode().equals(AuthRole.TEACHER)){
            //??????????????????????????????????????????500
            session.removeAttribute(userDto.getTelephone());
            session.removeAttribute(userDto.getAccountNumber());
            session.removeAttribute(returnMap.get("token"));
            SecurityUtils.getSubject().logout();
            return CommonResult.error(500,"?????????????????????????????????????????????");
        }
        return CommonResult.success("????????????", returnMap);
    }

    /**
     *@author: GG
     *@data: 2022/7/20 11:36
     *@function:?????????????????????????????????token???
     */
    @Override
    public CommonResult<Object> getInternshipPlanInfoByTeacher(HttpServletRequest request, InternshipPlanInfoVo vo) {
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        Integer planId = vo.getPlanId();
        InternshipPlanInfo internshipPlanInfo = internshipPlanInfoMapper.selectById(planId);
        SimplePlanInfoDto simplePlanInfoDto = new SimplePlanInfoDto();
        simplePlanInfoDto.setStartTime(internshipPlanInfo.getStartTime());
        simplePlanInfoDto.setEndTime(internshipPlanInfo.getEndTime());
        simplePlanInfoDto.setPlanTitle(internshipPlanInfo.getPlanTitle());
        simplePlanInfoDto.setId(internshipPlanInfo.getId());
        simplePlanInfoDto.setMajorId(internshipPlanInfo.getMajorId());
        simplePlanInfoDto.setGradeId(internshipPlanInfo.getGradeId());

        userDto.setInternshipPlanInfo(simplePlanInfoDto);
        SecurityUtils.getSubject().getSession().setAttribute(MyStringUtils.getRequestToken(request),userDto);
        return CommonResult.success("????????????" + MyStringUtils.getRequestToken(request), userDto);
    }

    /**
     * ??????token ??????????????????
     *
     * @param request ??????
     * @return ???????????????
     */
    @Override
    public CommonResult<Object> getUserInfoByToken(HttpServletRequest request) {
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getSession().getAttribute(MyStringUtils.getRequestToken(request));
        //????????????????????????
        if (userDto.getUserRoleDto().getRoleCode().equals(AuthRole.ADMIN) ||
                userDto.getUserRoleDto().getRoleCode().equals(AuthRole.SCHOOL_ADMIN)||
                userDto.getUserRoleDto().getRoleCode().equals(AuthRole.DEPARTMENT_ADMIN)) {
            userDto.setJumpType(0);
        }else {
            userDto.setJumpType(2);
        }

        if (userDto.getUserType().equals(UserDto.STUDENT)){
            if (userDto.getSchoolInfo() == null){
                SchoolInfo schoolInfo = schoolInfoService.getById(userDto.getStudentInfo().getSchoolId());
                userDto.setSchoolInfo(schoolInfo);
            }

            if (userDto.getInternshipPlanInfo() == null){
                StudentInfo studentInfo = userDto.getStudentInfo();
                Date now = new Date();
                /*??????????????????????????????*/
//                LambdaQueryWrapper<InternshipPlanInfo> planInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
//                planInfoLambdaQueryWrapper.eq(InternshipPlanInfo::getDepartmentId,studentInfo.getDepartmentId())
//                        .eq(InternshipPlanInfo::getMajorId,studentInfo.getMajorId())
//                        .eq(InternshipPlanInfo::getGradeId,studentInfo.getGradeId())
//                        .le(InternshipPlanInfo::getStartTime,now).ge(InternshipPlanInfo::getEndTime,now);
//                InternshipPlanInfo planInfo = planInfoService.getOne(planInfoLambdaQueryWrapper);

                BaseVo baseVo = new BaseVo();
                baseVo.setStudentId(studentInfo.getId());
                baseVo.setStartTime(now);

                SimplePlanInfoDto planInfoByStudent = internshipPlanInfoMapper.getPlanInfoByStudent(baseVo);
                userDto.setInternshipPlanInfo(planInfoByStudent);
            }
            if (userDto.getInternshipPlanInfo() != null){

                //??????????????????????????????????????????
                LambdaQueryWrapper<InternshipInfoByStudent> internshipInfoByStudentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                internshipInfoByStudentLambdaQueryWrapper.eq(InternshipInfoByStudent::getRelationStudentId,userDto.getStudentInfo().getId())
                        .eq(InternshipInfoByStudent::getRelationPlanId,userDto.getInternshipPlanInfo().getId())
                        .in(InternshipInfoByStudent::getInternshipType,1,2)
                        .eq(InternshipInfoByStudent::getStatus,3)
                        .eq(InternshipInfoByStudent::getDeleted,BaseVo.UNDELETE);
                List<InternshipInfoByStudent> list = infoByStudentService.list(internshipInfoByStudentLambdaQueryWrapper);
                if (list.size() > 0){
                    userDto.setInternshipInfo(list.get(0));
                }
            }
        }

        if (userDto.getUserType().equals(UserDto.TEACHER)){
            if (userDto.getSchoolInfo() == null){
                SchoolInfo schoolInfo = schoolInfoService.getById(userDto.getTeacherInfo().getSchoolId());
                userDto.setSchoolInfo(schoolInfo);
            }
        }

        return CommonResult.success("????????????" + MyStringUtils.getRequestToken(request), userDto);
    }

    /**
     * @Decription: ??????????????????
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/10 14:58
     */
    @Override
    public UserDto getUserInfoByPhoneOrUserAccount(String phoneOrAccount,Object loginType) {
        UserDto userInfoByPhoneOrUserAccount = this.userMapper.getUserInfoByPhoneOrUserAccount(phoneOrAccount);
        // ??????role
        if (userInfoByPhoneOrUserAccount != null) {
            // ????????????????????????????????????????????????
            AuthRole role = this.getRole(userInfoByPhoneOrUserAccount.getId(),loginType);
            userInfoByPhoneOrUserAccount.setUserRoleDto(role);
            //????????????????????????
            if (userInfoByPhoneOrUserAccount.getUserType().equals(UserVo.TEACHER)){
                LambdaQueryWrapper<TeacherInfo> teacherInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
                teacherInfoLambdaQueryWrapper.eq(TeacherInfo::getUserId,userInfoByPhoneOrUserAccount.getId());
                userInfoByPhoneOrUserAccount.setTeacherInfo(teacherInfoMapper.selectOne(teacherInfoLambdaQueryWrapper));
                /*????????????????????????*/
                //???????????????
                if (role.getRoleCode().equals(AuthRole.SCHOOL_ADMIN)){
                    LambdaQueryWrapper<SchoolTeacherRelation> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(SchoolTeacherRelation::getRelationType,1).eq(SchoolTeacherRelation::getDeleted,BaseVo.UNDELETE)
                            .eq(SchoolTeacherRelation::getTeacherId,userInfoByPhoneOrUserAccount.getTeacherInfo().getId());
                    List<SchoolTeacherRelation> list = schoolTeacherRelationService.list(queryWrapper);
                    Collection<Integer> schoolIds = new HashSet<>();
                    list.forEach(info -> {schoolIds.add(info.getSchoolId());});
                    userInfoByPhoneOrUserAccount.setSchoolIds(new ArrayList<>(schoolIds));
                }
                //???????????????
                if (role.getRoleCode().equals(AuthRole.DEPARTMENT_ADMIN)){
                    LambdaQueryWrapper<DepartmentTeacherRelation> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(DepartmentTeacherRelation::getRelationType,1).eq(DepartmentTeacherRelation::getDeleted,BaseVo.UNDELETE)
                            .eq(DepartmentTeacherRelation::getTeacherId,userInfoByPhoneOrUserAccount.getTeacherInfo().getId());
                    List<DepartmentTeacherRelation> list = departmentTeacherRelationService.list(queryWrapper);
                    Collection<Integer> departmentIds = new HashSet<>();
                    list.forEach(info -> {departmentIds.add(info.getDepartmentId());});
                    userInfoByPhoneOrUserAccount.setDepartmentIds(new ArrayList<>(departmentIds));
                }
                //?????????
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
     * ?????????????????????
     *
     * @param phoneOrAccount ????????????
     * @return ???????????????????????????
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class,readOnly = true)
    public UserDto registerUser(String phoneOrAccount) {
        try {
            //????????????????????????
            LambdaQueryWrapper<UserAccount> userAccountLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userAccountLambdaQueryWrapper.eq(UserAccount::getTelephone,phoneOrAccount).eq(UserAccount::getDeleted,BaseVo.UNDELETE);
            int count = userAccountService.count(userAccountLambdaQueryWrapper);
            if (count > 0){
                return null;
            }

            //??????????????????
            User user = new User();
            user.setUserType(UserDto.VISITOR);
            user.setUserName("??????"+UUID.randomUUID().toString().substring(0,6));
            //???????????????
            boolean saveUser = super.baseMapper.insert(user) > 0;
            if (!saveUser){
                throw new RuntimeException("??????user??????");
            }
            //????????????????????????
            UserAccount account = new UserAccount();
            account.setTelephone(phoneOrAccount);
            account.setUserId(user.getId());
            //???????????????
            boolean saveAccount = userAccountService.getBaseMapper().insert(account) > 0;
            if (!saveAccount){
                throw new RuntimeException("??????account??????");
            }
            AuthUserRole role = new AuthUserRole();
            role.setRoleId(8);
            role.setUserId(user.getId());
            boolean saveRole = authUserRoleService.getBaseMapper().insert(role) > 0;
            if (!saveRole){
                throw new RuntimeException("??????account??????");
            }
            return this.getUserInfoByPhoneOrUserAccount(phoneOrAccount, null);
        }catch (RuntimeException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * @Decription: ????????????????????????(??????schoolid???departmentid)
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
        // ??????
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
     * @Decription: ??????userId???????????????role
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/12 11:28
     */
    public AuthRole getRole(Integer userId,Object loginType) {
        // ???????????????0=????????????1=??????
        Integer delete = 0;
        // ??????userid????????????
        if (userId == null) {
            return null;
        }
        List<AuthUserRole> foreachList = new ArrayList<>();

        QueryWrapper<AuthUserRole> authUserRoleQueryWrapper = new QueryWrapper<>();
        authUserRoleQueryWrapper.eq("user_id", userId).eq("deleted", delete);
        List<AuthUserRole> list = authUserRoleService.list(authUserRoleQueryWrapper);

        //??????????????????????????????????????????????????????true ??????????????????false
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
        //???????????????????????????
        if (foreachList.size() == 0){
            foreachList.addAll(list);
        }

        // ????????????????????????
        List<Integer> roleIds = new ArrayList<>();
        for (AuthUserRole authUserRole : foreachList) {
            roleIds.add(authUserRole.getRoleId());
        }
        if (roleIds.size()>0){
            // ???????????????????????????
            QueryWrapper<AuthRole> authRoleQueryWrapper = new QueryWrapper<>();
            authRoleQueryWrapper.in("id", roleIds).orderByAsc("parent_id");
            List<AuthRole> authRoles = authRoleService.list(authRoleQueryWrapper);

            if (authRoles == null || authRoles.isEmpty()) {
                return null;
            }
            AuthRole returnRole = null;
            if (authRoles.size()>1){
                for (AuthRole role:authRoles) {
                    //??????
                    if (returnRole == null){
                        returnRole = role;
                    }
                    //?????????????????????
                    if (role.getId()<returnRole.getId()){
                        returnRole = role;
                    }
                }
            }else {
                returnRole = authRoles.get(0);
            }
            //?????????????????????????????????
            returnRole.setPermission(permission);
            return returnRole;
        }else {
            return null;
        }

    }

    /**
     * @Decription: ??????????????????
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/11 14:46
     */
    @Override
    public CommonResult<Object> updateUserInfo(UpdateUserVo updateUserVo) {
        if (updateUserVo == null) {
            return CommonResult.error(500, "??????????????????????????????");
        }
        Session session = SecurityUtils.getSubject().getSession();
        // ????????????????????????
        User user = super.getById(updateUserVo.getId());
        if (user == null) {
            return CommonResult.error(500, "???????????????");
        }
        if (user.getStatus().equals(User.UNNORMAL) || user.getDeleted().equals(User.DELETED)) {
            return CommonResult.error(500, "??????????????????");
        }
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", user.getId());
        // ??????
        UserAccount userAccount = userAccountService.getOne(userAccountQueryWrapper);

        // ??????????????????????????????
        if (updateUserVo.getNewPassword() != null) {
            if (updateUserVo.getNewPassword().trim().length() < UpdateUserVo.PASSWORD_LENGTH) {
                return CommonResult.error(500, "???????????????????????????6???");
            }
            return this.updateUserPassword(user, updateUserVo);
        }
        boolean modify = false;
        // ????????????????????????????????????
        if (!StringUtils.isBlank(updateUserVo.getAddressCode()) && !StringUtils.isBlank(updateUserVo.getAddress())) {
            // ??????????????????
            userAccount.setAddressCode(Integer.valueOf(updateUserVo.getAddressCode()));
            userAccount.setAddress(updateUserVo.getAddress());
            modify = true;
        }
        //??????
        if (!StringUtils.isBlank(updateUserVo.getBirth())) {
            userAccount.setBirth(updateUserVo.getBirth());
            modify = true;
        }
        //??????
        if (!StringUtils.isBlank(updateUserVo.getGender())) {
            userAccount.setGender(updateUserVo.getGender());
            modify = true;
        }
        //??????
        if (!StringUtils.isBlank(updateUserVo.getEmail())) {
            userAccount.setEmail(updateUserVo.getEmail());
            modify = true;
        }
        //????????????
        if (!StringUtils.isBlank(updateUserVo.getSign())) {
            userAccount.setSign(updateUserVo.getSign());
            modify = true;
        }
        // ????????????????????????????????????
        if (updateUserVo.getUserAvatar() != null) {
            // ?????????????????????
            user.setUserAvatar(updateUserVo.getUserAvatar());
        }
        // ????????????????????????????????????
        if (updateUserVo.getUserName() != null) {
            user.setUserName(updateUserVo.getUserName());
        }
        if (modify){
            boolean updateById = userAccountService.updateById(userAccount);
            // ??????????????????userInfo
            UserDto userDto = this.getUserInfoByPhoneOrUserAccount(userAccount.getAccountNumber(),null);
            session.setAttribute(session.getId(), userDto);
            return updateById ? CommonResult.success("????????????", null) : CommonResult.error(500, "????????????");
        }

        if (userAccount == null) {
            return CommonResult.error(500, "??????????????????");
        }

        // ?????????????????????????????????
        if (updateUserVo.getTelephone() != null) {
            // ???????????????
            if (updateUserVo.getCode() == null) {
                return CommonResult.error(500, "???????????????????????????");
            }
            // ???????????????????????????????????????
            CommonResult<Object> objectCommonResult = this.confimCode(updateUserVo);
            if (!objectCommonResult.isSuccess()) {
                return objectCommonResult;
            }
            // ?????????????????????????????????????????????
            SecurityUtils.getSubject().getSession().removeAttribute(SmsConfig.REDIS_CODE + updateUserVo.getOldTelephone());
            // ???????????????
            userAccount.setTelephone(updateUserVo.getTelephone());
            // ??????????????????????????????????????????
            QueryWrapper<UserAccount> queryWrapper = new QueryWrapper();
            queryWrapper
                    .eq("telephone", updateUserVo.getTelephone())
                    .eq("deleted", 0);
            UserAccount account = this.userAccountService.getOne(queryWrapper);
            if (account != null && account.getId() != null) {
                return CommonResult.error(500, "?????????????????????????????????????????????????????????");
            }
            boolean updateById = userAccountService.updateById(userAccount);
            // ?????????????????????????????????
            if (updateById) {
                session.removeAttribute(session.getId());
                SecurityUtils.getSubject().logout();
                return CommonResult.success("????????????", null);
            }
            return CommonResult.error(500, "????????????");
        }
        return CommonResult.error(500, "????????????????????????");
    }

    /**
     * @param operator
     * @param vo
     * @Description: ????????????
     * @Author: LZH
     * @Date: 2022/2/22 10:41
     */
    @Override
    public CommonResult<Object> resetPassword(UserDto operator, UserVo vo) {

        //??????????????????????????????
        User user = this.getById(vo.getUserId());

        QueryWrapper<UserAccount> accountQueryWrapper = new QueryWrapper<>();
        accountQueryWrapper.eq("user_id",user.getId()).eq("deleted",0);
        UserAccount one = userAccountService.getOne(accountQueryWrapper);
        if (one == null){
            return CommonResult.error(500,"????????????????????????");
        }
        //??????????????????????????????????????????????????????????????????
        if (user.getUserType().equals(UserVo.TEACHER)) {
            if (!operator.getUserType().equals(UserVo.ADMIN)){
                return CommonResult.error(500,"???????????????????????????????????????");
            }
        }

        //?????????????????????
        user.setPassword(PasswordCryptoTool.getDefaultPassword(one.getAccountNumber()));
        user.setUpdateBy(operator.getId());

        //?????????????????????
        return this.updateById(user) ? CommonResult.success("????????????",null) : CommonResult.error(500,"?????????????????????????????????");
    }



    /**
     * ????????????????????????
     *
     * @param userVo ????????????
     * @return ????????????
     */
    @Override
    public CommonResult<Object> chooseRole(HttpServletRequest request, UserVo userVo) {

        // ??????token-?????????
        String tokens = MyStringUtils.getRequestToken(request);
        Session session = SecurityUtils.getSubject().getSession();
        UserDto userDto = (UserDto) session.getAttribute(tokens);

        //????????????????????????id
        LambdaQueryWrapper<AuthUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuthUserRole::getUserId,userDto.getId()).eq(AuthUserRole::getRoleId,userVo.getRoleId())
                .eq(AuthUserRole::getDeleted,BaseVo.UNDELETE);
        AuthUserRole one = authUserRoleService.getOne(queryWrapper);
        if (one == null) {
            return CommonResult.error(500, "??????????????????");
        }
        AuthRole roleServiceById = authRoleService.getById(userVo.getRoleId());

        userDto.setUserRoleDto(roleServiceById);

        SecurityUtils.getSubject().runAs(new SimplePrincipalCollection(userDto.getUserRoleDto().getId(),userDto.getUserRoleDto().getRoleCode()));

        //???????????????????????????
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        if (roleCode.equals(AuthRole.SCHOOL_ADMIN)){
            List<Integer> schoolIds = new ArrayList<>();
            if (userVo.getSchoolId() == null){
                return CommonResult.error(500,"????????????id");
            }
            schoolIds.add(userVo.getSchoolId());
            userDto.setSchoolIds(schoolIds);
        }else if (roleCode.equals(AuthRole.DEPARTMENT_ADMIN)){
            List<Integer> departments = new ArrayList<>();
            if (userVo.getDepartmentId() == null){
                return CommonResult.error(500,"??????????????????id");
            }
            departments.add(userVo.getDepartmentId());
            userDto.setDepartmentIds(departments);
        }

        // ????????????
        session.setAttribute(tokens, userDto);

        Map<String, Object> returnMap = new HashMap<>(16);

        session.setAttribute(tokens, userDto);
        returnMap.put("userInfo", userDto);

        return CommonResult.success("????????????", returnMap);

    }

    /**
     * @param userDto
     * @param vo
     * @Description: ????????????????????????
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
        //?????????????????????
        if (list.size()>0){
            //??????????????????
            for (AuthUserRole info:list) {
                //????????????
                AuthRole authRole = authRoleService.getById(info.getRoleId());
                String roleCode = authRole.getRoleCode();
                if (roleCode.equals(AuthRole.SCHOOL_ADMIN)){
                    //??????????????????
                }else if (roleCode.equals(AuthRole.DEPARTMENT_ADMIN)){
                    //?????????????????????
                }else if (roleCode.equals(AuthRole.TEACHER)){
                    //????????????
                }
            }
        }

        returnMap.put("list",userRoleDtoList);

        return CommonResult.success(returnMap);
    }

    /**
     * @Decription: ?????????????????????
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/11 15:07
     */
    public CommonResult<Object> updateUserPassword(User user, UpdateUserVo updateUserVo) {
        Session session = SecurityUtils.getSubject().getSession();
        //  ????????????????????????????????????code
        if (updateUserVo.getOldPassword() != null) {
            // ???????????????????????????????????????
            if (!PasswordCryptoTool.checkPassword(updateUserVo.getOldPassword(), user.getPassword())) {
                return CommonResult.error(500, "???????????????");
            }
            if (updateUserVo.getNewPassword().length()<PasswordCryptoTool.DEFAULT_PASSWORD_LENGTH){
                return CommonResult.error(500,"????????????????????????6???");
            }
            // ????????????
            String newPassword = PasswordCryptoTool.encryptPassword(updateUserVo.getNewPassword());

            user.setPassword(newPassword);
            boolean updateById = super.updateById(user);
            if (updateById) {
                session.removeAttribute(session.getId());
                SecurityUtils.getSubject().logout();
                return CommonResult.success("????????????", null);
            }
            return CommonResult.error(500, "????????????");
        } else if (updateUserVo.getCode() != null) {
            // ???????????????????????????
            CommonResult<Object> objectCommonResult = this.confimOldTelephoneCode(updateUserVo);
            if (!objectCommonResult.isSuccess()) {
                return objectCommonResult;
            }
            // ????????????
            String newPassword = PasswordCryptoTool.encryptPassword(updateUserVo.getNewPassword());
            user.setPassword(newPassword);
            // ????????????
            SecurityUtils.getSubject().getSession().removeAttribute(SmsConfig.REDIS_CODE + updateUserVo.getOldTelephone());
            boolean updateById = super.updateById(user);
            if (updateById) {
                session.removeAttribute(session.getId());
                SecurityUtils.getSubject().logout();
                return CommonResult.success("????????????", null);
            }
            return CommonResult.error(500, "????????????");
        } else {
            return CommonResult.error(500, "???????????????????????????");
        }
    }

    /**
     * @Decription: ??????????????????????????????
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/11 15:25
     */
    public CommonResult<Object> confimCode(UpdateUserVo updateUserVo) {
        // ???????????????
        Map<String, Object> map = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(SmsConfig.REDIS_CODE + updateUserVo.getTelephone());
        // ???????????????????????????
        if (map == null) {
            return CommonResult.error(500, "??????????????????");
        }
        Long time = (Long) map.get("time");
        String code = (String) map.get("code");
        if (RedisException.EXPIRETIME < (System.currentTimeMillis() - time)) {
            return CommonResult.error(500, "???????????????");
        }
        if (!updateUserVo.getCode().equals(code)) {
            return CommonResult.error(500, "???????????????");
        }
        return CommonResult.success(SUCCESS, null);
    }

    /**
     * @Decription: ??????????????????????????????
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2022/1/11 15:25
     */
    public CommonResult<Object> confimOldTelephoneCode(UpdateUserVo updateUserVo) {
        // ???????????????
        Map<String, Object> map = (Map<String, Object>) SecurityUtils.getSubject().getSession().getAttribute(SmsConfig.REDIS_CODE + updateUserVo.getOldTelephone());
        // ???????????????????????????
        if (map == null) {
            return CommonResult.error(500, "??????????????????");
        }
        Long time = (Long) map.get("time");
        String code = (String) map.get("code");
        if (RedisException.EXPIRETIME < (System.currentTimeMillis() - time)) {
            return CommonResult.error(500, "???????????????");
        }
        if (!updateUserVo.getCode().equals(code)) {
            return CommonResult.error(500, "???????????????");
        }
        return CommonResult.success(SUCCESS, null);
    }

    /**
     * @Decription:??????sdk????????????????????????
     * @params:
     * @return:
     * @Author:hxj
     * @Date:2021/12/13 11:29
     */
    public String getMessageForCode(String phone, String code) {
        //???????????????AccessKey??????
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", SmsConfig.ACCESS_KEY_ID, SmsConfig.ACCESS_KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        //?????????????????????????????????
        request.setSysDomain("dysmsapi.aliyuncs.com");
        //API????????????
        request.setSysVersion("2017-05-25");
        //API?????????
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        //???????????????????????????
        request.putQueryParameter("PhoneNumbers", phone);
        //??????????????????
        request.putQueryParameter("SignName", SmsConfig.SIGN_NAME);
        //????????????ID
        request.putQueryParameter("TemplateCode", SmsConfig.TEMPLATE_CODE);
        Map<String, Object> params = new HashMap<>(16);
        params.put("code", code);
        //????????????????????????????????????
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
