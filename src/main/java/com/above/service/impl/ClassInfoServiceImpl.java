package com.above.service.impl;

import com.above.bean.excel.SchoolInfoExcelData;
import com.above.bean.theory.ClassInfoDto;
import com.above.config.easyExcel.SchoolInfoExcelListener;
import com.above.dao.ClassInfoMapper;
import com.above.dao.DepartmentInfoMapper;
import com.above.dao.SchoolInfoMapper;
import com.above.dao.TeacherInfoMapper;
import com.above.dto.ClassDto;
import com.above.dto.DepartmentWithClassDto;
import com.above.dto.UserDto;
import com.above.po.*;
import com.above.service.*;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.ClassVo;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <p>
 * 班级 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Slf4j
@Service
public class ClassInfoServiceImpl extends ServiceImpl<ClassInfoMapper, ClassInfo> implements ClassInfoService {

    @Autowired
    private SchoolInfoMapper schoolInfoMapper;
    @Autowired
    private DepartmentInfoMapper departmentInfoMapper;
    @Autowired
    private ClassInfoMapper classInfoMapper;
    @Autowired
    private AuthUserRoleService authUserRoleService;
    @Autowired
    private ClassTeacherRelationService classTeacherRelationService;
    @Autowired
    private TeacherInfoMapper teacherInfoMapper;
    @Autowired
    private TeacherInfoService teacherInfoService;
    @Autowired
    private MajorInfoService majorInfoService;
    @Autowired
    private GradeInfoService gradeInfoService;

