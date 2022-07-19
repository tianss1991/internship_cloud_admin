package com.above.service.impl;

import com.above.bean.excel.TeacherInfoExcelData;
import com.above.config.easyExcel.TeacherInfoExcellListener;
import com.above.dao.DepartmentInfoMapper;
import com.above.dao.SchoolInfoMapper;
import com.above.dao.TeacherInfoMapper;
import com.above.dto.UserDto;
import com.above.po.*;
import com.above.service.*;
import com.above.utils.CommonResult;
import com.above.utils.PasswordCryptoTool;
import com.above.utils.RandomUtil;
import com.above.vo.BaseVo;
import com.above.vo.TeacherVo;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <p>
 * 教师信息表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Slf4j
@Service
public class TeacherInfoServiceImpl extends ServiceImpl<TeacherInfoMapper, TeacherInfo> implements TeacherInfoService {

    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    @Lazy
    private AuthUserRoleService userRoleService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private SchoolTeacherRelationService schoolTeacherRelationService;
    @Autowired
    private DepartmentTeacherRelationService departmentTeacherRelationService;
    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;
    @Autowired
    TransactionDefinition transactionDefinition;
    @Autowired
    private DepartmentInfoMapper departmentInfoMapper;
    @Autowired
    private SchoolInfoMapper schoolInfoMapper;


