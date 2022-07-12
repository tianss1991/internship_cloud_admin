package com.above.service.impl;

import com.above.bean.excel.StudentInfoExcelData;
import com.above.config.easyExcel.StudentInfoExcelListener;
import com.above.dao.*;
import com.above.dto.UserDto;
import com.above.po.*;
import com.above.service.*;
import com.above.utils.CommonResult;
import com.above.utils.PasswordCryptoTool;
import com.above.utils.RandomUtil;
import com.above.vo.BaseVo;
import com.above.vo.LeaveApplyInfoVo;
import com.above.vo.StudentVo;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <p>
 * 学生信息表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-24
 */
@Slf4j
@Service
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoMapper, StudentInfo> implements StudentInfoService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private SchoolInfoMapper schoolInfoMapper;
    @Autowired
    private DepartmentInfoMapper departmentInfoMapper;
    @Autowired
    private ClassInfoMapper classInfoMapper;
    @Autowired
    private GradeInfoMapper gradeInfoMapper;
    @Autowired
    private MajorInfoMapper majorInfoMapper;
    @Autowired
    private AuthUserRoleService authUserRoleService;

    /**
     * @param userDto 用户信息
     * @param vo 学生vo字段
     * @Description: 增加学生(管理员)
     * @Author: GG
     * @Date: 2022/07/01 15:21
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> addStudent(StudentVo vo, UserDto userDto) {
        //系部，先从vo拿，后续需要从useDto拿
        Integer departmentId = vo.getDepartmentId();
        //学校
        Integer schoolId = vo.getSchoolId();
        //年级
        Integer gradeId = vo.getGradeId();
        //专业
        Integer majorId = vo.getMajorId();
        //班级
        Integer classId = vo.getClassId();
        //学号
        String studentNumber = vo.getStudentNumber();
        //姓名
        String studentName = vo.getStudentName();
        //性别
        Integer gender = vo.getGender();
        //手机号
        String telephone = vo.getTelephone();
        //邮箱
        String mail = vo.getMail();
        //创建人
        Integer createBy = userDto.getId();

        /*判断账号是否符合要求*/

        QueryWrapper<UserAccount> accountQueryWrapper = new QueryWrapper<>();
        accountQueryWrapper.eq("account_number",studentNumber).eq("deleted", BaseVo.UNDELETE);
        //查询是否有重复账号
        int count = userAccountService.count(accountQueryWrapper);
        //若存在则返回
        if (count>0){
            return CommonResult.error(500,"工号重复");
        }
        //判断院校信息是否存在

        SchoolInfo schoolInfo = schoolInfoMapper.selectById(schoolId);
        if (schoolInfo == null){
            return CommonResult.error(500,"学校不存在");
        }
        DepartmentInfo departmentInfo = departmentInfoMapper.selectById(departmentId);
        if (departmentInfo == null){
            return CommonResult.error(500,"系部不存在");
        }
        ClassInfo classInfo = classInfoMapper.selectById(classId);
        if (classInfo == null){
            return CommonResult.error(500,"班级不存在");
        }

        /*先添加新账户*/

        User user = new User();
        //用户名默认为学生名
        user.setUserName(studentNumber);
        //设置默认密码
        user.setPassword(PasswordCryptoTool.getDefaultPassword(studentNumber));
        //创建者
        user.setCreateBy(createBy);
        //设置为学生
        user.setUserType(1);
        //逻辑删除-未删除
        user.setDeleted(0);
        //状态-正常
        user.setStatus(0);
        //插入数据库
        boolean saveUser = userService.save(user);
        try {
            //判断是否存储成功
            if (!saveUser) {
                return CommonResult.error(500, "创建用户信息失败");
            }
            Integer userId = user.getId();
            /*添加账户表*/

            UserAccount userAccount = new UserAccount();
            //关联user_id
            userAccount.setUserId(userId);
            //账号为教师工号
            userAccount.setAccountNumber(studentNumber);
            //创建者
            userAccount.setCreateBy(createBy);
            //插入数据库
            boolean saveAccount = userAccountService.save(userAccount);
            if (!saveAccount){
                throw new RuntimeException("创建账号信息失败");
            }

            //添加权限
            AuthUserRole authUserRole = new AuthUserRole();
            authUserRole.setCreateBy(createBy);
            authUserRole.setUserId(user.getId());
            authUserRole.setRoleId(7);
            //保存
            boolean save = authUserRoleService.save(authUserRole);
            if(!save){
                throw new RuntimeException("创建权限失败");
            }

            //新建对象
            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setSchoolId(schoolId);
            studentInfo.setDepartmentId(departmentId);
            studentInfo.setGradeId(gradeId);
            studentInfo.setMajorId(majorId);
            studentInfo.setClassId(classId);
            //关联用户
            studentInfo.setUserId(userId);
            studentInfo.setStudentNumber(studentNumber);
            studentInfo.setStudentName(studentName);
            studentInfo.setGender(gender);
            studentInfo.setStudyStatus(0);
            studentInfo.setCreateBy(createBy);
            if (telephone != null) {
                //电话
                studentInfo.setTelephone(telephone);
            }
            if (mail != null) {
                //邮箱
                studentInfo.setMail(mail);
            }
            boolean saveStudnet = this.save(studentInfo);
            if (!saveStudnet) {
                return CommonResult.error(500, "管理员操作失败");
            }
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info(e.getMessage());
            return CommonResult.error(500,e.getMessage());
        }
        return CommonResult.success("管理员操作成功");
    }

    /**
     * @param userDto 用户信息
     * @param vo 学生vo字段
     * @Description: 修改学生(管理员)
     * @Author: GG
     * @Date: 2022/07/01 15:41
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> modifyStudent(StudentVo vo, UserDto userDto) {
        //系部，先从vo拿，后续需要从useDto拿
        Integer departmentId = vo.getDepartmentId();
        //学校
        Integer schoolId = vo.getSchoolId();
        //年级
        Integer gradeId = vo.getGradeId();
        //专业
        Integer majorId = vo.getMajorId();
        //班级
        Integer classId = vo.getClassId();
        //学号
        String studentNumber = vo.getStudentNumber();
        //姓名
        String studentName = vo.getStudentName();
        //性别
        Integer gender = vo.getGender();
        //手机号
        String telephone = vo.getTelephone();
        //邮箱
        String mail = vo.getMail();
        //创建人
        Integer createBy = userDto.getId();
        //学生id
        Integer studentId = vo.getStudentId();
        //用于判断是否修改
        boolean flag = false;

        //查找学生是否存在
        StudentInfo studentInfo = this.getById(studentId);
        if (studentInfo == null){
            return CommonResult.error(500,"学生不存在");
        }
        try{
            //若学生姓名参数不为空则判断是否修改
            if (studentName != null){
                //判断是否修改
                if (!studentInfo.getStudentNumber().equals(studentName)){
                    /*修改姓名，同时修改用户信息*/

                    //获取用户信息
                    User user = userService.getById(studentInfo.getUserId());
                    //设置用户名
                    user.setUserName(studentName);
                    //更新人
                    user.setUpdateBy(createBy);
                    //更新数据库
                    boolean b = userService.updateById(user);
                    //若成功则修改状态，并把新工号放入teacherInfo
                    if (b){
                        studentInfo.setStudentName(studentName);
                        flag=true;
                    }else {
                        throw new RuntimeException("修改账号失败");
                    }

                }
            }

            //判断邮箱是否为空
            if (mail != null){
                //判断是否修改
                if (!studentInfo.getMail().equals(mail)){
                    /*修改性别*/

                    studentInfo.setMail(mail);
                    flag=true;
                }
            }

            //判断电话是否为空
            if (telephone != null){
                //判断是否修改
                if (!studentInfo.getTelephone().equals(telephone)){
                    /*修改性别*/

                    studentInfo.setTelephone(telephone);
                    flag=true;
                }
            }


            //若学号参数不为空则判断是否修改
            if (studentNumber != null){
                //判断是否修改
                if (!studentInfo.getStudentNumber().equals(studentNumber)){

                    /*修改学号，同时修改账户信息*/

                    //判断学号是否符合要求
                    QueryWrapper<UserAccount> accountQueryWrapper = new QueryWrapper<>();
                    accountQueryWrapper.eq("account_number",studentNumber).eq("deleted",BaseVo.UNDELETE);
                    int count = userAccountService.count(accountQueryWrapper);
                    if (count>0){
                        return CommonResult.error(500,"学号重复");
                    }

                    //根据user_id获取账户信息
                    QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("user_id",studentInfo.getUserId());
                    List<UserAccount> list = userAccountService.list(queryWrapper);
                    if (list == null && list.size() <= 0){
                        return CommonResult.error(500,"账号信息不存在");
                    }
                    UserAccount one = list.get(0);
                    //设置账号
                    one.setAccountNumber(studentNumber);
                    //设置更新人
                    one.setUpdateBy(createBy);
                    //插入数据库
                    boolean b = userAccountService.updateById(one);

                    //若成功则修改状态，并把新工号放入teacherInfo
                    if (b){
                        studentInfo.setStudentNumber(studentNumber);
                        flag=true;
                    }else {
                        throw new RuntimeException("修改账号失败");
                    }
                }
            }
            //若学生性别参数不为空则判断是否修改
            if (gender != null){
                //判断是否修改
                if (!studentInfo.getGender().equals(gender)){
                    /*修改性别*/

                    studentInfo.setGender(gender);
                    flag=true;
                }
            }

            /*判断是否修改院校信息
             * 删除关联信息后面加
             * */
            if (schoolId != null){
                //判断是否修改
                if (!studentInfo.getSchoolId().equals(schoolId)){
                    /*修改学校*/
                    studentInfo.setSchoolId(schoolId);
                    flag=true;
                }
            }
            if (departmentId != null){
                //判断是否修改
                if (!studentInfo.getDepartmentId().equals(departmentId)){
                    /*修改系部*/
                    studentInfo.setDepartmentId(departmentId);
                    flag=true;
                }
            }
            if (classId != null){
                //判断是否修改
                if (!studentInfo.getClassId().equals(classId)){
                    /*修改班级*/
                    studentInfo.setClassId(classId);
                    flag=true;
                }
            }

            /*根据flag判断是否需要修改*/
            if (flag){
                //设置更新人
                studentInfo.setUpdateBy(createBy);
                //插入数据库
                boolean b = super.updateById(studentInfo);
                //根据更新是否成功返回
                if (b){
                    return CommonResult.success("修改成功",null);
                }else {
                    throw new RuntimeException("修改失败");
                }
            }else {
                return CommonResult.error(500,"无需修改");
            }
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info(e.getMessage());
            return CommonResult.error(500,e.getMessage());
        }

    }

    /**
     * @param userDto 用户信息
     * @param vo 学生vo字段
     * @Description: 删除学生(管理员)
     * @Author: GG
     * @Date: 2022/07/01 15:51
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> deleteStudent(StudentVo vo, UserDto userDto) {
        try{
            //获取参数
            List<Integer> studentIdList = vo.getStudentIds();
            Integer updateBy = userDto.getId();
            //批量查询
            Collection<StudentInfo> studentInfos = super.getBaseMapper().selectBatchIds(studentIdList);
            //判断是否全部都有查出
            if (studentIdList.size() > studentInfos.size()){
                return CommonResult.error(500,"有学生已被删除");
            }
            //循环修改为删除状态
            for (StudentInfo info:studentInfos) {
                // 查看该学生是否状态为删除
                if (info.getDeleted()==1) {
                    return CommonResult.error(500, "【" + info.getStudentNumber() + "】该学生已删除");
                }
                //设置为删除状态
                info.setDeleted(1);
                //设置更新人
                info.setUpdateBy(updateBy);
            }
            //修改教职工信息
            boolean i = super.updateBatchById(studentInfos);
            //若修改后进入try-catch中，

            if (i){
                //设置批量删除数组
                Collection<User> users = new ArrayList<>();
                Collection<UserAccount> userAccounts = new ArrayList<>();
                Collection<AuthUserRole> userRoles = new ArrayList<>();

                //循环删除学生信息
                for (StudentInfo info:studentInfos) {
                    //用户id
                    Integer userId = info.getUserId();
                    //学生id
                    Integer studentId = info.getId();
                    /*设置学生账号为删除状态*/
                    //根据user_id获取
                    User user = userService.getById(userId);
                    if (user != null){
                        user.setUpdateBy(updateBy);
                        //设置删除状态
                        user.setDeleted(User.DELETED);
                        //添加到批量删除数组
                        users.add(user);
                    }

                    //用户账户信息表
                    QueryWrapper<UserAccount> accountQueryWrapper = new QueryWrapper<>();
                    accountQueryWrapper.eq("user_id",userId).eq("deleted",0);
                    UserAccount userAccount = userAccountService.getOne(accountQueryWrapper);
                    if (userAccount != null){
                        //修改状态
                        userAccount.setDeleted(1);
                        userAccount.setUpdateBy(updateBy);
                        //添加到批量删除数组
                        userAccounts.add(userAccount);
                    }
                    /*删除权限*/
                    QueryWrapper<AuthUserRole> authUserRoleQueryWrapper = new QueryWrapper<>();
                    authUserRoleQueryWrapper.eq("user_id",userId).eq("deleted",0);
                    AuthUserRole userRole = authUserRoleService.getOne(authUserRoleQueryWrapper);
                    if (userAccount != null){
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
                if (userRoles.size()>0) {
                    boolean updateClass = authUserRoleService.updateBatchById(userRoles);
                    if (!updateClass) {
                        throw new RuntimeException("删除用户异常");
                    }
                }
                //返回
                return CommonResult.success("删除成功",null);
            }else {
                throw new RuntimeException("删除用户异常");
            }
        }catch (Exception e){
            log.info(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CommonResult.error(500,e.getMessage());
        }
    }

