package com.above.service.impl;

import com.above.bean.excel.SchoolInfoExcelData;
import com.above.config.easyExcel.SchoolInfoExcelListener;
import com.above.dao.DepartmentInfoMapper;
import com.above.dao.SchoolInfoMapper;
import com.above.dao.TeacherInfoMapper;
import com.above.dto.DepartmentSimpleDto;
import com.above.dto.DepartmentWithLeaderDto;
import com.above.dto.LeaderList;
import com.above.dto.UserDto;
import com.above.po.*;
import com.above.service.*;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.ClassVo;
import com.above.vo.DepartmentVo;
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
 * 二级学院 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Service
@Slf4j
public class DepartmentInfoServiceImpl extends ServiceImpl<DepartmentInfoMapper, DepartmentInfo> implements DepartmentInfoService {

    @Autowired
    private SchoolInfoMapper schoolInfoMapper;

    @Autowired
    private DepartmentTeacherRelationService departmentTeacherService;
    @Autowired
    private AuthUserRoleService authUserRoleService;
    @Autowired
    private ClassInfoService classInfoService;
    @Autowired
    private DepartmentTeacherRelationService departmentTeacherRelationService;
    @Autowired
    private TeacherInfoMapper teacherInfoMapper;
    @Autowired
    private TeacherInfoService teacherInfoService;
    @Autowired
    private ClassTeacherRelationService classTeacherRelationService;
    @Autowired
    private MajorInfoService majorInfoService;
    @Autowired
    private GradeInfoService gradeInfoService;


    /**
     * 添加二级学院
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> addDepartment(UserDto userDto, DepartmentVo departmentVo) {
        //获取参数 vbvvbb
        Integer createBy = userDto.getId();
        Integer schoolId = departmentVo.getSchoolId();
        Collection<Integer> teacherIds = departmentVo.getTeacherIdList();

        //判断学校是否存在
        SchoolInfo schoolInfo = schoolInfoMapper.selectById(schoolId);
        if (schoolInfo == null){
            return CommonResult.error(500,"学校不存在");
        }

        //判断二级学院名称是否重复
        QueryWrapper<DepartmentInfo> departmentInfoQueryWrapper = new QueryWrapper<>();
        departmentInfoQueryWrapper.eq("department_name",departmentVo.getDepartmentName())
                                    .eq("school_id",schoolId).eq("deleted",BaseVo.UNDELETE);
        int count = super.count(departmentInfoQueryWrapper);
        if (count>0){
            return CommonResult.error(500,"名称重复");
        }

        //新建二级学院实体类
        DepartmentInfo departmentInfo = new DepartmentInfo();
        //二级学院名称
        departmentInfo.setDepartmentName(departmentVo.getDepartmentName());
        departmentInfo.setCreateBy(createBy);
        //关联学校id
        departmentInfo.setSchoolId(schoolId);

        //插入数据库
        boolean save = this.save(departmentInfo);

        //添加成功则增加校领导信息
        if (save){
            //二级学院id
            Integer departmentId = departmentInfo.getId();
            try {
                /*判断是否有教师传入，有则添加关联*/