    /**
     * @Description: 添加教师
     * @Author: LZH
     * @Date: 2022/1/11 11:24
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> insertTeacherInfo(UserDto userDto,TeacherVo teacherVo) {
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        //获取参数
        Integer createBy = userDto.getId();
        String teacherName = teacherVo.getTeacherName();
        String workNumber = teacherVo.getWorkNumber();
        Integer gender = teacherVo.getGender();

        //编制类型
        String workType = teacherVo.getWorkType();
        //电话号码
        String telephone = teacherVo.getTelephone();
        //邮箱
        String mail = teacherVo.getMail();

        if(roleCode.equals(AuthRole.SCHOOL_ADMIN)){
            List<Integer> schoolIds = userDto.getSchoolIds();
            teacherVo.setSchoolId(schoolIds.get(0));
        }
        //如果是二级学院管理、则关联本二级学院
        if (roleCode.equals(AuthRole.DEPARTMENT_ADMIN)){
            List<Integer> departmentIds = userDto.getDepartmentIds();
            teacherVo.setDepartmentId(departmentIds.get(0));
        }

//        //判断关联参数是否有传
        if (teacherVo.getSchoolId() == null){
            return CommonResult.error(500,"缺少学校id");
        }
        if (teacherVo.getDepartmentId() == null){
            return CommonResult.error(500,"缺少二级学院id");
        }

        //关联信息
        Integer schoolId = teacherVo.getSchoolId();
        Integer departmentId = teacherVo.getDepartmentId();
        String schoolName = null;
        String departmentName = null;

        if (schoolId != null){
            SchoolInfo schoolInfo = schoolInfoMapper.selectById(teacherVo.getSchoolId());
            if (schoolInfo == null || schoolInfo.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"学校信息不存在或已删除");
            }
            schoolId = schoolInfo.getId();
            schoolName = schoolInfo.getSchoolName();
        }
        if (departmentId!= null){
            DepartmentInfo departmentInfo = departmentInfoMapper.selectById(teacherVo.getDepartmentId());
            if (departmentInfo == null || departmentInfo.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"学校信息不存在");
            }
            SchoolInfo schoolInfo = schoolInfoMapper.selectById(departmentInfo.getSchoolId());
            schoolId = schoolInfo.getId();
            schoolName = schoolInfo.getSchoolName();
            departmentId = departmentInfo.getId();
            departmentName = departmentInfo.getDepartmentName();
        }


        /*判断账号是否符合要求*/
        QueryWrapper<UserAccount> accountQueryWrapper = new QueryWrapper<>();
        accountQueryWrapper.eq("account_number",workNumber).eq("deleted",BaseVo.UNDELETE);
        //查询是否有重复账号
        int count = userAccountService.count(accountQueryWrapper);
        //若存在则返回
        if (count>0){
            return CommonResult.error(500,"工号重复");
        }
        /*先添加新账户*/
        User user = new User();
        //用户名默认为教师名
        user.setUserName(teacherName);
        //设置默认密码
        user.setPassword(PasswordCryptoTool.getDefaultPassword(workNumber));
        //创建者
        user.setCreateBy(createBy);
        //设置为教师
        user.setUserType(2);
        //逻辑删除-未删除
        user.setDeleted(0);
        //状态-正常
        user.setStatus(0);
        //插入数据库
        boolean saveUser = userService.save(user);
        try{
            //判断是否存储成功
            if (!saveUser){
                return CommonResult.error(500,"创建用户信息失败");
            }
            Integer userId = user.getId();

            /*添加账户表*/

            UserAccount userAccount = new UserAccount();
            //关联user_id
            userAccount.setUserId(userId);
            //账号为教师工号
            userAccount.setAccountNumber(workNumber);
            userAccount.setEmail(mail);
//            userAccount.setTelephone(telephone);
            //创建者
            userAccount.setCreateBy(createBy);
            //插入数据库
            boolean saveAccount = userAccountService.save(userAccount);
            if (!saveAccount){
                throw new RuntimeException("添加失败");
            }
            /*添加用户权限*/
            AuthUserRole authUserRole = new AuthUserRole();
            //设置user_id
            authUserRole.setUserId(userId);
            //角色id
            authUserRole.setRoleId(6);
            //创建者
            authUserRole.setCreateBy(createBy);
            //插入数据库
            boolean saveRole = userRoleService.save(authUserRole);
            if (!saveRole){
                throw new RuntimeException("添加失败");
            }
            /*最后添加教师表*/
            TeacherInfo teacherInfo = new TeacherInfo();
            //判断是否有关联信息
            if (schoolId != null){
                teacherInfo.setSchoolId(schoolId);
            }
            if (departmentId != null){
                teacherInfo.setDepartmentId(departmentId);
            }
            if (departmentName != null){
                teacherInfo.setDepartmentName(departmentName);
            }
            if (schoolName != null){
                teacherInfo.setSchoolName(schoolName);
            }
            //关联用户
            teacherInfo.setUserId(userId);
            //教师姓名
            teacherInfo.setTeacherName(teacherName);
            //编制类型
            teacherInfo.setWorkType(workType);
            //电话
            teacherInfo.setTelephone(telephone);
            //邮箱
            teacherInfo.setMail(mail);
            //性别
            teacherInfo.setGender(gender);
            //工号
            teacherInfo.setWorkNumber(workNumber);
            //创建者
            teacherInfo.setCreateBy(createBy);
            //插入数据库
            boolean save = super.save(teacherInfo);
            //判断信息是否保存成功
            if (save){
                return CommonResult.success("添加成功",null);
            }else {
                throw new RuntimeException("添加失败");
            }

        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("异常打印-->"+e.getMessage());
            return CommonResult.error(500,e.getMessage());
        }
    }

    /**
     * @Description: 修改教师
     * @Author: LZH
     * @Date: 2022/1/11 11:24
     */
    @Override
    public CommonResult<Object> modifyTeacher(UserDto userDto, TeacherVo teacherVo) throws RuntimeException{
        //获取参数
        Integer updateBy = userDto.getId();
        Integer teacherId = teacherVo.getTeacherId();
        String workNumber = teacherVo.getWorkNumber();
        String teacherName = teacherVo.getTeacherName();
        //电话号码
        String telephone = teacherVo.getTelephone();
        String mail = teacherVo.getMail();
        //用于判断是否修改
        boolean flag = false;

        //查找老师是否存在
        TeacherInfo teacherInfo = super.getById(teacherId);
        if (teacherInfo == null){
            return CommonResult.error(500,"教师不存在");
        }

        if (!StringUtils.isBlank(telephone)){
            teacherInfo.setTelephone(telephone);
            flag=true;
        }
        if (!StringUtils.isBlank(mail)){
            //根据user_id获取账户信息
            QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",teacherInfo.getUserId());
            List<UserAccount> list = userAccountService.list(queryWrapper);
            if (list == null || list.isEmpty()){
                return CommonResult.error(500,"账号信息不存在");
            }
            UserAccount one = list.get(0);
            //设置账号
            one.setEmail(mail);
            //设置更新人
            one.setUpdateBy(updateBy);
            //插入数据库
            boolean b = userAccountService.updateById(one);
            if (!b){
                return CommonResult.error(500,"修改邮箱失败");
            }
            teacherInfo.setMail(mail);
            flag=true;
        }
        if (teacherVo.getGender() != null){
            teacherInfo.setGender(teacherVo.getGender());
            flag=true;
        }
        if (teacherVo.getDepartmentId() != null && teacherVo.getDepartmentId() > 0){
            if (!teacherVo.getTeacherId().equals(teacherInfo.getDepartmentId())){
                DepartmentInfo departmentInfo = departmentInfoMapper.selectById(teacherVo.getDepartmentId());
                teacherInfo.setDepartmentId(departmentInfo.getId()).setDepartmentName(departmentInfo.getDepartmentName());
                flag = true;
            }
        }

        //若教师姓名参数不为空则判断是否修改
        if (!StringUtils.isBlank(teacherName)){
            //判断是否修改
            if (!teacherInfo.getTeacherName().equals(teacherName)){
                /*修改姓名，同时修改用户信息*/
                //获取用户信息

                User user = userService.getById(teacherInfo.getUserId());
                //设置用户名
                user.setUserName(teacherName);
                //更新人
                user.setUpdateBy(updateBy);
                //更新数据库
                boolean b = userService.updateById(user);
                //若成功则修改状态，并把新工号放入teacherInfo
                if (b){
                    teacherInfo.setTeacherName(teacherName);
                    flag=true;
                }else {
                    return CommonResult.error(500,"修改账号失败");
                }

            }
        }

        //若工号参数不为空则判断是否修改
        if (!StringUtils.isBlank(workNumber)){
            //判断是否修改
            if (!teacherInfo.getWorkNumber().equals(workNumber)){

                /*修改工号，同时修改账户信息*/

                //判断工号是否符合要求
                QueryWrapper<UserAccount> accountQueryWrapper = new QueryWrapper<>();
                accountQueryWrapper.eq("account_number",workNumber).eq("deleted",BaseVo.UNDELETE);
                int count = userAccountService.count(accountQueryWrapper);
                if (count>0){
                    return CommonResult.error(500,"工号重复");
                }

                //根据user_id获取账户信息
                QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id",teacherInfo.getUserId());
                List<UserAccount> list = userAccountService.list(queryWrapper);
                if (list == null || list.isEmpty()){
                    return CommonResult.error(500,"账号信息不存在");
                }
                UserAccount one = list.get(0);
                //设置账号
                one.setAccountNumber(workNumber);
                //设置更新人
                one.setUpdateBy(updateBy);
                //插入数据库
                boolean b = userAccountService.updateById(one);

                //若成功则修改状态，并把新工号放入teacherInfo
                if (b){
                    teacherInfo.setWorkNumber(workNumber);
                    flag=true;
                }else {
                    return CommonResult.error(500,"修改账号失败");
                }
            }
        }
        /*根据flag判断是否需要修改*/
        if (flag){
            //设置更新人
            teacherInfo.setUpdateBy(updateBy);
            //插入数据库
            boolean b = super.updateById(teacherInfo);
            //根据更新是否成功返回
            if (b){
                return CommonResult.success("修改成功",null);
            }else {
                throw new RuntimeException("修改失败");
            }

        }else {
            return CommonResult.error(500,"无需修改");
        }

    }

    /**
     * @Description: 删除教师
     * @Author: LZH
     */
    @Override
    public CommonResult<Object> deleteTeacher(UserDto userDto,TeacherVo teacherVo) throws RuntimeException{

        //获取参数
        Collection<Integer> teacherIdList = teacherVo.getTeacherIdList();
        Integer updateBy = userDto.getId();
        //批量查询
        Collection<TeacherInfo> teacherInfos = super.getBaseMapper().selectBatchIds(teacherIdList);
        //判断是否全部都有查出
        if (teacherIdList.size()>teacherInfos.size()){
            return CommonResult.error(500,"有教师已被删除");
        }
        //循环修改为删除状态
        for (TeacherInfo info:teacherInfos) {
            // 查看该教师是否状态为删除
            if (info.getDeleted()==1) {
                return CommonResult.error(500, "【" + info.getTeacherName() + "】该教师已删除");
            }
            //设置为删除状态
            info.setDeleted(1);
            //设置更新人
            info.setUpdateBy(updateBy);
        }
        //修改教师信息
        boolean i = super.updateBatchById(teacherInfos);
        //若修改后进入try-catch中，
//        try{
        if (i){
            //设置批量删除数组
            Collection<User> users = new ArrayList<>();
            Collection<UserAccount> userAccounts = new ArrayList<>();
            Collection<AuthUserRole> userRoles = new ArrayList<>();
            //关联学校
            Collection<SchoolTeacherRelation> schoolTeacher = new ArrayList<>();
            //关联二级学院
            Collection<DepartmentTeacherRelation> departmentTeacher= new ArrayList<>();

            //循环删除教师信息
            for (TeacherInfo info:teacherInfos) {
                //用户id
                Integer userId = info.getUserId();
                //教师id
                Integer teacherId = info.getId();
                /*设置教师账号为删除状态*/
                //根据user_id获取
                User user = userService.getById(userId);
                if (user == null){
                    throw new RuntimeException("找不到用户信息");
                }
                user.setUpdateBy(updateBy);
                //设置删除状态
                user.setDeleted(User.DELETED);
                //添加到批量删除数组
                users.add(user);
                //用户账户信息表
                QueryWrapper<UserAccount> accountQueryWrapper = new QueryWrapper<>();
                accountQueryWrapper.eq("user_id",userId).eq("deleted",0);
                UserAccount userAccount = userAccountService.getOne(accountQueryWrapper);
                if (userAccount == null){
                    throw new RuntimeException("找不到账户信息");
                }
                //修改状态
                userAccount.setDeleted(1);
                userAccount.setUpdateBy(updateBy);
                //添加到批量删除数组
                userAccounts.add(userAccount);

                /*若删除成功则继续删除教师关联的权限*/

                //查询school权限
                QueryWrapper<SchoolTeacherRelation> schoolTeacherQueryWrapper = new QueryWrapper<>();
                schoolTeacherQueryWrapper.eq("teacher_id",teacherId).eq("deleted",0);
                List<SchoolTeacherRelation> list = schoolTeacherRelationService.list(schoolTeacherQueryWrapper);
                for (SchoolTeacherRelation schoolTeacherRelation:list) {
                    //设置删除状态
                    schoolTeacherRelation.setDeleted(1);
                    schoolTeacherRelation.setUpdateBy(updateBy);
                    //添加到批量删除数组
                    schoolTeacher.add(schoolTeacherRelation);
                }

                //查询department权限
                QueryWrapper<DepartmentTeacherRelation> departmentTeacherQueryWrapper = new QueryWrapper<>();
                departmentTeacherQueryWrapper.eq("teacher_id",teacherId).eq("deleted",0);
                List<DepartmentTeacherRelation> list2 = departmentTeacherRelationService.list(departmentTeacherQueryWrapper);
                for (DepartmentTeacherRelation departmentTeacherRelation:list2) {
                    //设置删除状态
                    departmentTeacherRelation.setDeleted(1);
                    departmentTeacherRelation.setUpdateBy(updateBy);
                    //添加到批量删除数组
                    departmentTeacher.add(departmentTeacherRelation);
                }

                /*删除权限*/
                QueryWrapper<AuthUserRole> authUserRoleQueryWrapper = new QueryWrapper<>();
                authUserRoleQueryWrapper.eq("user_id",userId).eq("deleted",0);
                AuthUserRole userRole = userRoleService.getOne(authUserRoleQueryWrapper);
                if (userRole != null){
                    userRole.setUpdateBy(updateBy);
                    userRole.setDeleted(1);
                    userRoles.add(userRole);
                }

            }
            //进行批量修改
            boolean updateUser = userService.updateBatchById(users);
            if (!updateUser){
                throw new RuntimeException("删除用户异常");
            }
            boolean updateAccount = userAccountService.updateBatchById(userAccounts);
            if (!updateAccount){
                throw new RuntimeException("删除用户异常");
            }
            //判断是否需要更新
            if (schoolTeacher.size()>0) {
                boolean updateSchool = schoolTeacherRelationService.updateBatchById(schoolTeacher);
                if (!updateSchool) {
                    throw new RuntimeException("删除用户异常");
                }
            }
            //判断是否需要更新
            if (departmentTeacher.size()>0) {
                boolean updateDepartment = departmentTeacherRelationService.updateBatchById(departmentTeacher);
                if (!updateDepartment) {
                    throw new RuntimeException("删除用户异常");
                }
            }
            if (userRoles.size()>0) {
                boolean updateClass = userRoleService.updateBatchById(userRoles);
                if (!updateClass) {
                    throw new RuntimeException("删除用户异常");
                }
            }

            //返回
            return CommonResult.success("删除成功",null);
        }else {
            throw new RuntimeException("删除用户异常");
        }
//        }catch (Exception e){
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            log.info("异常打印-->"+e.getMessage());
//            return CommonResult.error(500,e.getMessage());
//        }

    }

    /**
     * @Description: 获取教师列表（分页）
     * @Author: LZH
     * @Date: 2022/1/11 11:25
     */
    @Override
    public CommonResult<Object> getTeacherPageList(UserDto userDto,TeacherVo teacherVo) {
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        //新建分页参数
        Page<TeacherInfo> teacherInfoPage = new Page<>(teacherVo.getPage(), teacherVo.getSize());
        QueryWrapper<TeacherInfo> teacherInfoQueryWrapper = getQueryWrapper(userDto, teacherVo);

        //查询
        IPage<TeacherInfo> page = super.page(teacherInfoPage, teacherInfoQueryWrapper);
        //放入返回集合
        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap.put(BaseVo.LIST,page.getRecords());
        returnMap.put(BaseVo.PAGE,page.getPages());
        returnMap.put(BaseVo.TOTAL,page.getTotal());

        return CommonResult.success(returnMap);
    }

    /**
     * @Description: 获取教师列表（不分页）
     * @Author: LZH
     * @Date: 2022/1/11 11:25
     */
    @Override
    public CommonResult<Object> getTeacherWithoutList(UserDto userDto,TeacherVo teacherVo) {
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        QueryWrapper<TeacherInfo> teacherInfoQueryWrapper = getQueryWrapper(userDto, teacherVo);

        //查询
        List<TeacherInfo> list = super.list(teacherInfoQueryWrapper);

        //放入返回集合
        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap.put(BaseVo.LIST,list);

        return CommonResult.success(returnMap);
    }

    /**
     * @Description: 导入教师
     * @Author: LZH
     * @Date: 2022/1/13 16:34
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> importTeacherInfo(UserDto userDto, MultipartFile file,BaseVo baseVo)  {

        String roleCode = userDto.getUserRoleDto().getRoleCode();

        try {
            //获取通用参数
            Integer createBy = userDto.getId();

            //获取文件名
            file.getOriginalFilename();
            //获取文件流
            InputStream inputStream = file.getInputStream();
            //实例化实现了AnalysisEventListener接口的类
            TeacherInfoExcellListener listener = new TeacherInfoExcellListener();
            ExcelReader build = EasyExcel.read(inputStream, TeacherInfoExcelData.class, listener).build();
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            build.read(readSheet);
            //获取数据
            List<Object> list = listener.getDatas();

            //判断是否有数据
            if (list.size()>0){

                //关联信息
                Integer schoolId = null;
                Integer departmentId = null;
                String departmentName = null;
                String schoolName = null;
                boolean isSchoolAdmin = false;
                boolean isDepAdmin = false;

                if (roleCode.equals(AuthRole.SCHOOL_ADMIN)){
                    if (userDto.getSchoolIds() == null || userDto.getSchoolIds().size() <= 0){
                        return CommonResult.error(500,"绑定学校id为空");
                    }
                    schoolId = userDto.getSchoolIds().get(0);
                    schoolName = schoolInfoMapper.selectById(schoolId).getSchoolName();
                    isSchoolAdmin = true;
                }else if (roleCode.equals(AuthRole.DEPARTMENT_ADMIN)){
                    if (userDto.getDepartmentIds() == null || userDto.getDepartmentIds().size() <= 0){
                        return CommonResult.error(500,"绑定二级学院id为空");
                    }
                    departmentId = userDto.getDepartmentIds().get(0);
                    DepartmentInfo departmentInfo = departmentInfoMapper.selectById(departmentId);
                    departmentName = departmentInfo.getDepartmentName();
                    schoolId = departmentInfo.getSchoolId();
                    schoolName = schoolInfoMapper.selectById(schoolId).getSchoolName();
                    isDepAdmin = true;
                }

                //user批量插入数组
                Collection<User> users = new HashSet<>();
                //新建教师，账户信息数组
                Collection<TeacherInfo> teacherInfos = new HashSet<>();
                Collection<UserAccount> userAccounts = new HashSet<>();
                Collection<AuthUserRole> userRoles = new HashSet<>();

                //获取学校列表
                QueryWrapper<SchoolInfo> schoolInfoQueryWrapper = new QueryWrapper<>();
                schoolInfoQueryWrapper.eq("deleted",0);
                List<SchoolInfo> schoolInfos = schoolInfoMapper.selectList(schoolInfoQueryWrapper);
                //二级学院列表
                QueryWrapper<DepartmentInfo> departmentQueryWrapper = new QueryWrapper<>();
                departmentQueryWrapper.eq("deleted",BaseVo.UNDELETE);
                List<DepartmentInfo> departmentInfos = departmentInfoMapper.selectList(departmentQueryWrapper);

                /*第一次循环添加User，获取User_id*/

                for (Object o : list){
                    //强转
                    TeacherInfoExcelData data = (TeacherInfoExcelData) o;
                    //获取参数
                    String name = data.getName();
                    //工号
                    String workNumber = data.getWorkNumber();
                    String phone = data.getPhone();
                    //查询工号是否符合
                    QueryWrapper<UserAccount> accountQueryWrapper = new QueryWrapper<>();
                    accountQueryWrapper.eq("account_number",workNumber).eq("deleted",BaseVo.UNDELETE);
                    //查询是否有重复账号
                    int count = userAccountService.count(accountQueryWrapper);
                    //若存在则返回
                    if (count>0){
                        return CommonResult.error(500,name+"添加失败，该教师【"+workNumber+"】工号重复");
                    }

                    if (userAccounts.size() > 0){
                        for (UserAccount info:userAccounts) {
                            if (info.getAccountNumber().equals(workNumber)){
                                return CommonResult.error(500,name+",该教师【"+workNumber+"】工号重复,导入信息中请确保工号唯一");
                            }
                        }
                    }

                    /*先循环创建user*/

                    //用户唯一码
                    String userCode = RandomUtil.randomUUID(16);

                    User user = new User();
                    //设置用户唯一码
                    user.setUserCode(userCode).setUserName(name).setUserType(2).setDeleted(0).setStatus(0).setCreateBy(createBy);
                    //设置默认密码
                    user.setPassword(PasswordCryptoTool.getDefaultPassword(workNumber));
                    //放入user数组
                    users.add(user);

                    /*添加账户表*/

                    UserAccount userAccount = new UserAccount();
                    //用户唯一码
                    userAccount.setUserCode(userCode).setAccountNumber(workNumber).setTelephone(phone).setCreateBy(createBy);
                    //放入数组
                    userAccounts.add(userAccount);
                }
                //判断是否有用户
                if (users.size()==0){
                    return CommonResult.error(500,"添加用户异常");
                }
                if (users.size()!=list.size()){
                    return CommonResult.error(500,"添加用户异常");
                }
                //循环结束后批量添加
                boolean saveBatch = userService.saveBatch(users,1000);
                //判断是否存储成功
                if (!saveBatch){
                    return CommonResult.error(500,"创建用户信息失败");
                }
                /*成功后进入第二次循环 - 处理院校信息*/
                for (Object o:list) {
                    //强转
                    TeacherInfoExcelData data = (TeacherInfoExcelData) o;
                    //获取关联参数，判断数据是否符合条件
                    String schoolTeacher = data.getSchoolName();
                    String departmentTeacher = data.getDepartment();
                    //获取参数
                    String name = data.getName();

                    //判断教师的关联信息
                    if (isSchoolAdmin || isDepAdmin){
                        if (!schoolTeacher.equals(schoolName)) {
                            throw new RuntimeException("教师【"+name+"】的学校,非用户管理学校");
                        }
                        if (isDepAdmin){
                            if (!departmentTeacher.equals(departmentName)) {
                                throw new RuntimeException("教师【"+name+"】的二级学院,非用户管理的二级学院");
                            }
                        }
                        if (isSchoolAdmin){
                            int dId = 0;
                            for (DepartmentInfo info:departmentInfos) {
                                if (info.getSchoolId().equals(schoolId)){
                                    if (info.getDepartmentName().equals(departmentTeacher)){
                                        dId = info.getId();
                                        break;
                                    }
                                }
                            }
                            if (dId == 0){
                                DepartmentInfo departmentInfo = new DepartmentInfo();
                                departmentInfo.setDepartmentName(departmentTeacher).setSchoolId(schoolId);
                                int insert = departmentInfoMapper.insert(departmentInfo);
                                if (insert <= 0){
                                    throw new RuntimeException("创建二级学院出错");
                                }else {
                                    departmentInfos.add(departmentInfo);
                                }
                            }
                        }
                    }else {
                        //超管判断院校信息
                        int sId = 0;
                        String sName = null;
                        SchoolInfo created = null;
                        for (SchoolInfo info:schoolInfos) {
                            if (info.getSchoolName().equals(schoolTeacher)){
                                sId = info.getId();
                                sName = info.getSchoolName();
                                break;
                            }
                        }
                        if (sId == 0){
//                            return CommonResult.error(500,"找不到教师【"+name+"】的学校");
                            //创建二级学院并塞入数组
                            SchoolInfo schoolInfo = new SchoolInfo();
                            schoolInfo.setSchoolName(schoolTeacher).setStatus(0).setCreateBy(userDto.getId());
                            int insert = schoolInfoMapper.insert(schoolInfo);
                            if (insert<=0){
                                throw new RuntimeException("创建学校出错");
                            }else {
                                created = schoolInfo;
                                schoolInfos.add(schoolInfo);
                            }
                        }
                        int dId = 0;
                        if (created == null){
                            for (DepartmentInfo info:departmentInfos) {
                                if (info.getSchoolId().equals(sId)){
                                    if (info.getDepartmentName().equals(departmentTeacher)){
                                        dId = info.getId();
                                        break;
                                    }
                                }
                            }
                        }
                        if (dId == 0){
//                            return CommonResult.error(500,"找不到教师【"+name+"】的二级学院");
                            DepartmentInfo departmentInfo = new DepartmentInfo();
                            departmentInfo.setDepartmentName(departmentTeacher);
                            if (created != null){
                                departmentInfo.setSchoolId(created.getId());
                            }else {
                                departmentInfo.setSchoolId(sId);
                            }
                            int insert = departmentInfoMapper.insert(departmentInfo);
                            if (insert <= 0){
                                throw new RuntimeException("创建二级学院出错");
                            }else {
                                departmentInfos.add(departmentInfo);
                            }
                        }
                    }

                }
                /*成功后进入第三次循环 -- 处理教师信息*/
                for (UserAccount account:userAccounts) {
                    //循环user
                    for (User user:users) {

                        //判断用户唯一码是否相同
                        if (account.getUserCode().equals(user.getUserCode())){
                            //取出userId
                            Integer userId = user.getId();
                            //放入关联
                            account.setUserId(userId);


                            //根据id循环excel集合

                            for (Object o:list) {
                                //强转
                                TeacherInfoExcelData data = (TeacherInfoExcelData) o;
                                //如果工号相同则添加信息
                                if (data.getWorkNumber().equals(account.getAccountNumber())){
                                    //获取参数
                                    String name = data.getName();
                                    //工号
                                    String workNumber = data.getWorkNumber();
                                    //性别
                                    String gender = data.getGender();
                                    //部门名称
                                    String department = data.getDepartment();
                                    String dataSchoolName = data.getSchoolName();
                                    //电话
                                    String phone = data.getPhone();
                                    //邮箱
                                    String mail = data.getMail();
                                    //关联账号
//                                    account.setTelephone(phone).setEmail(mail);

                                    //设置性别
                                    int sex = 0;
                                    if (!StringUtils.isEmpty(gender)) {
                                        if ("男".equals(gender)) {
                                            sex = 1;
                                        }else if ("女".equals(gender)){
                                            sex = 2;
                                        }
                                    }

                                    /*添加用户权限*/
                                    AuthUserRole authUserRole = new AuthUserRole();
                                    //设置user_id
                                    authUserRole.setUserId(user.getId()).setRoleId(6).setCreateBy(createBy);
                                    userRoles.add(authUserRole);

                                    /*最后添加教师表*/

                                    TeacherInfo teacherInfo = new TeacherInfo();

                                    for (SchoolInfo info:schoolInfos) {
                                        if (info.getSchoolName().equals(dataSchoolName)){
                                            schoolId = info.getId();
                                            schoolName = info.getSchoolName();
                                            break;
                                        }
                                    }
                                    for (DepartmentInfo info:departmentInfos) {
                                        if (info.getSchoolId().equals(schoolId)){
                                            if (info.getDepartmentName().equals(department)){
                                                departmentId = info.getId();
                                                departmentName = info.getDepartmentName();
                                                break;
                                            }
                                        }
                                    }

                                    if (!isDepAdmin){
                                        for (DepartmentInfo info:departmentInfos) {
                                            if (info.getSchoolId().equals(schoolId)){
                                                if (info.getDepartmentName().equals(department)){
                                                    departmentId = info.getId();
                                                    departmentName = info.getDepartmentName();
                                                    break;
                                                }
                                            }
                                        }
                                        if (!isSchoolAdmin){
                                            for (SchoolInfo info:schoolInfos) {
                                                if (info.getSchoolName().equals(dataSchoolName)){
                                                    schoolId = info.getId();
                                                    schoolName = info.getSchoolName();
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    //教师信息
                                    teacherInfo.setTeacherName(name).setWorkNumber(workNumber).setWorkStatus(0).setGender(sex)
                                            .setTelephone(phone).setCreateBy(createBy).setUserId(userId).setMail(mail);
                                    //关联信息
                                    teacherInfo.setSchoolId(schoolId).setSchoolName(schoolName)
                                            .setDepartmentId(departmentId).setDepartmentName(departmentName);
                                    //放入集合
                                    teacherInfos.add(teacherInfo);
                                    break;
                                }

                            }
                            break;
                        }
                    }

                }
                /*统一批量插入*/

                //账户信息
                if (userAccounts.size()>0){
                    boolean saveAccount = userAccountService.saveBatch(userAccounts,1000);
                    if (!saveAccount){
                        throw new RuntimeException("导入账号信息失败");
                    }
                }
                //用户角色
                if (userRoles.size()>0){
                    boolean saveRole = userRoleService.saveBatch(userRoles);
                    if (!saveRole){
                        throw new RuntimeException("导入角色信息失败");
                    }
                }
                //教师信息
                if (teacherInfos.size()>0){
                    boolean saveTeacher = this.saveBatch(teacherInfos);
                    if (!saveTeacher){
                        throw new RuntimeException("导入教师信息失败");
                    }
                }
                return CommonResult.success("导入成功",null);
            }else {
                return CommonResult.error(500,"没有数据");
            }
        }catch (RuntimeException e ){
            log.info("异常打印-->"+e.getMessage());
            return CommonResult.error(500,e.getMessage());
        }catch (IOException e){
            return CommonResult.error(500,e.getMessage());
        }


    }


    private QueryWrapper<TeacherInfo> getQueryWrapper(UserDto userDto,TeacherVo teacherVo){
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        QueryWrapper<DepartmentInfo> departmentInfoQueryWrapper = new QueryWrapper<>();
        departmentInfoQueryWrapper.eq("department_name","通识学院").eq("deleted",0);
        DepartmentInfo departmentInfo = departmentInfoMapper.selectOne(departmentInfoQueryWrapper);
        //新建查询
        QueryWrapper<TeacherInfo> teacherInfoQueryWrapper = new QueryWrapper<>();
        //筛选删除状态
        teacherInfoQueryWrapper.eq("deleted",0);
        //若有传在职离职状态则筛选
        if (teacherVo.getWorkStatus() != null){
            teacherInfoQueryWrapper.eq("work_status",teacherVo.getWorkStatus());
        }
        //超管权限
        if(AuthRole.ADMIN.equals(roleCode)){
            if (teacherVo.getSchoolId() != null && teacherVo.getSchoolId() > 0){
                teacherInfoQueryWrapper.eq("school_id",teacherVo.getSchoolId());
            }
            if (teacherVo.getDepartmentId() != null && teacherVo.getDepartmentId() > 0){
                teacherInfoQueryWrapper.eq("department_id",teacherVo.getDepartmentId());
            }
        }else if (AuthRole.SCHOOL_ADMIN.equals(roleCode)){
            if (teacherVo.getSchoolId() != null && teacherVo.getSchoolId() > 0){
                teacherInfoQueryWrapper.eq("school_id",teacherVo.getSchoolId());
            }else {
                teacherInfoQueryWrapper.in("school_id",userDto.getSchoolIds());
            }
            if (teacherVo.getDepartmentId() != null && teacherVo.getDepartmentId() > 0){
                teacherInfoQueryWrapper.eq("department_id",teacherVo.getDepartmentId());
            }else if (teacherVo.getDepartmentId() != null && teacherVo.getDepartmentId() < 0){
                teacherInfoQueryWrapper.isNull("department_id");
            }

        }else if (AuthRole.DEPARTMENT_ADMIN.equals(roleCode)) {
            teacherInfoQueryWrapper.in("school_id", userDto.getTeacherInfo().getSchoolId());
            if (teacherVo.getDepartmentId() != null && teacherVo.getDepartmentId() > 0) {
                teacherInfoQueryWrapper.eq("department_id", teacherVo.getDepartmentId());
            }else if (teacherVo.getDepartmentId() != null && teacherVo.getDepartmentId() < 0){
                teacherInfoQueryWrapper.isNull("department_id");
            } else {
                teacherInfoQueryWrapper.in("department_id", userDto.getDepartmentIds());
            }
        }else {
            if (teacherVo.getSchoolId() != null && teacherVo.getSchoolId() > 0){
                teacherInfoQueryWrapper.eq("school_id",teacherVo.getSchoolId());
            }else {
                teacherInfoQueryWrapper.in("school_id", userDto.getTeacherInfo().getSchoolId());
            }
            if (teacherVo.getDepartmentId() != null && teacherVo.getDepartmentId() > 0){
                teacherInfoQueryWrapper.eq("department_id",teacherVo.getDepartmentId());
            }else if (teacherVo.getDepartmentId() != null && teacherVo.getDepartmentId() < 0){
                teacherInfoQueryWrapper.isNull("department_id");
            }else {
                teacherInfoQueryWrapper.in("department_id", userDto.getTeacherInfo().getDepartmentId());
            }
        }

        //若有传关键字则筛选
        if (!StringUtils.isEmpty(teacherVo.getKey())) {
            teacherInfoQueryWrapper.like("teacher_name", teacherVo.getKey()).or().like("department_name", teacherVo.getKey()).eq("deleted", 0);
            //筛选删除状态
            teacherInfoQueryWrapper.eq("deleted",0);
            //若有传在职离职状态则筛选
            if (teacherVo.getWorkStatus() != null){
                teacherInfoQueryWrapper.eq("work_status",teacherVo.getWorkStatus());
            }
            //超管根据传进来的值筛选
            if (userDto.getUserRoleDto().getRoleCode().equals(AuthRole.ADMIN)){
                if (teacherVo.getSchoolId()!=null){
                    teacherInfoQueryWrapper.eq("school_id",teacherVo.getSchoolId());
                }
                if (teacherVo.getDepartmentId()!=null){
                    teacherInfoQueryWrapper.eq("department_id",teacherVo.getDepartmentId());
                }
            }
            //校管理关联学校
            if (userDto.getUserRoleDto().getRoleCode().equals(AuthRole.SCHOOL_ADMIN)){
                teacherInfoQueryWrapper.in("school_id",userDto.getSchoolIds());
                if (teacherVo.getDepartmentId()!=null){
                    teacherInfoQueryWrapper.eq("department_id",teacherVo.getDepartmentId());
                }
            }
            //二级学院管理员关联二级学院
            if (userDto.getUserRoleDto().getRoleCode().equals(AuthRole.DEPARTMENT_ADMIN)){
                List<Integer> departmentIds = userDto.getDepartmentIds();
                if (departmentInfo!=null){
                    departmentIds.add(departmentInfo.getId());
                }
                teacherInfoQueryWrapper.in("department_id",departmentIds);
            }

        }
        return teacherInfoQueryWrapper;
    }
    @Override
    public TeacherInfo addNewTeacher(UserDto userDto, String teacherName, String workNumber) {
        Integer createBy = userDto.getId();
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        //关联信息
        Integer schoolId = null;
        Integer departmentId = null;
        String departmentName = null;
        String schoolName = null;

        if(roleCode.equals(AuthRole.SCHOOL_ADMIN)){
            List<Integer> schoolIds = userDto.getSchoolIds();
            if (schoolIds!=null && schoolIds.size()>0){
                SchoolInfo schoolInfo = schoolInfoMapper.selectById(schoolIds.get(0));
                schoolId = schoolInfo.getId();
                schoolName = schoolInfo.getSchoolName();
            }

        }
        //如果是二级学院管理、则关联本二级学院
        if (roleCode.equals(AuthRole.DEPARTMENT_ADMIN)){
            List<Integer> departmentIds = userDto.getDepartmentIds();
            if (departmentIds!=null && departmentIds.size()>0){
                DepartmentInfo departmentInfo = departmentInfoMapper.selectById(departmentIds.get(0));
                if (departmentInfo != null){
                    departmentId = departmentInfo.getId();
                    departmentName = departmentInfo.getDepartmentName();
                    schoolId = departmentInfo.getSchoolId();
                    SchoolInfo schoolInfo = schoolInfoMapper.selectById(schoolId);
                    schoolName = schoolInfo.getSchoolName();
                }
            }
        }

        /*判断账号是否符合要求*/
        QueryWrapper<UserAccount> accountQueryWrapper = new QueryWrapper<>();
        accountQueryWrapper.eq("account_number", workNumber).eq("deleted", BaseVo.UNDELETE);
        //查询是否有重复账号
        int count = userAccountService.count(accountQueryWrapper);
        //若存在则返回
        if (count > 0) {
            return null;
        }
        /*先添加新账户*/
        User user = new User();
        //用户名默认为教师名
        user.setUserName(teacherName);
        //设置默认密码
        user.setPassword(PasswordCryptoTool.getDefaultPassword(workNumber));
        //创建者
        user.setCreateBy(createBy);
        //设置为教师
        user.setUserType(2);
        //逻辑删除-未删除
        user.setDeleted(0);
        //状态-正常
        user.setStatus(0);
        //插入数据库
        boolean saveUser = userService.save(user);
        //判断是否存储成功
        if (!saveUser) {
            return null;
        }
        Integer userId = user.getId();
        /*添加账户表*/

        UserAccount userAccount = new UserAccount();
        //关联user_id
        userAccount.setUserId(userId);
        //账号为教师工号
        userAccount.setAccountNumber(workNumber);
        //创建者
        userAccount.setCreateBy(createBy);
        //插入数据库
        boolean saveAccount = userAccountService.save(userAccount);
        if (!saveAccount) {
            return null;
        }
        /*添加用户权限*/
        AuthUserRole authUserRole = new AuthUserRole();
        //设置user_id
        authUserRole.setUserId(userId);
        //角色id
        authUserRole.setRoleId(6);
        //创建者
        authUserRole.setCreateBy(createBy);
        //插入数据库
        boolean saveRole = userRoleService.save(authUserRole);
        if (!saveRole) {
            throw new RuntimeException("添加失败");
        }
        /*最后添加教师表*/
        TeacherInfo teacherInfo = new TeacherInfo();
        //判断是否有关联信息
        if (schoolId != null){
            teacherInfo.setSchoolId(schoolId);
        }
        if (departmentId != null){
            teacherInfo.setDepartmentId(departmentId);
        }
        if (departmentName != null){
            teacherInfo.setDepartmentName(departmentName);
        }
        if (schoolName != null){
            teacherInfo.setSchoolName(schoolName);
        }
        //关联用户
        teacherInfo.setUserId(userId);
        //教师姓名
        teacherInfo.setTeacherName(teacherName);
        //工号
        teacherInfo.setWorkNumber(workNumber);
        //是否在职 0-在职 1-离职
        teacherInfo.setWorkStatus(0);
        //创建者
        teacherInfo.setCreateBy(createBy);
        //插入数据库
        boolean save = super.save(teacherInfo);

        return save ? teacherInfo : null;

    }

    /**
     * @param userDto
     * @param teacherVo
     * @Description: 根据学校id删除教师
     * @Author: LZH
     * @Date: 2022/4/27 10:53
     */
    @Override
    public CommonResult<Object> deleteTeacherBySchoolId(UserDto userDto, TeacherVo teacherVo) throws RuntimeException{
        //查询条件
        LambdaQueryWrapper<TeacherInfo> teacherInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherInfoLambdaQueryWrapper.in(TeacherInfo::getSchoolId,teacherVo.getSchoolIdList())
                .eq(TeacherInfo::getDeleted,BaseVo.UNDELETE);
        //获取教师列表
        List<TeacherInfo> list = super.list(teacherInfoLambdaQueryWrapper);
        List<Integer> teacherIds = new ArrayList<>();
        //循环取出id
        list.forEach(info -> teacherIds.add(info.getId()));
        //放入vo
        teacherVo.setTeacherIdList(teacherIds);
        //执行删除
        return this.deleteTeacher(userDto,teacherVo);

    }

    /**
     * @param userDto
     * @param teacherVo
     * @Description: 根据学校id删除教师
     * @Author: LZH
     * @Date: 2022/4/27 10:53
     */
    @Override
    public CommonResult<Object> deleteTeacherByDepartmentId(UserDto userDto, TeacherVo teacherVo) throws RuntimeException{
        //查询条件
        LambdaQueryWrapper<TeacherInfo> teacherInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherInfoLambdaQueryWrapper.in(TeacherInfo::getDepartmentId,teacherVo.getDepartmentIdList())
                .eq(TeacherInfo::getDeleted,BaseVo.UNDELETE);
        //获取教师列表
        List<TeacherInfo> list = super.list(teacherInfoLambdaQueryWrapper);
        List<Integer> teacherIds = new ArrayList<>();
        //循环取出id
        list.forEach(info -> teacherIds.add(info.getId()));
        //放入vo
        teacherVo.setTeacherIdList(teacherIds);

        if (teacherIds.size() <= 0){
            return CommonResult.success("选中学校中不存在教师,无需删除!");
        }

        //执行删除
        return this.deleteTeacher(userDto,teacherVo);
    }
}