    /**
     * 添加班级
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public CommonResult<Object> addClass(UserDto userDto, ClassVo classVo) {

        //获取参数
        Integer createBy = userDto.getId();
        Integer schoolId = classVo.getSchoolId();
        Integer departmentId = classVo.getDepartmentId();

        //判断学校是否存在
        SchoolInfo schoolInfo = schoolInfoMapper.selectById(schoolId);
        if (schoolInfo == null) {
            return CommonResult.error(500, "学校不存在");
        }
        //判断二级学院是否存在
        DepartmentInfo departmentInfo = departmentInfoMapper.selectById(departmentId);
        if (departmentInfo == null) {
            return CommonResult.error(500, "二级学院不存在");
        }

        //判断班级名称是否重复
        QueryWrapper<ClassInfo> classInfoQueryWrapper = new QueryWrapper<>();
        classInfoQueryWrapper.eq("department_id", departmentId)
                .eq("class_name", classVo.getClassName())
                .eq("school_id", schoolId).eq("deleted", BaseVo.UNDELETE);
        //why this place is super
        int count = super.count(classInfoQueryWrapper);
        if (count > 0) {
            return CommonResult.error(500, "名称重复");
        }
        //新建班级实体类
        ClassInfo classInfo = new ClassInfo();
        //班级名称
        classInfo.setClassName(classVo.getClassName());
        //创建人
        classInfo.setCreateBy(createBy);
        //关联学校id
        classInfo.setSchoolId(schoolId);
        //关联二级学院
        classInfo.setDepartmentId(departmentId);
        /*关联专业和年级*/
        classInfo.setGradeId(classVo.getGradeId());
        classInfo.setMajorId(classVo.getMajorId());
        //插入数据库
        boolean save = this.save(classInfo);
        Integer classInfoId = classInfo.getId();
        try {
            //添加成功则增加校领导信息
            if (save) {
                List<ClassTeacherRelation> teacherRelations = new ArrayList<>();
                //判断是否需要添加辅导员
                if (classVo.getTeacherIdList() != null && classVo.getTeacherIdList().size() > 0){

                }
                if (!StringUtils.isEmpty(classVo.getClassTeacherName() )){
                    ClassTeacherRelation relation = new ClassTeacherRelation();
                    relation.setRelationType(2);
                    relation.setClassId(classInfoId).setDeleted(BaseVo.UNDELETE);
                    teacherRelations.add(relation);
                }

                //添加成功返回
                return CommonResult.success("添加成功", null);
            } else {
                return CommonResult.error(500, "添加二级学院失败");
            }
        } catch (RuntimeException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("异常打印-->"+e.getMessage());
            return CommonResult.error(500, e.getMessage());
        }
    }

    /**
     * 修改班级
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public CommonResult<Object> modifyClass(UserDto userDto, ClassVo classVo) {
        //获取参数
        boolean flag = false;
        Integer updateBy = userDto.getId();
        Integer classId = classVo.getClassId();
        String className = classVo.getClassName();

        ClassInfo classInfo = super.getById(classId);
        //判断二级学院是否存在
        if (classInfo == null) {
            return CommonResult.error(500, "该班级不存在");
        }
        if (classInfo.getDeleted() == 1) {
            return CommonResult.error(500, "该班级已删除");
        }
        //若班级名称与原名称不一致则修改
        if (!StringUtils.isBlank(className)){
            if (!classInfo.getClassName().equals(className)) {
                //设置新班级名称
                classInfo.setClassName(className);
                //修改flag状态
                flag = true;
            }
        }
        //判断是否有执行更新
        if (flag) {
            classInfo.setUpdateBy(updateBy);
            //更新数据库
            boolean b = super.updateById(classInfo);
            if (b) {
                return CommonResult.success("修改成功", null);
            } else {
                throw new RuntimeException("修改失败,请重试");
            }

        } else {
            //没操作更新直接返回
            return CommonResult.success("无需修改", null);
        }

    }

    /**
     * 删除班级
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public CommonResult<Object> deleteClass(UserDto userDto, ClassVo classVo) {

        //获取参数
        Collection<Integer> classIdList = classVo.getClassIdList();
        Integer updateBy = userDto.getId();

        if (classIdList.isEmpty()) {
            return CommonResult.error(500, "缺少二级学院id");
        }
        //批量查询
        List<ClassInfo> classInfos = this.getBaseMapper().selectBatchIds(classIdList);

        //循环设置删除状态和更新人
        for (ClassInfo info : classInfos) {
            if (info.getDeleted().equals(BaseVo.DELETE)) {
                return CommonResult.error(500, "请勿重复删除");
            }
            info.setUpdateBy(updateBy);
            info.setDeleted(1);
        }
        //批量删除
        boolean i = this.updateBatchById(classInfos);

        try {
            //删除成功后，删除相关信息
            if (i) {
                log.info("删除成功");
                return CommonResult.success("删除成功", null);
            } else {
                throw new RuntimeException("删除失败");
            }
        } catch (RuntimeException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("异常打印-->"+e.getMessage());
            return CommonResult.error(500, e.getMessage());
        }
    }

    /**
     * 获取班级列表（分页）
     */
    @Override
    public CommonResult<Object> getClassPageList(UserDto userDto,ClassVo classVo) {
        Integer userType=userDto.getUserType();
        Integer departmentId=0;
        String roleCode= userDto.getUserRoleDto().getRoleCode();


        //设置分页参数
        Page<ClassInfo> page = new Page<>(classVo.getPage(), classVo.getSize());
        //设置查找状态正常的学校
        //设置查找状态正常的学校
        QueryWrapper<ClassInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", BaseVo.UNDELETE);

        if (classVo.getSchoolId() != null && classVo.getSchoolId() > 0){
            queryWrapper.eq("school_id", classVo.getSchoolId());
        }
        if (classVo.getDepartmentId() != null && classVo.getDepartmentId() > 0){
            queryWrapper.eq("department_id",classVo.getDepartmentId());
        }

        //若有传key则筛选
        if (!StringUtils.isEmpty(classVo.getKey())) {
            queryWrapper.like("class_name", classVo.getKey());
        }
        IPage<ClassInfo> iPage = super.page(page, queryWrapper);
        //获取分页集合
        List<ClassInfo> departmentInfos = iPage.getRecords();

        List<ClassInfo> classInfos = new ArrayList<>();
        //返回辅导员，班主任列表
        for (ClassInfo info:departmentInfos) {
            ClassDto classDto = new ClassDto();
            BeanUtils.copyProperties(info,classDto);
            LambdaQueryWrapper<ClassTeacherRelation> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(ClassTeacherRelation::getClassId,info.getId()).eq(ClassTeacherRelation::getDeleted,BaseVo.UNDELETE)
                        .eq(ClassTeacherRelation::getRelationType,1);
            List<ClassTeacherRelation> list1 = classTeacherRelationService.list(queryWrapper1);
            if (list1.size() > 0){
                classDto.setClassLeader(list1);
            }
            LambdaQueryWrapper<ClassTeacherRelation> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(ClassTeacherRelation::getClassId,info.getId()).eq(ClassTeacherRelation::getDeleted,BaseVo.UNDELETE)
                    .eq(ClassTeacherRelation::getRelationType,2);
            List<ClassTeacherRelation> list2 = classTeacherRelationService.list(queryWrapper2);
            if (list2.size() > 0){
                classDto.setClassTeacher(list2);
            }
            classInfos.add(classDto);
        }
        iPage.setRecords(classInfos);

        //新建返回集合
        Map<String, Object> returnMap = new HashMap<>(16);

        //总页数
        returnMap.put(BaseVo.PAGE, iPage.getPages());
        //总数
        returnMap.put(BaseVo.TOTAL, iPage.getTotal());
        //返回数据
        returnMap.put(BaseVo.LIST,departmentInfos);

        return CommonResult.success(returnMap);
    }

    /**
     * 获取班级列表（不分页）
     */
    @Override
    public CommonResult<Object> getClassWithoutPage(UserDto userDto,ClassVo classVo) {
        Integer userType=userDto.getUserType();
        Integer schoolId=0;
        Integer departmentId=0;
        //设置查找状态正常的学校
        QueryWrapper<ClassInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("deleted", BaseVo.UNDELETE);
        if (classVo.getSchoolId() != null && classVo.getSchoolId() > 0){
            queryWrapper.eq("school_id", classVo.getSchoolId());
        }
        if (classVo.getDepartmentId() != null && classVo.getDepartmentId() > 0){
            queryWrapper.eq("department_id",classVo.getDepartmentId());
        }

        //若有传key则筛选
        if (!StringUtils.isEmpty(classVo.getKey())) {
            queryWrapper.like("class_name", classVo.getKey());
        }
        List<ClassInfo> list=this.list(queryWrapper);

        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap.put(BaseVo.LIST,list);

        return CommonResult.success(returnMap);
    }

    /**
     * 获取班级-二级学院列表
     *
     * @param userDto 用户信息
     * @param classVo 前端传入参数
     * @return 返回类
     */
    @Override
    public CommonResult<Object> getDepartmentAndClass(UserDto userDto, ClassVo classVo) {

        //查询条件
        LambdaQueryWrapper<DepartmentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DepartmentInfo::getDeleted,BaseVo.UNDELETE);

        //查询的二级学院列表
        List<DepartmentInfo> result = new ArrayList<>();
        //判断前端是否有传参数
        if (classVo != null){
            //是否传入二级学院id
            if (classVo.getDepartmentId() == null){
                //是否传入学校id
                //查询
                result = departmentInfoMapper.selectList(queryWrapper);
            }else {
                DepartmentInfo departmentInfo = departmentInfoMapper.selectById(classVo.getDepartmentId());
                result.add(departmentInfo);
            }
        }else {
            //默认无参数时查询
            result = departmentInfoMapper.selectList(queryWrapper);
        }
        //判断数组长度
        if (result.size() > 0){
            List<DepartmentWithClassDto> returnList = new ArrayList<>();
            //遍历查询
            for (DepartmentInfo info:result) {
                //返回对象
                DepartmentWithClassDto returnDto = new DepartmentWithClassDto();
                //拷贝
                BeanUtils.copyProperties(info,returnDto);
                //查询班级列表
                LambdaQueryWrapper<ClassInfo> classQueryWrapper = new LambdaQueryWrapper<>();
                classQueryWrapper.eq(ClassInfo::getDepartmentId,info.getId());
                List<ClassInfo> list = super.list(classQueryWrapper);
                //放入返回对象
                returnDto.setClassInfoList(list);
                //放入返回列表中
                returnList.add(returnDto);
            }
            
            return CommonResult.success(returnList);

        }else {
            return CommonResult.success(result);
        }

    }

    @Override
    public List<ClassInfoDto> getClassInfoDtoList() {
        return this.classInfoMapper.getClassInfoDtoList();
    }


    /**
     * @param userDto
     * @param file
     * @Description:
     * @Author: LZH
     * @Date: 2022/3/8 10:48
     */
    @Override
    public CommonResult<Object> importClass(UserDto userDto, MultipartFile file) {
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        //获取通用参数
        Integer createBy = userDto.getId();
        //获取文件名
        file.getOriginalFilename();
        try {
            //获取文件流
            InputStream inputStream = file.getInputStream();
            //实例化实现了AnalysisEventListener接口的类
            SchoolInfoExcelListener listener = new SchoolInfoExcelListener();
            ExcelReader build = EasyExcel.read(inputStream, SchoolInfoExcelData.class, listener).build();
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            build.read(readSheet);
            //获取数据
            List<Object> list = listener.getDatas();

            //学校关联信息
            Integer schoolIdDefualt = null;
            String schoolNameDefualt = null;
            Integer departmentIdDefualt = null;
            String departmentNameDefualt = null;

            //根据不同角色获取绑定的角色
            if (roleCode.equals(AuthRole.DEPARTMENT_ADMIN)){
                if (userDto.getDepartmentIds() == null || userDto.getDepartmentIds().size() <= 0){
                    return CommonResult.error(500,"绑定二级学院id为空");
                }
                departmentIdDefualt = userDto.getDepartmentIds().get(0);
                DepartmentInfo departmentInfo = departmentInfoMapper.selectById(departmentIdDefualt);
                departmentNameDefualt = departmentInfo.getDepartmentName();
                schoolIdDefualt = departmentInfo.getSchoolId();
                schoolNameDefualt = schoolInfoMapper.selectById(schoolIdDefualt).getSchoolName();
            }else {
                return CommonResult.error(500,"无权限");
            }

            //判断是否存在数据
            if (list.size()>0){
                //年级列表
                QueryWrapper<GradeInfo> gradeInfoQueryWrapper = new QueryWrapper<>();
                gradeInfoQueryWrapper.eq("deleted",BaseVo.UNDELETE).eq("school_id",schoolIdDefualt);
                List<GradeInfo> gradeInfos = gradeInfoService.list(gradeInfoQueryWrapper);

                //第一次循环判断数据是否存在
                for (Object o:list){
                    SchoolInfoExcelData data = (SchoolInfoExcelData) o;
                    //获取数据-学校
                    String schoolName = data.getSchoolName();
                    //二级学院
                    String departmentName = data.getDepartmentName();
                    //年级
                    String gradeName = data.getGradeName();
                    if (!gradeName.endsWith("级")){
                        gradeName = gradeName + "级";
                    }
                    //专业
                    String majorName = data.getMajorName();
                    //班级
                    String className = data.getClassName();
                    //判断院校信息是否完全存在
                    if (StringUtils.isBlank(schoolName)){
                        return CommonResult.error(500,"请检查学校信息");
                    }
                    if (StringUtils.isBlank(departmentName)){
                        return CommonResult.error(500,"请检查二级学院信息");
                    }
                    if (StringUtils.isBlank(gradeName)){
                        return CommonResult.error(500,"请检查年级信息");
                    }
                    if (StringUtils.isBlank(majorName)){
                        return CommonResult.error(500,"请检查专业信息");
                    }
                    if (StringUtils.isBlank(className)){
                        return CommonResult.error(500,"请检查班级信息");
                    }
                    boolean flag = true;
                    for (GradeInfo grade:gradeInfos) {
                        if (grade.getGradeYear().equals(gradeName)){
                            flag = false;
                        }
                    }
                    if (flag){
                        return CommonResult.error(500,"["+gradeName+"]该年级不存在，请联系管理员添加");
                    }
                }
                //班级列表
                QueryWrapper<ClassInfo> classQueryWrapper = new QueryWrapper<>();
                classQueryWrapper.eq("deleted",BaseVo.UNDELETE).eq("school_id",schoolIdDefualt).eq("department_id",departmentIdDefualt);
                List<ClassInfo> classInfos = this.list(classQueryWrapper);
                //教师列表
                QueryWrapper<TeacherInfo> teacherQueryWrapper = new QueryWrapper<>();
                teacherQueryWrapper.eq("deleted",BaseVo.UNDELETE).eq("school_id",schoolIdDefualt);
                List<TeacherInfo> teacherInfos = teacherInfoMapper.selectList(teacherQueryWrapper);
                //专业列表
                QueryWrapper<MajorInfo> majorInfoQueryWrapper = new QueryWrapper<>();
                majorInfoQueryWrapper.eq("deleted",BaseVo.UNDELETE).eq("school_id",schoolIdDefualt).eq("department_id",departmentIdDefualt);
                List<MajorInfo> majorInfos = majorInfoService.list(majorInfoQueryWrapper);

                //批量插入集合
                Collection<ClassInfo> classInfoList = new ArrayList<>();
                Collection<ClassTeacherRelation> classTeacherRelations = new ArrayList<>();
                //用户权限
                Collection<AuthUserRole> userRoles = new ArrayList<>();
                Collection<TeacherInfo> updateTeacherInfos = new ArrayList<>();

                for (Object o:list){
                    SchoolInfoExcelData data = (SchoolInfoExcelData) o;
                    //获取数据-学校
                    String schoolName = data.getSchoolName();
                    if (!schoolName.equals(schoolNameDefualt)){
                        throw new RuntimeException("请检查学校信息【"+schoolName+"】是否为当前管理二级学院所属学校");
                    }
                    //二级学院
                    String departmentName = data.getDepartmentName();
                    if (!departmentName.equals(departmentNameDefualt)){
                        throw new RuntimeException("请检查二级学院信息【"+departmentName+"】是否为当前管理二级学院");
                    }
                    //班级
                    String className = data.getClassName();
                    //年级
                    String gradeName = data.getGradeName();
                    if (!gradeName.endsWith("级")){
                        gradeName = gradeName + "级";
                    }
                    //专业
                    String majorName = data.getMajorName();
                    //辅导员信息
                    String classLeader = data.getClassLeader();
                    String classLeaderNum = data.getClassLeaderNum();
                    String classTeacher = data.getClassTeacher();

                    boolean classIsNew = false;
                    TeacherInfo classLeaderInfo = null;

                    //关联参数
                    Integer classId = null;
                    Integer majorId = null;
                    Integer gradeId = null;

                    //判断院校信息是否完全存在
                    if (StringUtils.isEmpty(schoolName)){
                        return CommonResult.error(500,"请检查学校信息是否存在");
                    }
                    if (StringUtils.isEmpty(departmentName)){
                        return CommonResult.error(500,"请检查二级学院信息是否存在");
                    }
                    //辅导员
                    if (!StringUtils.isBlank(classLeader)){
                        //判断系管
                        if (!StringUtils.isBlank(classLeaderNum)||!StringUtils.isBlank(classLeader)){
                            if (StringUtils.isBlank(classLeaderNum)||StringUtils.isBlank(classLeader)){
                                return CommonResult.error(500,"请检查系管理员工号和姓名是否填写");
                            }
                            boolean isExist = false;
                            //循环判断
                            for (TeacherInfo info:teacherInfos) {
                                if (info.getWorkNumber().equals(classLeaderNum)){
                                    if (info.getTeacherName().equals(classLeader)){
                                        //放入关联参数中
                                        classLeaderInfo = info;
                                        isExist = true;
                                    }else {
                                        return CommonResult.error(500,"教职工【"+classLeader+"】与工号【"+classLeaderNum+"】不符");
                                    }
                                    break;
                                }
                            }
                            //如果教师不存在，则新增一个教师信息
                            if (!isExist){
                                //添加一个教师
                                TeacherInfo teacherInfo = teacherInfoService.addNewTeacher(userDto, classLeader, classLeaderNum);
                                if (teacherInfo != null){
                                    log.info("添加教师"+classLeader+"成功！");
                                    classLeaderInfo = teacherInfo;
                                    teacherInfos.add(teacherInfo);
                                    classIsNew = true;
                                }else {
                                    log.info("添加教师"+classLeader+"失败！");
                                }
                            }
                        }
                    }
                    //判断添加年级
                    if (!StringUtils.isBlank(gradeName)){
                        //查看是否存在
                        for (GradeInfo info:gradeInfos) {
                            //判断如果同个学校中有该名称的则关联
                            if (info.getSchoolId().equals(schoolIdDefualt)){
                                //班级名称相同
                                if (info.getGradeYear().equals(gradeName)){
                                    gradeId = info.getId();
                                    break;
                                }
                            }
                        }
                    }
                    //判断添加专业
                    if (!StringUtils.isBlank(majorName)){
                        //查看是否存在
                        for (MajorInfo info:majorInfos) {
                            //判断如果同个学校中有该名称的则关联
                            if (info.getSchoolId().equals(schoolIdDefualt)){
                                if (info.getDepartmentId().equals(departmentIdDefualt)){
                                    //班级名称相同
                                    if (info.getMajorName().equals(majorName)){
                                        majorId = info.getId();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //不存在则添加
                    if(majorId==null){
                        //新建班级
                        MajorInfo majorInfo = new MajorInfo();
                        //关联学校
                        majorInfo.setSchoolId(schoolIdDefualt);
                        //二级学院学校
                        majorInfo.setDepartmentId(departmentIdDefualt);
                        //专业名称
                        majorInfo.setMajorName(majorName);
                        //创建人
                        majorInfo.setCreateBy(createBy);
                        //插入数据库
                        boolean save = majorInfoService.save(majorInfo);
                        if (save){
                            //添加到数组中
                            majorInfos.add(majorInfo);
                            majorId = majorInfo.getId();
                        }else {
                            throw new RuntimeException("班级"+majorName+"添加异常");
                        }
                    }
                    //判断是否有班级
                    if (!StringUtils.isEmpty(className)){
                        /*添加班级*/
                        //查看班级是否存在
                        for (ClassInfo info:classInfos) {
                            //判断如果同个学校中有该名称的则关联
                            if (info.getSchoolId().equals(schoolIdDefualt)){
                                //二级学院名称
                                if (info.getDepartmentId().equals(departmentIdDefualt)){
                                    //班级名称相同
                                    if (info.getClassName().equals(className)){
                                        classId = info.getId();
                                        break;
                                    }
                                }
                            }
                        }
                        //不存在则添加
                        if(classId==null){
                            //新建班级
                            ClassInfo classInfo = new ClassInfo();
                            //关联学校
                            classInfo.setSchoolId(schoolIdDefualt);
                            //关联二级学院
                            classInfo.setDepartmentId(departmentIdDefualt);
                            //专业id
                            classInfo.setMajorId(majorId);
                            //年级id
                            classInfo.setGradeId(gradeId);
                            //二级学院名称
                            classInfo.setClassName(className);
                            //创建人
                            classInfo.setCreateBy(createBy);
                            //插入数据库
                            boolean save = super.save(classInfo);
                            if (save){
                                //添加到数组中
                                classInfos.add(classInfo);
                                classId = classInfo.getId();
                            }else {
                                throw new RuntimeException("班级"+className+"添加异常");
                            }
                        }
                        if (classLeaderInfo != null){
                            //查询是否有该领导
                            QueryWrapper<ClassTeacherRelation> relationQueryWrapper = new QueryWrapper<>();
                            relationQueryWrapper.eq("class_id",classId).eq("deleted",0)
                                    .eq("teacher_id",classLeaderInfo.getId()).eq("relation_type",1);
                            //判断数据库是否有数据
                            int count = classTeacherRelationService.count(relationQueryWrapper);
                            //没有的话添加
                            if (count<=0) {
                                //新建关联实体类
                                ClassTeacherRelation classTeacherRelation = new ClassTeacherRelation();
                                //关联学校id
                                classTeacherRelation.setClassId(classId);
                                //关联教职工id
                                classTeacherRelation.setTeacherId(classLeaderInfo.getId());
                                classTeacherRelation.setTeacherName(classLeaderInfo.getTeacherName());
                                classTeacherRelation.setRelationType(1);
                                classTeacherRelation.setCreateBy(createBy);
                                //放入批量添加数组
                                classTeacherRelations.add(classTeacherRelation);

                                /*判断是否需要添加权限*/
                                Boolean checkTeacher = checkTeacher(userRoles, classLeaderInfo.getUserId(), 4);
                                //返回ture添加权限
                                if (!checkTeacher) {
                                    //添加权限
                                    AuthUserRole role = new AuthUserRole();
                                    role.setRoleId(4);
                                    role.setUserId(classLeaderInfo.getUserId());
                                    role.setCreateBy(createBy);
                                    userRoles.add(role);
                                }
                            }
                            if (classIsNew){
                                classLeaderInfo.setSchoolName(schoolName);
                                classLeaderInfo.setSchoolId(schoolIdDefualt);
                                classLeaderInfo.setDepartmentId(departmentIdDefualt);
                                classLeaderInfo.setDepartmentName(departmentName);
                                updateTeacherInfos.add(classLeaderInfo);
                            }
                        }
                        if (!StringUtils.isBlank(classTeacher)){
                            //查询是否有该领导
                            QueryWrapper<ClassTeacherRelation> relationQueryWrapper = new QueryWrapper<>();
                            relationQueryWrapper.eq("class_id",classId).eq("deleted",0)
                                    .eq("teacher_name",classTeacher).eq("relation_type",2);
                            //判断数据库是否有数据
                            int count = classTeacherRelationService.count(relationQueryWrapper);
                            //没有的话添加
                            if (count<=0) {
                                //新建关联实体类
                                ClassTeacherRelation classTeacherRelation = new ClassTeacherRelation();
                                //关联学校id
                                classTeacherRelation.setClassId(classId);
                                //关联教职工id
                                classTeacherRelation.setRelationType(2);
                                classTeacherRelation.setCreateBy(createBy);
                                classTeacherRelation.setTeacherName(classTeacher);
                                //放入批量添加数组
                                classTeacherRelations.add(classTeacherRelation);
                            }
                        }
                    }
                }
                boolean save = this.saveBatch(classInfoList);
                if (!save){
                    return CommonResult.success("导入失败");
                }
                if (classTeacherRelations.size()>0){
                    boolean b = classTeacherRelationService.saveBatch(classTeacherRelations, 1000);
                    if (!b){
                        throw new RuntimeException("辅导员导入异常");
                    }
                }
                if (updateTeacherInfos.size()>0){
                    boolean b = teacherInfoService.updateBatchById(updateTeacherInfos);
                    if (!b){
                        log.info("更新教师管理信息失败");
                    }
                }
                return CommonResult.success("导入成功");
            }else {
                return CommonResult.error(500,"表格数据为空");
            }
        }catch (IOException e){
            return CommonResult.error(500,e.getMessage());
        }catch (RuntimeException e){
            log.info("异常打印-->"+e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CommonResult.error(500,e.getMessage());
        }
    }
    /**
     * @Description: 查找数据库中是否存在改权限
     * @Author: LZH
     * @Date: 2022/1/17 15:21
     */
    private Boolean checkTeacher(Collection<AuthUserRole> userRoles, Integer userId, Integer roleId){

        QueryWrapper<AuthUserRole> authUserRoleQueryWrapper = new QueryWrapper<>();
        authUserRoleQueryWrapper.eq("deleted",0)
                .eq("user_id",userId).eq("role_id",roleId);

        int count = authUserRoleService.count(authUserRoleQueryWrapper);
        //判断返回
        if (count>0){
            return false;
        }else {
            if(userRoles.size()>0){
                boolean flag =false;
                //循环查看是否添加到roles中
                for (AuthUserRole info:userRoles) {
                    //判断userid是否相等
                    if(info.getUserId().equals(userId)){
                        //判断权限是否相等
                        if (info.getRoleId().equals(roleId)){
                            flag= true;
                            break;
                        }
                    }
                }
                return flag;
            }else {
                return false;
            }
        }
    }

}