                //添加成功返回
                return CommonResult.success("添加成功",null);
            }catch (Exception e){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                log.info("异常打印-->"+e.getMessage());
                return CommonResult.error(500,e.getMessage());
            }

        }else {
        return CommonResult.error(500,"添加二级学院失败");
    }

    }
    /**
     * 修改二级学院
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> modifyDepartment(UserDto userDto, DepartmentVo departmentVo) {
        //获取参数
        boolean flag=false;
        Integer updateBy = userDto.getId();
        Integer departmentId = departmentVo.getDepartmentId();
        String departmentName = departmentVo.getDepartmentName();
        List<Integer> teacherIds = departmentVo.getTeacherIdList();
        List<Integer> deleteTeacherIdList = departmentVo.getDeleteTeacherIdList();
        //批量添加数组
        Collection<DepartmentTeacherRelation> teacherRelations = new ArrayList<>();
        Collection<AuthUserRole> roles = new ArrayList<>();

        DepartmentInfo departmentInfo = super.getById(departmentId);
        //判断二级学院是否存在
        if (departmentInfo == null){
            return CommonResult.error(500,"该二级学院不存在");
        }
        if (departmentInfo.getDeleted()==1){
            return CommonResult.error(500,"该二级学院已删除");
        }
        //若二级学院名称与原名称不一致则修改
        if (!departmentInfo.getDepartmentName().equals(departmentName)){

            QueryWrapper<DepartmentInfo> departmentInfoQueryWrapper = new QueryWrapper<>();
            departmentInfoQueryWrapper.eq("school_id",departmentInfo.getSchoolId())
                                .eq("department_name",departmentName).eq("deleted",0);
            int count = this.count(departmentInfoQueryWrapper);
            if (count>0){
                return CommonResult.error(500,"该二级学院已存在");
            }
            //设置新二级学院名称
            departmentInfo.setDepartmentName(departmentName);
            //修改flag状态
            flag=true;
        }
        try {
            //判断二级学院管理是否有传，没有传直接跳过添加校领导
            if (teacherIds != null&&teacherIds.size()>0){

                /*修改成功判断是否有领导需要更新*/
                /*判断新老数组是否一致，删除掉一样的元素*/
                Iterator<Integer> iterator1 = teacherIds.iterator();
                while (iterator1.hasNext()){
                    Integer teacherId1 = iterator1.next();
                    Iterator<Integer> iterator2 = deleteTeacherIdList.iterator();
                    while(iterator2.hasNext()){
                        Integer deleteTeacherList =iterator2.next();
                        if (teacherId1.equals(deleteTeacherList)){
                            iterator1.remove();
                            iterator2.remove();
                        }
                    }
                }

                //判断是否要添加
                if (teacherRelations.size()>0){
                    boolean save = departmentTeacherService.saveBatch(teacherRelations);
                    if (!save){
                        throw new RuntimeException("添加关联信息失败");
                    }
                }
                //判断是否要添加
                if (roles.size()>0){
                    boolean save = authUserRoleService.saveBatch(roles);
                    if (!save){
                        throw new RuntimeException("添加角色信息失败");
                    }
                }
                flag=true;
            }
            if(deleteTeacherIdList != null && deleteTeacherIdList.size() > 0){
                // 删除权限
                Integer error=500;
                //删除校领导
                CommonResult<Object> objectCommonResult = this.deleteDepartmentLeader(updateBy,deleteTeacherIdList,departmentId);
                //判断删除校领导是否成功
                if (objectCommonResult.getCode().equals(error)){
                    return objectCommonResult;
                }
                flag=true;

            }
            //判断是否有执行更新
            if (flag){
                //更新数据库
                boolean b = super.updateById(departmentInfo);
                if (b){
                    return CommonResult.success("修改成功",null);
                }else {
                    throw new RuntimeException("修改失败");
                }
            }else {
                //没操作更新直接返回
                return CommonResult.success("无需修改",null);
            }
        }catch (RuntimeException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("异常打印-->"+e.getMessage());
            return CommonResult.error(500,e.getMessage());
        }

    }
    /**
     * 批量删除二级学院
     */
    @Override
    public CommonResult<Object> deleteDepartment(UserDto userDto, DepartmentVo departmentVo) {
        //获取参数
        Collection<Integer> departmentIdList = departmentVo.getDepartmentIdList();
        Integer updateBy = userDto.getId();

        if(departmentIdList.isEmpty()){
            return CommonResult.error(500,"缺少二级学院id");
        }
        //批量查询
        List<DepartmentInfo> departmentInfos = this.getBaseMapper().selectBatchIds(departmentIdList);

        //循环设置删除状态和更新人
        for (DepartmentInfo info:departmentInfos) {
            if (info.getDeleted()==1){
                return CommonResult.error(500,"请勿重复删除");
            }
            info.setUpdateBy(updateBy);
            info.setDeleted(1);
        }
        //批量删除
        boolean i = this.updateBatchById(departmentInfos);

        //删除成功后，删除相关信息
        if (i){
            log.info("删除成功,开始删除相关信息");

            //删除关联的领导
            QueryWrapper<DepartmentTeacherRelation> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("department_id",departmentIdList).eq("deleted",BaseVo.UNDELETE);
            Collection<DepartmentTeacherRelation> schoolTeacherRelations = departmentTeacherService.list(queryWrapper);
            //如果存在则进行删除
            if (schoolTeacherRelations.size()>0){
                //循环设置删除状态和更新人
                for (DepartmentTeacherRelation info:schoolTeacherRelations) {
                    info.setUpdateBy(updateBy);
                    info.setDeleted(1);
                    //删除领导权限
                    CommonResult<Object> result = this.deleteDepartmentLeaderRole(info.getTeacherId(), info.getId());
                    //判断是否成功
                    if (result.getCode()==500){
                        return CommonResult.error(500, result.getMessage());
                    }else {
                        log.info(result.getMessage());
                    }
                }
                //批量删除
                boolean isSuccess = departmentTeacherService.updateBatchById(schoolTeacherRelations);
                //若删除失败则抛出异常回滚
                if (!isSuccess){
                    throw new RuntimeException("领导信息删除错误");
                }
            }

            /*清空关联教师的二级学院数据*/
            LambdaQueryWrapper<TeacherInfo> teacherInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teacherInfoLambdaQueryWrapper.in(TeacherInfo::getDepartmentId,departmentIdList).eq(TeacherInfo::getDeleted,BaseVo.UNDELETE);
            List<TeacherInfo> teacherInfos = teacherInfoService.list(teacherInfoLambdaQueryWrapper);
            teacherInfos.forEach(item -> item.setDepartmentId(null).setDepartmentName(null));
            if (teacherInfos.size() > 0){
                boolean updateTeacher = teacherInfoService.updateBatchById(teacherInfos);
                if (!updateTeacher){
                    throw new RuntimeException("更新教师信息出错");
                }
            }

            //删除相关班级
            QueryWrapper<ClassInfo> classInfoQueryWrapper = new QueryWrapper<>();
            classInfoQueryWrapper.in("department_id",departmentIdList).eq("deleted",BaseVo.UNDELETE);
            Collection<ClassInfo> classInfos = classInfoService.list(classInfoQueryWrapper);
            List<Integer> classIds = new ArrayList<>();
            classInfos.forEach(info ->
                classIds.add(info.getId())
            );

            ClassVo classVo = new ClassVo();
            classVo.setClassIdList(classIds);
            CommonResult<Object> deleteClass = classInfoService.deleteClass(userDto, classVo);

            if (!deleteClass.isSuccess()){
                throw new RuntimeException(deleteClass.getMessage());
            }
        }else {
            throw new RuntimeException("删除失败");
        }
        return CommonResult.success("删除成功",null);
    }
    /**
     * 获取二级学院列表（分页）
     */
    @Override
    public CommonResult<Object> getDepartmentPageList(UserDto userDto,DepartmentVo departmentVo) {
        String roleCode = userDto.getUserRoleDto().getRoleCode();

        //设置分页参数
        Page<DepartmentInfo> page = new Page<>(departmentVo.getPage(), departmentVo.getSize());
        //设置查找状态正常的学校
        QueryWrapper<DepartmentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",BaseVo.UNDELETE);
        //超管权限
        if(AuthRole.ADMIN.equals(roleCode)){
            if (departmentVo.getSchoolId() != null && departmentVo.getSchoolId() > 0){
                queryWrapper.eq("school_id",departmentVo.getSchoolId());
            }
        }else if (AuthRole.SCHOOL_ADMIN.equals(roleCode)){
            if (departmentVo.getSchoolId() != null && departmentVo.getSchoolId() > 0){
                queryWrapper.eq("school_id",departmentVo.getSchoolId());
            }else {
                queryWrapper.in("school_id",userDto.getSchoolIds());
            }
        }else if (AuthRole.DEPARTMENT_ADMIN.equals(roleCode)){
            if (departmentVo.getSchoolId() != null && departmentVo.getSchoolId() > 0 ){
                queryWrapper.eq("school_id",departmentVo.getSchoolId());
            }else {
                queryWrapper.in("id",userDto.getDepartmentIds());
            }
        }else {
            queryWrapper.in("id",userDto.getTeacherInfo().getDepartmentId());
        }

        //若有传key则筛选
        if (!StringUtils.isBlank(departmentVo.getKey())){
            queryWrapper.like("department_name", departmentVo.getKey());
        }

        IPage<DepartmentInfo> iPage = super.page(page, queryWrapper);
        //获取分页集合
        List<DepartmentInfo> departmentInfos = iPage.getRecords();
        //新建返回数组
        List<DepartmentWithLeaderDto> returnList = new ArrayList<>();

        //循环获取该校领导信息
        for (DepartmentInfo info:departmentInfos) {
            //根据学校id获取学校的领导
            List<LeaderList> leaderListBySchoolId = departmentTeacherService.getLeaderListByDepartmentId(info.getId(),1);
            //新建返回类
            DepartmentWithLeaderDto departmentWithLeaderDto = new DepartmentWithLeaderDto();
            //拷贝school信息
            BeanUtils.copyProperties(info,departmentWithLeaderDto);
            //放入领导数组
            if (leaderListBySchoolId!=null && !leaderListBySchoolId.isEmpty() && leaderListBySchoolId.get(0)!=null) {
                departmentWithLeaderDto.setList(leaderListBySchoolId);
            }else {
                departmentWithLeaderDto.setList(new ArrayList<>());
            }
            //放入返回数组
            returnList.add(departmentWithLeaderDto);
        }
        //新建返回集合
        Map<String, Object> returnMap = new HashMap<>(10);
        //放入学校列表
        returnMap.put(BaseVo.LIST,returnList);
        //总页数
        returnMap.put(BaseVo.PAGE,iPage.getPages());
        //总数
        returnMap.put(BaseVo.TOTAL,iPage.getTotal());

        return CommonResult.success(returnMap);
    }
    /**
     * 获取二级学院列表（不分页）
     */
    @Override
    public CommonResult<Object> getDepartmentWithoutPage(UserDto userDto,DepartmentVo departmentVo) {
        String roleCode = userDto.getUserRoleDto().getRoleCode();

        //设置查找状态正常的二级学院
        QueryWrapper<DepartmentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",BaseVo.UNDELETE);
        //超管权限
        if(AuthRole.ADMIN.equals(roleCode)){
            if (departmentVo.getSchoolId() != null && departmentVo.getSchoolId() > 0){
                queryWrapper.eq("school_id",departmentVo.getSchoolId());
            }
        }else if (AuthRole.SCHOOL_ADMIN.equals(roleCode)){
            if (departmentVo.getSchoolId() != null && departmentVo.getSchoolId() > 0){
                queryWrapper.eq("school_id",departmentVo.getSchoolId());
            }else {
                queryWrapper.in("school_id",userDto.getSchoolIds());
            }
        }else if (AuthRole.DEPARTMENT_ADMIN.equals(roleCode)){
            if (departmentVo.getSchoolId() != null && departmentVo.getSchoolId() > 0){
                queryWrapper.eq("school_id",departmentVo.getSchoolId()).in("id",userDto.getDepartmentIds());
            }else {
                queryWrapper.in("id",userDto.getDepartmentIds());
            }
        }else {
            queryWrapper.in("id",userDto.getTeacherInfo().getDepartmentId());
        }

        //若有传key则筛选
        if (departmentVo.getKey() != null){
            queryWrapper.like("department_name", departmentVo.getKey());
        }

        //搜索
        List<DepartmentInfo> list = super.list(queryWrapper);

        ArrayList<DepartmentSimpleDto> returnList = new ArrayList<>();

        //循环取出有用的数据返回
        for (DepartmentInfo info:list) {
            DepartmentSimpleDto departmentSimpleDto = new DepartmentSimpleDto();
            BeanUtils.copyProperties(info,departmentSimpleDto);
            //放入返回数组
            returnList.add(departmentSimpleDto);
        }
        Map<String, Object> returnMap = new HashMap<>(10);
        returnMap.put(BaseVo.LIST,returnList);

        return CommonResult.success(returnMap);
    }

    /**
     * @Description: 删除二级学院管理
     * @Author: LZH
     * @Date: 2022/1/12 19:43
     */
    @Override
    public CommonResult<Object> deleteDepartmentLeader(Integer updateBy, Collection<Integer> deleteTeacherIdList,Integer departmentId) {
        //批量查询
        List<TeacherInfo> teacherInfos = teacherInfoMapper.selectBatchIds(deleteTeacherIdList);
        //循环判断删除
        for (TeacherInfo info: teacherInfos) {
            //获取id
            Integer id = info.getId();
            //创建查找
            QueryWrapper<DepartmentTeacherRelation> teacherRelationQueryWrapper = new QueryWrapper<>();
            teacherRelationQueryWrapper.eq("deleted",BaseVo.UNDELETE).eq("department_id",departmentId)
                    .eq("teacher_id",id);
            //获取该校领导
            DepartmentTeacherRelation one = departmentTeacherService.getOne(teacherRelationQueryWrapper);
            //判断是否删除
            if (one != null){
                //设置更新人
                one.setUpdateBy(updateBy);
                //删除状态
                one.setDeleted(1);
                boolean b = departmentTeacherService.updateById(one);
                if (!b){
                    CommonResult.error(500,"删除失败");
                }
            }
            /*删除后判断该教职工是否需要删除领导角色*/
            //创建查找，该教师是否还有领导权限
            QueryWrapper<DepartmentTeacherRelation> relationQueryWrapper = new QueryWrapper<>();
            relationQueryWrapper.eq("deleted",BaseVo.UNDELETE)
                    .eq("teacher_id",id);
            List<DepartmentTeacherRelation> list = departmentTeacherService.list(relationQueryWrapper);
            int count = list.size();
            //若无关联学校则删除权限
            if (count<=0){
                QueryWrapper<AuthUserRole> roleQueryWrapper = new QueryWrapper<>();
                roleQueryWrapper.eq("user_id",info.getUserId()).eq("role_id",3)
                        .eq("deleted",BaseVo.UNDELETE);
                AuthUserRole role = authUserRoleService.getOne(roleQueryWrapper);
                //判断是否获取到信息
                if (role != null){
                    //设置更新信息
                    role.setDeleted(1);
                    role.setUpdateBy(updateBy);

                    boolean b = authUserRoleService.updateById(role);

                    if (!b){
                        return CommonResult.error(500,"删除权限异常");
                    }
                }

            }

        }
        log.info("移除角色成功");
        return CommonResult.success("修改完成");
    }

    /**
     * @Description: 刪除领导判断权限
     * @Author: LZH
     * @Date: 2022/1/13 8:48
     */
    @Override
    public CommonResult<Object> deleteDepartmentLeaderRole(Integer teacherId, Integer relationId)throws RuntimeException{
        //查找该教师是否还有管理的学校
        QueryWrapper<DepartmentTeacherRelation> schoolInfoQueryWrapper = new QueryWrapper<>();
        schoolInfoQueryWrapper.eq("teacher_id",teacherId).ne("id",relationId).eq("deleted",BaseVo.UNDELETE);
        //查询数量
        int count = departmentTeacherService.count(schoolInfoQueryWrapper);
        //若数量为0，则删除领导权限
        if (count<=0){
            //获取教师User_id
            TeacherInfo teacherInfo = teacherInfoMapper.selectById(teacherId);
            //根据User_id查询
            QueryWrapper<AuthUserRole> authUserRoleQueryWrapper = new QueryWrapper<>();
            authUserRoleQueryWrapper.eq("user_id",teacherInfo.getUserId()).eq("deleted",BaseVo.UNDELETE)
                    .eq("role_id",3);
            AuthUserRole one = authUserRoleService.getOne(authUserRoleQueryWrapper);
            //若存在则删除
            if (one != null){
                one.setDeleted(1);
                boolean b = authUserRoleService.updateById(one);
                if (!b){
                    throw new RuntimeException("权限删除异常");
                }
            }
        }
        return CommonResult.success("删除成功",null);
    }

    /**
     * @param userDto
     * @param file
     * @Description: 二级学院导入
     * @Author: LZH
     * @Date: 2022/1/17 17:03
     */
    @Override
    public CommonResult<Object> importDepartmentInfo(UserDto userDto, MultipartFile file) throws RuntimeException{
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
            Integer schoolIdDefualt;
            String schoolNameDefualt;

            //根据不同角色获取绑定的角色
            if (roleCode.equals(AuthRole.SCHOOL_ADMIN)){
                if (userDto.getSchoolIds() == null || userDto.getSchoolIds().size() <= 0){
                    return CommonResult.error(500,"绑定学校id为空");
                }
                schoolIdDefualt = userDto.getSchoolIds().get(0);
                schoolNameDefualt = schoolInfoMapper.selectById(schoolIdDefualt).getSchoolName();
            }else {
                return CommonResult.error(500,"只有校领导才能操作");
            }

            //判断是否存在数据
            if (list.size()>0){
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
                }
                //二级学院列表
                QueryWrapper<DepartmentInfo> departmentQueryWrapper = new QueryWrapper<>();
                departmentQueryWrapper.eq("deleted",BaseVo.UNDELETE).eq("school_id",schoolIdDefualt);
                //班级列表
                List<DepartmentInfo> departmentInfos = this.list(departmentQueryWrapper);
                QueryWrapper<ClassInfo> classQueryWrapper = new QueryWrapper<>();
                classQueryWrapper.eq("deleted",BaseVo.UNDELETE).eq("school_id",schoolIdDefualt);
                List<ClassInfo> classInfos = classInfoService.list(classQueryWrapper);
                //教师列表
                QueryWrapper<TeacherInfo> teacherQueryWrapper = new QueryWrapper<>();
                teacherQueryWrapper.eq("deleted",BaseVo.UNDELETE).eq("school_id",schoolIdDefualt);
                List<TeacherInfo> teacherInfos = teacherInfoMapper.selectList(teacherQueryWrapper);
                //专业列表
                QueryWrapper<MajorInfo> majorInfoQueryWrapper = new QueryWrapper<>();
                majorInfoQueryWrapper.eq("deleted",BaseVo.UNDELETE).eq("school_id",schoolIdDefualt);
                List<MajorInfo> majorInfos = majorInfoService.list(majorInfoQueryWrapper);
//年级列表
                QueryWrapper<GradeInfo> gradeInfoQueryWrapper = new QueryWrapper<>();
                gradeInfoQueryWrapper.eq("deleted",BaseVo.UNDELETE).eq("school_id",schoolIdDefualt);
                List<GradeInfo> gradeInfos = gradeInfoService.list(gradeInfoQueryWrapper);

                //批量插入集合
                //用户权限
                Collection<AuthUserRole> userRoles = new ArrayList<>();
                //教职工关联
                Collection<DepartmentTeacherRelation> departmentTeacherRelations = new ArrayList<>();
                Collection<ClassTeacherRelation> classTeacherRelations = new ArrayList<>();
                Collection<TeacherInfo> updateTeacherInfos = new ArrayList<>();

                for (Object o:list){
                    SchoolInfoExcelData data = (SchoolInfoExcelData) o;
                    //获取数据-学校
                    String schoolName = data.getSchoolName();
                    if (!schoolName.equals(schoolNameDefualt)){
                        return CommonResult.error(500,"请检查学校信息【"+schoolName+"】是否为管理院校名称");
                    }
                    //二级学院
                    String departmentName = data.getDepartmentName();
                    String departmentLeader = data.getDepartmentLeader();
                    String departmentLeaderNum = data.getDepartmentLeaderNum();
                    //班级
                    String className = data.getClassName();
                    String classLeader = data.getClassLeader();
                    String classLeaderNum = data.getClassLeaderNum();
                    String classTeacher = data.getClassTeacher();
                    //年级
                    String gradeName = data.getGradeName();
                    if (!gradeName.endsWith("级")){
                        gradeName = gradeName + "级";
                    }
                    //专业
                    String majorName = data.getMajorName();
                    //关联参数
                    Integer departmentId = null;
                    Integer classId = null;
                    Integer majorId = null;
                    Integer gradeId = null;
                    //关联领导参数
                    boolean depIsNew = false;
                    TeacherInfo departmentLeaderInfo = null;
                    boolean classIsNew = false;
                    TeacherInfo classLeaderInfo = null;

                    //判断院校信息是否完全存在
                    if (StringUtils.isBlank(schoolName)){
                        return CommonResult.error(500,"请检查学校信息是否存在");
                    }
                    if (StringUtils.isBlank(departmentName)){
                        return CommonResult.error(500,"请检查二级学院信息是否存在");
                    }

                    //判断两种种教职工是否存在（不用判断校领导）
                    //判断领导
                    if (!StringUtils.isBlank(departmentName)){
                        //判断系管
                        if (!StringUtils.isBlank(departmentLeaderNum)||!StringUtils.isBlank(departmentLeader)){
                            if (StringUtils.isBlank(departmentLeader)||StringUtils.isBlank(departmentLeaderNum)){
                                return CommonResult.error(500,"请检查系管理员工号和姓名是否填写");
                            }
                            boolean isExist = false;
                            //循环判断
                            for (TeacherInfo info:teacherInfos) {
                                if (info.getWorkNumber().equals(departmentLeaderNum)){
                                    if (info.getTeacherName().equals(departmentLeader)){
                                        //放入关联参数中
                                        departmentLeaderInfo = info;
                                        isExist = true;
                                    }else {
                                        return CommonResult.error(500,"教职工【"+departmentLeader+"】与工号【"+departmentLeaderNum+"】不符");
                                    }
                                    break;
                                }
                            }
                            //如果教师不存在，则新增一个教师信息
                            if (!isExist){
                                //添加一个教师
                                TeacherInfo teacherInfo = teacherInfoService.addNewTeacher(userDto, departmentLeader, departmentLeaderNum);
                                if (teacherInfo != null){
                                    log.info("添加教师"+departmentLeader+"成功！");
                                    departmentLeaderInfo = teacherInfo;
                                    teacherInfos.add(teacherInfo);
                                    depIsNew = true;
                                }else {
                                    log.info("添加教师"+departmentLeader+"失败！");
                                }
                            }
                        }
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
                    /*添加二级学院*/
                    if (!StringUtils.isEmpty(departmentName)){
                        //查看二级学院是否存在
                        for (DepartmentInfo info:departmentInfos) {
                            //判断如果同个学校中有该名称的则关联
                            if (info.getSchoolId().equals(schoolIdDefualt)){
                                if (info.getDepartmentName().equals(departmentName)){
                                    departmentId = info.getId();
                                    break;
                                }
                            }
                        }
                        //不存在则添加
                        if(departmentId==null){
                            //新建二级学院
                            DepartmentInfo departmentInfo = new DepartmentInfo();
                            //关联二级学院
                            departmentInfo.setSchoolId(schoolIdDefualt);
                            //二级学院名称
                            departmentInfo.setDepartmentName(departmentName);
                            //创建人
                            departmentInfo.setCreateBy(createBy);
                            //插入数据库
                            boolean save = this.save(departmentInfo);
                            if (save){
                                departmentId=departmentInfo.getId();
                                //添加到数组中
                                departmentInfos.add(departmentInfo);
                            }else {
                                throw new RuntimeException("二级学院"+departmentName+"添加异常");
                            }
                        }
                        /*二级学院添加成功判断是否添加系管*/
                        if (departmentLeaderInfo != null){
                            //查询是否有该领导
                            QueryWrapper<DepartmentTeacherRelation> relationQueryWrapper = new QueryWrapper<>();
                            relationQueryWrapper.eq("department_id",departmentId).eq("deleted",0)
                                    .eq("teacher_id",departmentLeaderInfo.getId()).eq("relation_type",1);
                            //判断数据库是否有数据
                            int count = departmentTeacherService.count(relationQueryWrapper);
                            //没有的话添加
                            if (count<=0) {
                                //新建关联实体类
                                DepartmentTeacherRelation departmentTeacherRelation = new DepartmentTeacherRelation();
                                //关联学校id
                                departmentTeacherRelation.setDepartmentId(departmentId);
                                //关联教职工id
                                departmentTeacherRelation.setTeacherId(departmentLeaderInfo.getId());
                                departmentTeacherRelation.setRelationType(1);
                                departmentTeacherRelation.setCreateBy(createBy);
                                //放入批量添加数组
                                departmentTeacherRelations.add(departmentTeacherRelation);

                                if (departmentLeaderInfo.getGender() == null){
                                    departmentLeaderInfo.setDepartmentId(departmentId);
                                    departmentLeaderInfo.setDepartmentName(departmentName);
                                    updateTeacherInfos.add(departmentLeaderInfo);
                                }
                                /*判断是否需要添加权限*/
                                Boolean checkTeacher = checkTeacher(userRoles, departmentLeaderInfo.getUserId(), 3);
                                //返回ture添加权限
                                if (!checkTeacher) {
                                    //添加权限
                                    AuthUserRole role = new AuthUserRole();
                                    role.setRoleId(3);
                                    role.setUserId(departmentLeaderInfo.getUserId());
                                    role.setCreateBy(createBy);
                                    userRoles.add(role);
                                }
                                if (depIsNew){
                                    departmentLeaderInfo.setSchoolName(schoolName);
                                    departmentLeaderInfo.setSchoolId(schoolIdDefualt);
                                    departmentLeaderInfo.setDepartmentId(departmentId);
                                    departmentLeaderInfo.setDepartmentName(departmentName);
                                    updateTeacherInfos.add(departmentLeaderInfo);
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
                    //不存在则添加
                    if(gradeId==null){
                        //新建班级
                        GradeInfo gradeInfo = new GradeInfo();
                        //关联学校
                        gradeInfo.setSchoolId(schoolIdDefualt);
                        //年级名称
                        gradeInfo.setGradeYear(gradeName);
                        //创建人
                        gradeInfo.setCreateBy(createBy);
                        //插入数据库
                        boolean save = gradeInfoService.save(gradeInfo);
                        if (save){
                            //添加到数组中
                            gradeInfos.add(gradeInfo);
                            gradeId = gradeInfo.getId();
                        }else {
                            throw new RuntimeException("班级"+gradeName+"添加异常");
                        }
                    }
                    //判断添加专业
                    if (!StringUtils.isBlank(majorName)){
                        //查看是否存在
                        for (MajorInfo info:majorInfos) {
                            //判断如果同个学校中有该名称的则关联
                            if (info.getSchoolId().equals(schoolIdDefualt)){
                                if (info.getDepartmentId().equals(departmentId)){
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
                        majorInfo.setDepartmentId(departmentId);
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
                                if (info.getDepartmentId().equals(departmentId)){
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
                            classInfo.setDepartmentId(departmentId);
                            //专业id
                            classInfo.setMajorId(majorId);
                            //年级id
                            classInfo.setGradeId(gradeId);
                            //班级名称
                            classInfo.setClassName(className);
                            //创建人
                            classInfo.setCreateBy(createBy);
                            //插入数据库
                            boolean save = classInfoService.save(classInfo);
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
                                classLeaderInfo.setDepartmentId(departmentId);
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
                                classTeacherRelation.setRelationType(1);
                                classTeacherRelation.setCreateBy(createBy);
                                classTeacherRelation.setTeacherName(classTeacher);
                                //放入批量添加数组
                                classTeacherRelations.add(classTeacherRelation);
                            }
                        }
                    }
                }
                //循环结束
                //校领导、系管、班主任、用户权限批量添加
                if (userRoles.size()>0){
                    boolean b = authUserRoleService.saveBatch(userRoles, 1000);
                    if (!b){
                        throw new RuntimeException("用户权限导入异常");
                    }
                }
                if (departmentTeacherRelations.size()>0){
                    boolean b = departmentTeacherService.saveBatch(departmentTeacherRelations, 1000);
                    if (!b){
                        throw new RuntimeException("二级学院管理员导入异常");
                    }
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