//    /**
//     * @param userDto 用户信息
//     * @param vo 学生vo字段
//     * @Description: 批量删除学生(管理员)
//     * @Author: GG
//     * @Date: 2022/07/01 15:51
//     */
//    @Override
//    public CommonResult<Object> deleteStudentByIds(StudentVo vo, UserDto userDto) {
//
//    }


    /**
     * @param userDto 用户信息
     * @param vo 学生vo字段
     * @Description: 显示学生列表(分页)(管理员)
     * @Author: GG
     * @Date: 2022/07/01 16:51
     */
    @Override
    public CommonResult<Object> displayStudentListPage(StudentVo vo, UserDto userDto) {
        //分页参数
        Page<StudentInfo> studentInfoPage=new Page<>(vo.getPage(),vo.getSize());
        //年级
        Integer gradeId = vo.getGradeId();
        //专业
        Integer majorId = vo.getMajorId();
        //搜索词
        String key = vo.getKey();
        //角色
        String roleCode = userDto.getUserRoleDto().getRoleCode();

        QueryWrapper<StudentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",BaseVo.UNDELETE);

        if(roleCode.equals(AuthRole.SCHOOL_ADMIN)){
            queryWrapper.eq("school_id",userDto.getSchoolIds());
        }
        if(roleCode.equals(AuthRole.DEPARTMENT_ADMIN)){
            queryWrapper.eq("school_id",userDto.getTeacherInfo().getSchoolId());
            queryWrapper.eq("department_id",userDto.getDepartmentIds());
        }
        if(gradeId != null){
            queryWrapper.eq("grade_id",gradeId);
        }
        if(majorId != null){
            queryWrapper.eq("major_id",majorId);
        }
        if(key != null){
            queryWrapper.like("name",key);
        }

        //获取list
        IPage<StudentInfo> iPage=this.page(studentInfoPage,queryWrapper);
        //放入返回map,定义初始化容量为16
        Map<String,Object> returnMap=new HashMap<>(16);
        //得到的所有数据都放到list键中
        returnMap.put(BaseVo.LIST,iPage.getRecords());
        //得到的分出来的页数放到page键中
        returnMap.put(BaseVo.PAGE,iPage.getPages());
        //得到的查询总数放到total键中
        returnMap.put(BaseVo.TOTAL,iPage.getTotal());
        //把数据返回给前端
        return CommonResult.success(returnMap);
    }

    /**
     * @param userDto 用户信息
     * @param vo 学生vo字段
     * @Description: 显示学生列表(不分页)(管理员)
     * @Author: GG
     * @Date: 2022/07/01 17:11
     */
    @Override
    public CommonResult<Object> displayStudentListNoPage(StudentVo vo, UserDto userDto) {
        //年级
        Integer gradeId = vo.getGradeId();
        //专业
        Integer majorId = vo.getMajorId();
        //搜索词
        String key = vo.getKey();
        //角色
        String roleCode = userDto.getUserRoleDto().getRoleCode();

        QueryWrapper<StudentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",BaseVo.UNDELETE);

        if(roleCode.equals(AuthRole.SCHOOL_ADMIN)){
            queryWrapper.eq("school_id",userDto.getSchoolIds());
        }
        if(roleCode.equals(AuthRole.DEPARTMENT_ADMIN)){
            queryWrapper.eq("school_id",userDto.getTeacherInfo().getSchoolId());
            queryWrapper.eq("department_id",userDto.getDepartmentIds());
        }
        if(gradeId != null){
            queryWrapper.eq("grade_id",gradeId);
        }
        if(majorId != null){
            queryWrapper.eq("major_id",majorId);
        }
        if(key != null){
            queryWrapper.like("name",key);
        }

        List<StudentInfo> list = this.list(queryWrapper);

        //放入返回map,定义初始化容量为16
        Map<String,Object> returnMap=new HashMap<>(16);
        //得到的所有数据都放到list键中
        if(list.size()>0){
            returnMap.put(BaseVo.LIST,list);
        }
        //把数据返回给前端
        return CommonResult.success(returnMap);
    }

    /**
     * @param userDto 用户信息
     * @param file 导入文件
     * @Description: 导入学生信息
     * @Author: GG
     * @Date: 2022/07/04 08:34
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> importStudentInfo(UserDto userDto, MultipartFile file) {
        return null;
//        String roleCode = userDto.getUserRoleDto().getRoleCode();
//        //获取通用参数
//        Integer createBy = userDto.getId();
//        //获取文件名
//        file.getOriginalFilename();
//        try {
//            //获取文件流
//            InputStream inputStream = file.getInputStream();
//            //实例化实现了AnalysisEventListener接口的类
//            StudentInfoExcelListener listener = new StudentInfoExcelListener();
//            ExcelReader build = EasyExcel.read(inputStream, StudentInfoExcelData.class, listener).build();
//            ReadSheet readSheet = EasyExcel.readSheet(0).build();
//            build.read(readSheet);
//            //获取数据
//            List<Object> list = listener.getDatas();
//
//            //批量添加数组
//            Collection<User> users = new ArrayList<>();
//            Collection<UserAccount> userAccounts = new ArrayList<>();
//            Collection<StudentInfo> studentInfos = new ArrayList<>();
//            Collection<AuthUserRole> userRoles = new ArrayList<>();
//
//            //学校关联信息
//            Integer schoolIdDefualt = null;
//            String schoolNameDefualt = null;
//            Integer departmentIdDefualt = null;
//            String departmentNameDefualt = null;
//
//            //根据不同角色获取绑定的角色
//            if (roleCode.equals(AuthRole.SCHOOL_ADMIN)){
//                if (userDto.getSchoolIds() == null || userDto.getSchoolIds().size() <= 0){
//                    return CommonResult.error(500,"绑定学校id为空");
//                }
//                schoolIdDefualt = userDto.getSchoolIds().get(0);
//                schoolNameDefualt = schoolInfoMapper.selectById(schoolIdDefualt).getSchoolName();
//            }else if (roleCode.equals(AuthRole.DEPARTMENT_ADMIN)){
//                if (userDto.getDepartmentIds() == null || userDto.getDepartmentIds().size() <= 0){
//                    return CommonResult.error(500,"绑定系部id为空");
//                }
//                departmentIdDefualt = userDto.getDepartmentIds().get(0);
//                DepartmentInfo departmentInfo = departmentInfoMapper.selectById(departmentIdDefualt);
//                departmentNameDefualt = departmentInfo.getDepartmentName();
//                schoolIdDefualt = departmentInfo.getSchoolId();
//                schoolNameDefualt = schoolInfoMapper.selectById(schoolIdDefualt).getSchoolName();
//            }
//
//            //判断是否存在数据
//            if (list.size()>0) {
//                //学校列表
//                QueryWrapper<SchoolInfo> schoolInfoQueryWrapper = new QueryWrapper<>();
//                schoolInfoQueryWrapper.eq("deleted", BaseVo.UNDELETE);
//                List<SchoolInfo> schoolInfos = schoolInfoMapper.selectList(schoolInfoQueryWrapper);
//                //系部列表
//                QueryWrapper<DepartmentInfo> departmentQueryWrapper = new QueryWrapper<>();
//                departmentQueryWrapper.eq("deleted", BaseVo.UNDELETE);
//                List<DepartmentInfo> departmentInfos = departmentInfoMapper.selectList(departmentQueryWrapper);
//                //班级列表
//                QueryWrapper<ClassInfo> classQueryWrapper = new QueryWrapper<>();
//                classQueryWrapper.eq("deleted", BaseVo.UNDELETE);
//                List<ClassInfo> classInfos = classInfoMapper.selectList(classQueryWrapper);
//
//                //循环导入
//                for(Object o:list){
//                    StudentInfoExcelData data = (StudentInfoExcelData) o;
//                    //获取参数
//                    String studentNumber = data.getStudentNumber();
//                    String name = data.getStudentName();
//
//                    //查询工号是否符合
//                    QueryWrapper<UserAccount> accountQueryWrapper = new QueryWrapper<>();
//                    accountQueryWrapper.eq("account_number",studentNumber).eq("deleted",BaseVo.UNDELETE);
//                    //查询是否有重复账号
//                    int count = userAccountService.count(accountQueryWrapper);
//                    //若存在则返回
//                    if (count>0){
//                        return CommonResult.error(500,name+"添加失败，该学生工【"+studentNumber+"】工号重复");
//                    }
//                    //获取参数
//                    String schoolName = data.getSchoolName();
//                    String departmentName = data.getDepartmentName();
//                    String className = data.getClassName();
//                    Integer schoolId = null;
//                    Integer departmentId = null;
//
//                    Integer classId = null;
//                    //循环查出院校信息，找到跳出
//                    if (schoolIdDefualt != null){
//                        if (!schoolName.equals(schoolNameDefualt)) {
//                            return CommonResult.error(500,"请添加自己学校的学生");
//                        }
//                        schoolId = schoolIdDefualt;
//                    }else {
//                        for (SchoolInfo info:schoolInfos) {
//                            if (info.getSchoolName().equals(schoolName)){
//                                schoolId = info.getId();
//                                break;
//                            }
//                        }
//                    }
//                    //判断是否有关联学校
//                    if (schoolId==null){
//                        return CommonResult.error(500,"请检查学生【"+name+"】的学校信息是否存在");
//                    }
//                    if (departmentIdDefualt!=null){
//                        if (!departmentName.equals(departmentNameDefualt)) {
//                            return CommonResult.error(500,"请添加自己系部的学生");
//                        }
//                        departmentId = departmentIdDefualt;
//                    }else {
//                        for (DepartmentInfo info:departmentInfos) {
//                            if (info.getSchoolId().equals(schoolId)){
//                                if (info.getDepartmentName().equals(departmentName)){
//                                    departmentId= info.getId();
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                    if (departmentId==null){
//                        return CommonResult.error(500,"请检查学生【"+name+"】的系部信息是否存在");
//                    }
//                    for (ClassInfo info:classInfos) {
//                        if (info.getDepartmentId().equals(departmentId)){
//                            if (info.getClassName().equals(className)){
//                                classId= info.getId();
//                                break;
//                            }
//                        }
//                    }
//                    if (classId==null){
//                        return CommonResult.error(500,"请检查学生【"+name+"】的班级信息是否存在");
//                    }
//                    /*先循环创建user*/
//
//                    //用户唯一码
//                    String userCode = RandomUtil.randomUUID(16);
//
//                    User user = new User();
//                    //设置用户唯一码
//                    user.setUserCode(userCode);
//                    //用户名默认为教师名
//                    user.setUserName(name);
//                    //设置默认密码
//                    user.setPassword(PasswordCryptoTool.getDefaultPassword(studentNumber));
//                    //创建者
//                    user.setCreateBy(createBy);
//                    //设置为教师
//                    user.setUserType(1);
//                    //逻辑删除-未删除
//                    user.setDeleted(0);
//                    //状态-正常
//                    user.setStatus(0);
//                    //放入user数组
//                    users.add(user);
//                    /*添加账户表*/
//                    UserAccount userAccount = new UserAccount();
//                    //用户唯一码
//                    userAccount.setUserCode(userCode);
//                    //账号为教师工号
//                    userAccount.setAccountNumber(studentNumber);
//                    //创建者
//                    userAccount.setCreateBy(createBy);
//                    //放入数组
//                    userAccounts.add(userAccount);
//
//                }
//                //判断是否有用户
//                if (users.size()==0){
//                    return CommonResult.error(500,"添加用户异常");
//                }
//                if (users.size()!=list.size()){
//                    return CommonResult.error(500,"添加用户异常");
//                }
//                //循环结束后批量添加
//                boolean saveBatch = userService.saveBatch(users,1000);
//                //判断是否存储成功
//                if (!saveBatch){
//                    return CommonResult.error(500,"创建用户信息失败");
//                }
//                /*成功后进入第二次循环*/
//                for (UserAccount account:userAccounts) {
//                    //循环user
//                    for (User user:users) {
//                        //判断用户唯一码是否相同
//                        if (account.getUserCode().equals(user.getUserCode())){
//                            //取出userId
//                            Integer userId = user.getId();
//                            //放入关联
//                            account.setUserId(userId);
//                            /*添加用户权限*/
//                            AuthUserRole authUserRole = new AuthUserRole();
//                            //用户id
//                            authUserRole.setUserId(userId);
//                            //角色id
//                            authUserRole.setRoleId(7);
//                            //创建者
//                            authUserRole.setCreateBy(createBy);
//                            //放入批量插入数组
//                            userRoles.add(authUserRole);
//                            //循环excel集合
//                            for (Object o:list) {
//                                //强转
//                                StudentInfoExcelData data = (StudentInfoExcelData) o;
//                                //如果学号不同则添加信息
//                                if (data.getStudentNum().equals(account.getAccountNumber())){
//                                    //获取参数
//                                    String schoolName = data.getSchoolName();
//                                    String departmentName = data.getDepartmentName();
//                                    String className = data.getClassName();
//                                    //学校关联信息
//                                    Integer schoolId = null;
//                                    Integer departmentId = null;
//                                    Integer classId = null;
//                                    //循环查出院校信息，找到跳出
//                                    if (schoolIdDefualt != null){
//                                        schoolId =schoolIdDefualt;
//                                    }else {
//                                        for (SchoolInfo info:schoolInfos) {
//                                            if (info.getSchoolName().equals(schoolName)){
//                                                schoolId = info.getId();
//                                                break;
//                                            }
//                                        }
//                                    }
//                                    if (departmentIdDefualt!=null){
//                                        departmentId = departmentIdDefualt;
//                                    }else {
//                                        for (DepartmentInfo info:departmentInfos) {
//                                            if (info.getSchoolId().equals(schoolId)){
//                                                if (info.getDepartmentName().equals(departmentName)){
//                                                    departmentId= info.getId();
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    }
//                                    for (ClassInfo info:classInfos) {
//                                        if (info.getDepartmentId().equals(departmentId)){
//                                            if (info.getClassName().equals(className)){
//                                                classId= info.getId();
//                                                break;
//                                            }
//                                        }
//                                    }
//                                    if (classId==null){
//                                        throw new RuntimeException("班级信息异常");
//                                    }
//
//                                    //获取参数
//                                    String name = data.getStudentName();
//                                    //工号
//                                    String studentNumber = data.getStudentNum();
//                                    //性别
//                                    String s = data.getGander();
//                                    int gender = 0;
//                                    if ("男".equals(s)){
//                                        gender=1;
//                                    }
//                                    if ("女".equals(s)){
//                                        gender=2;
//                                    }
//                                    //专业
//                                    String majorCode = data.getMajorCode();
//                                    String majorName = data.getMajor();
//                                    Integer majorId = null;
//                                    //查询出专业，没有的话就添加
//                                    QueryWrapper<MajorInfo> majorInfoQueryWrapper = new QueryWrapper<>();
//                                    majorInfoQueryWrapper.eq("major_code",majorCode)
//                                            .eq("major_name",majorName)
//                                            .eq("deleted",0);
//                                    MajorInfo one = majorInfoService.getOne(majorInfoQueryWrapper);
//                                    //判断该专业是否存在
//                                    if (StringUtils.isEmpty(one)){
//                                        //新建专业
//                                        MajorInfo majorInfo = new MajorInfo();
//                                        majorInfo.setDepartmentId(departmentId);
//                                        majorInfo.setMajorCode(majorCode);
//                                        majorInfo.setMajorName(majorName);
//                                        majorInfo.setCreateBy(createBy);
//                                        //获取专业
//                                        boolean save = majorInfoService.save(majorInfo);
//                                        if (!save){
//                                            throw new RuntimeException("专业新增失败");
//                                        }
//                                        //取出id
//                                        majorId = majorInfo.getId();
//
//                                    }else {
//                                        majorId = one.getId();
//                                    }
//                                    //校区
//                                    String campus = data.getCampus();
//                                    //年级
//                                    String grade = data.getGrade();
//                                    //学制
//                                    String educationalSystem = data.getEducationalSystem();
//                                    //出生年月
//                                    String birth = data.getBirth();
//                                    //证件号
//                                    String idCardNum = data.getIdCardNum();
//                                    //民族
//                                    String nation = data.getNation();
//                                    //政治面貌
//                                    String politicalAffiliation = data.getPoliticalAffiliation();
//                                    //学生电话
//                                    String phone = data.getPhone();
//
//                                    /*最后添加学生表*/
//
//                                    StudentInfo studentInfo = new StudentInfo();
//                                    //关联院校信息
//                                    studentInfo.setSchoolId(schoolId);
//                                    studentInfo.setDepartmentId(departmentId);
//                                    studentInfo.setClassId(classId);
//                                    //学生姓名
//                                    studentInfo.setName(name);
//                                    //工号
//                                    studentInfo.setStudentNumber(studentNumber);
//                                    //性别
//                                    studentInfo.setGender(gender);
//                                    //创建者
//                                    studentInfo.setCreateBy(createBy);
//                                    //学生状态
//                                    studentInfo.setStudyStatus(0);
//                                    //用户id
//                                    studentInfo.setUserId(userId);
//                                    //专业
//                                    studentInfo.setMajorId(majorId);
//                                    //校区
//                                    studentInfo.setCampus(getCompus(campus));
//                                    //年级
//                                    studentInfo.setGrade(grade);
//                                    //学制
//                                    studentInfo.setEducationalSystem(educationalSystem);
//                                    //出生年月
//                                    studentInfo.setBirthDate(birth);
//                                    //证件号
//                                    studentInfo.setIdCardNumber(idCardNum);
//                                    //民族
//                                    studentInfo.setNation(nation);
//                                    //政治面貌
//                                    if (politicalAffiliation != null){
//                                        studentInfo.setPoliticalAffiliation(getPolitical(politicalAffiliation ));
//                                    }
//                                    //学生电话
//                                    studentInfo.setTelephone(phone);
//
//                                    //放入集合
//                                    studentInfos.add(studentInfo);
//                                    break;
//                                }
//                            }
//                            break;
//                        }
//                    }
//
//                }
//                /*统一批量插入*/
//
//                //账户信息
//                if (userAccounts.size()>0){
//                    boolean saveAccount = userAccountService.saveBatch(userAccounts,1000);
//                    if (!saveAccount){
//                        throw new RuntimeException("导入账号信息失败");
//                    }
//                }
//                //用户角色
//                if (userRoles.size()>0){
//                    boolean saveRole = userRoleService.saveBatch(userRoles,1000);
//                    if (!saveRole){
//                        throw new RuntimeException("导入角色信息失败");
//                    }
//                }
//                //教师信息
//                if (studentInfos.size()>0){
//                    boolean saveTeacher = this.saveBatch(studentInfos,1000);
//                    if (!saveTeacher){
//                        throw new RuntimeException("导入学生信息失败");
//                    }
//                }
//
//                return CommonResult.success("导入成功",null);
//
//            }else {
//                return CommonResult.error(500,"没有数据");
//            }
//
//        }catch (IOException e){
//            return CommonResult.error(500,e.getMessage());
//        }catch (RuntimeException e){
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            log.info(e.getMessage());
//            return CommonResult.error(500,e.getMessage());
//        }
    }
}
