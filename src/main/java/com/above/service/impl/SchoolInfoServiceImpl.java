package com.above.service.impl;

import com.above.bean.excel.SchoolInfoExcelData;
import com.above.config.easyExcel.SchoolInfoExcelListener;
import com.above.dao.SchoolInfoMapper;
import com.above.dao.TeacherInfoMapper;
import com.above.dao.UserAccountMapper;
import com.above.dao.UserMapper;
import com.above.dto.LeaderList;
import com.above.dto.SchoolListWithLeader;
import com.above.dto.SchoolSimpleDto;
import com.above.dto.UserDto;
import com.above.po.*;
import com.above.service.*;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.ClassVo;
import com.above.vo.DepartmentVo;
import com.above.vo.SchoolVo;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
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
 * 学校信息 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Slf4j
@Service
public class SchoolInfoServiceImpl extends ServiceImpl<SchoolInfoMapper, SchoolInfo> implements SchoolInfoService {

     @Autowired
     private  AuthUserRoleService authUserRoleService;
     @Autowired
     private  DepartmentTeacherRelationService departmentTeacherRelationService;
     @Autowired
     private  DepartmentInfoService departmentInfoService;
     @Autowired
     private  SchoolTeacherRelationService schoolTeacherRelationService;
     @Autowired
     private  UserAccountMapper userAccountMapper;
     @Autowired
     private  UserMapper userMapper;
    @Autowired
    private TeacherInfoMapper teacherInfoMapper;
    @Autowired
    private TeacherInfoService teacherInfoService;
    @Autowired
    private ClassInfoService classInfoService;
    @Autowired
    private ClassTeacherRelationService classTeacherRelationService;

    @Autowired
    private MajorInfoService majorInfoService;
    @Autowired
    private GradeInfoService gradeInfoService;




    /**
     * @Description: 添加学校信息
     * @Author: LZH
     * @Date: 2022/1/10 13:47
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> addSchoolInfo(UserDto userDto, SchoolVo schoolVo) {

        //获取参数
        Integer createBy = userDto.getId();
        Collection<Integer> teacherIds = schoolVo.getTeacherIdList();

        //判断学校是否存在
        QueryWrapper<SchoolInfo> schoolInfoQueryWrapper = new QueryWrapper<>();
        schoolInfoQueryWrapper.eq("school_name",schoolVo.getSchoolName()).eq("deleted",BaseVo.UNDELETE);
        int count1 = super.count(schoolInfoQueryWrapper);
        if (count1>0){
            return CommonResult.error(500,"学校名已存在");
        }
        SchoolInfo schoolInfo = new SchoolInfo();
        //学校名称
        schoolInfo.setSchoolName(schoolVo.getSchoolName());
        //学校图标
        schoolInfo.setSchoolLogo(schoolVo.getSchoolLogo());
        //学校（禁用）状态：0=正常，1=禁用
        schoolInfo.setStatus(0);
        //设置创建者
        schoolInfo.setCreateBy(createBy);
        //添加学校信息
        boolean saveSchool = super.save(schoolInfo);
        if(saveSchool){
            //添加校级管理
            if (schoolVo.getTeacherIdList() != null && schoolVo.getTeacherIdList().size() > 0){
                CommonResult<Object> result = this.addSchoolLeader(createBy, schoolVo.getTeacherIdList(), schoolInfo.getId(), 1);
                if (!result.isSuccess()){
                    return result;
                }
            }

            return CommonResult.success("添加成功",null);
        }else{
            return CommonResult.error(500,"添加失败");
        }
    }

    /**
     * @Description: 修改学校
     * @Author: LZH
     * @Date: 2022/1/10 16:31
     */
    @Override
    public CommonResult<Object> modifySchoolInfo(UserDto userDto, SchoolVo schoolVo) {

        Integer updateBy = userDto.getId();
        Integer schoolId = schoolVo.getSchoolId();

        //根据id获取要修改的id
        SchoolInfo schoolInfo = super.getById(schoolId);
        //判断学校是否存在
        if (schoolInfo == null){
            return CommonResult.error(500,"该学校不存在");
        }
        if (schoolInfo.getDeleted()==1){
            return CommonResult.error(500,"该学校已删除");
        }
        //判断学校名是否重复
        QueryWrapper<SchoolInfo> schoolInfoQueryWrapper = new QueryWrapper<>();
        schoolInfoQueryWrapper.eq("school_name",schoolVo.getSchoolName()).eq("deleted",BaseVo.UNDELETE).ne("id",schoolId);
        int count1 = super.count(schoolInfoQueryWrapper);
        if (count1>0){
            return CommonResult.error(500,"学校名已存在");
        }
        //设置学校名称
        schoolInfo.setSchoolName(schoolVo.getSchoolName());
        //学校图标
        schoolInfo.setSchoolLogo(schoolVo.getSchoolLogo());
        //设置更新人
        schoolInfo.setUpdateBy(updateBy);
        //根据id更新
        boolean b = super.updateById(schoolInfo);

        try {
            //判断失败返回
            if (!b){
                return CommonResult.error(500,"更新失败");
            }else {
                List<Integer> teacherIds = schoolVo.getTeacherIdList();
                int teacherIds1 = teacherIds.size();
                List<Integer> deleteTeacherIdList = schoolVo.getDeleteTeacherIdList();
                int teacherIds2 = teacherIds.size();
                //去重
                for (int i = 0; i < teacherIds1; i++) {
                    Integer newId = teacherIds.get(i);
                    for (int j = 0; j < teacherIds2 ; j++) {
                        Integer oldId = deleteTeacherIdList.get(j);
                        if (newId.equals(oldId)){
                            teacherIds.remove(newId);
                            deleteTeacherIdList.remove(oldId);
                            --teacherIds1;
                            --teacherIds2;
                        }
                    }
                }
                //添加
                if (teacherIds.size() > 0){
                    Integer error=500;
                    CommonResult<Object> objectCommonResult = this.addSchoolLeader(updateBy,teacherIds,schoolId,1);
                    //判断删除校领导是否成功
                    if (objectCommonResult.getCode().equals(error)){
                        return objectCommonResult;
                    }
                }

                if (deleteTeacherIdList.size()>0){
                    Integer error=500;
                    //删除校领导
                    CommonResult<Object> objectCommonResult = this.deleteSchoolLeader(updateBy,deleteTeacherIdList,schoolId,1);
                    //判断删除校领导是否成功
                    if (objectCommonResult.getCode().equals(error)){
                        return objectCommonResult;
                    }
                }

                return CommonResult.success("修改成功",null);
            }
        }catch (Exception e){
            log.info("异常打印-->"+e.getMessage());
            return CommonResult.error(500,e.getMessage());
        }

    }

    /**
     * @Description: 删除学校
     * @Author: LZH
     * @Date: 2022/1/10 17:08
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> deleteSchoolInfo(UserDto userDto, SchoolVo schoolVo)  {
        //获取参数
        Collection<Integer> schoolIdList = schoolVo.getSchoolIdList();
        Integer updateBy = userDto.getId();

        //判断数组是否为空
        if (schoolIdList == null || schoolIdList.size() <= 0){
            return CommonResult.error(500,"缺少学校id");
        }
        //批量查询
        Collection<SchoolInfo> schoolInfos = this.getBaseMapper().selectBatchIds(schoolIdList);
        if(schoolInfos == null || schoolInfos.size() <= 0){
            return CommonResult.error(500,"缺少学校信息");
        }

        //循环设置删除状态和更新人
        for (SchoolInfo info:schoolInfos) {
            if (info.getDeleted()==1){
                return CommonResult.error(500,"请勿重复删除");
            }
            info.setUpdateBy(updateBy);
            info.setDeleted(1);
        }

        //批量删除
        boolean i = this.updateBatchById(schoolInfos);

        //删除成功后，删除相关信息
        if (i){

            try{

                for (Integer id:schoolIdList){
                    //删除关联的领导
                    QueryWrapper<SchoolTeacherRelation> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("school_id",id).eq("deleted",BaseVo.UNDELETE);
                    Collection<SchoolTeacherRelation> schoolTeacherRelations = schoolTeacherRelationService.list(queryWrapper);
                    //如果存在则进行删除
                    if (schoolTeacherRelations.size()>0){
                        //循环设置删除状态和更新人
                        for (SchoolTeacherRelation info:schoolTeacherRelations) {
                            info.setUpdateBy(updateBy);
                            info.setDeleted(1);
                            //删除领导权限
                            CommonResult<Object> result = deleteSchoolRole(info.getTeacherId(), info.getId());
                            //判断是否成功
                            if (result.getCode()==500){
                                throw new RuntimeException(result.getMessage());
                            }else {
                                log.info(result.getMessage());
                            }
                        }
                        //批量删除
                        boolean isSuccess = schoolTeacherRelationService.updateBatchById(schoolTeacherRelations,1000);
                        //若删除失败则回滚
                        if(!isSuccess){
                            throw new RuntimeException("领导信息删除错误");
                        }
                    }

                    //删除相关二级学院
                    QueryWrapper<DepartmentInfo> departmentInfoQueryWrapper = new QueryWrapper<>();
                    departmentInfoQueryWrapper.eq("school_id",id).eq("deleted",BaseVo.UNDELETE);
                    Collection<DepartmentInfo> departmentInfos = departmentInfoService.list(departmentInfoQueryWrapper);
                    List<Integer> departmentIds = new ArrayList<>();
                    departmentInfos.forEach(info -> {
                        departmentIds.add(info.getId());
                    });

                    DepartmentVo departmentVo = new DepartmentVo();
                    departmentVo.setDepartmentIdList(departmentIds);
                    CommonResult<Object> deleteDepartment = departmentInfoService.deleteDepartment(userDto, departmentVo);

                    if (!deleteDepartment.isSuccess()){
                        throw new RuntimeException(deleteDepartment.getMessage());
                    }

                    //批量删除
                    boolean isSuccess = departmentInfoService.updateBatchById(departmentInfos,1000);
                    //若删除失败则抛出异常回滚
                    if (!isSuccess){
                        throw new RuntimeException("二级学院信息删除错误");
                    }
                }
            }catch (RuntimeException e){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                log.info("异常打印-->"+e.getMessage());
                return CommonResult.error(500,e.getMessage());
            }
            return CommonResult.success("删除成功",null);
        }else {
            return CommonResult.error(500,"删除失败");
        }

    }

    /**
     * @Description: 获取学校列表不分页
     * @Author: LZH
     * @Date: 2022/1/10 19:22
     */
    @Override
    public CommonResult<Object> getSchoolWithoutPage(UserDto userDto, BaseVo vo) {
        //获取用户身份
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        //设置查找状态正常的学校
        QueryWrapper<SchoolInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",0).eq("deleted",BaseVo.UNDELETE).orderByDesc("id");
        //根据角色判断
        if (roleCode.equals(AuthRole.SCHOOL_ADMIN)){
            queryWrapper.in("id",userDto.getSchoolIds());
        }else if (roleCode.equals(AuthRole.DEPARTMENT_ADMIN)){
            List<DepartmentInfo> departmentInfos = departmentInfoService.getBaseMapper().selectBatchIds(userDto.getDepartmentIds());
            ArrayList<Integer> schoolIds = new ArrayList<>();
            departmentInfos.forEach(departmentInfo -> schoolIds.add(departmentInfo.getSchoolId()));
            queryWrapper.in("id",schoolIds);
        }
        //若有传key则筛选
        if (vo.getKey() != null){
            queryWrapper.like("school_name", vo.getKey());
        }
        //搜索
        List<SchoolInfo> list = super.list(queryWrapper);

        ArrayList<SchoolSimpleDto> returnList = new ArrayList<>();

        //循环取出有用的数据返回
        for (SchoolInfo info:list) {
            SchoolSimpleDto schoolSimpleDto = new SchoolSimpleDto();
            BeanUtils.copyProperties(info,schoolSimpleDto);
            //放入返回数组
            returnList.add(schoolSimpleDto);
        }
        Map<String, Object> returnMap = new HashMap<>(10);
        returnMap.put(BaseVo.LIST,returnList);

        return CommonResult.success(returnMap);
    }

    /**
     * @Description: 获取学校列表分页
     * @Author: LZH
     * @Date: 2022/1/10 19:22
     */
    @Override
    public CommonResult<Object> getSchoolPageList(UserDto userDto, BaseVo vo) {
        //获取用户身份
        String roleCode = userDto.getUserRoleDto().getRoleCode();

        //设置分页参数
        Page<SchoolInfo> page = new Page<>(vo.getPage(), vo.getSize());
        //设置查找状态正常的学校
        QueryWrapper<SchoolInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",0).eq("deleted",BaseVo.UNDELETE).orderByDesc("id");
        //根据角色判断
        if (roleCode.equals(AuthRole.SCHOOL_ADMIN)){
            queryWrapper.in("id",userDto.getSchoolIds());
        }else if (roleCode.equals(AuthRole.DEPARTMENT_ADMIN)){
            List<DepartmentInfo> departmentInfos = departmentInfoService.getBaseMapper().selectBatchIds(userDto.getDepartmentIds());
            ArrayList<Integer> schoolIds = new ArrayList<>();
            departmentInfos.forEach(departmentInfo -> schoolIds.add(departmentInfo.getSchoolId()));
            queryWrapper.in("id",schoolIds);
        }
        //若有传key则筛选
        if (vo.getKey() != null){
            queryWrapper.like("school_name", vo.getKey());
        }

        IPage<SchoolInfo> iPage = super.page(page, queryWrapper);
        //获取分页集合
        List<SchoolInfo> schoolInfos = iPage.getRecords();
        //新建返回数组
        List<SchoolListWithLeader> returnList = new ArrayList<>();

        //循环获取该校领导信息
        for (SchoolInfo info:schoolInfos) {
            //根据学校id获取学校的领导
            List<LeaderList> leaderListBySchoolId = schoolTeacherRelationService.getLeaderListBySchoolId(info.getId(),1);
            //新建返回类
            SchoolListWithLeader schoolListWithLeader = new SchoolListWithLeader();
            //拷贝school信息
            BeanUtils.copyProperties(info,schoolListWithLeader);
            //放入领导数组
            schoolListWithLeader.setList(leaderListBySchoolId);
            //放入返回数组
            returnList.add(schoolListWithLeader);
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
     * @Description: 删除校领导
     * @Author: LZH
     * @Date: 2022/1/12 19:42
     */
    @Override
    public CommonResult<Object> deleteSchoolLeader(Integer updateBy, Collection<Integer> deleteTeacherIdList,Integer schoolId, Integer relationType) {
        //批量查询
        List<TeacherInfo> teacherInfos = teacherInfoMapper.selectBatchIds(deleteTeacherIdList);
        //循环判断删除
        for (TeacherInfo info: teacherInfos) {
            //获取id
            Integer id = info.getId();
            //创建查找
            QueryWrapper<SchoolTeacherRelation> teacherRelationQueryWrapper = new QueryWrapper<>();
            teacherRelationQueryWrapper.eq("deleted",BaseVo.UNDELETE).eq("school_id",schoolId)
                    .eq("teacher_id",id).eq("relation_type",relationType);
            //获取该校领导
            SchoolTeacherRelation one = schoolTeacherRelationService.getOne(teacherRelationQueryWrapper);
            if (one == null){
                return CommonResult.error(500,"该校领导已移除");
            }else {
                //设置更新人
                one.setUpdateBy(updateBy);
                //删除状态
                one.setDeleted(1);
                boolean b = schoolTeacherRelationService.updateById(one);
                if (!b){
                    CommonResult.error(500,"删除失败");
                }
            }
            /*删除后判断该教职工是否需要删除领导角色*/
            QueryWrapper<SchoolTeacherRelation> relationQueryWrapper = new QueryWrapper<>();
            relationQueryWrapper.eq("deleted",BaseVo.UNDELETE).eq("teacher_id",id).eq("relation_type",relationType);
            List<SchoolTeacherRelation> list = schoolTeacherRelationService.list(relationQueryWrapper);
            int count = list.size();
            //若无关联学校则删除权限
            if (count<=0){
                Integer roleId = 2;
                QueryWrapper<AuthUserRole> roleQueryWrapper = new QueryWrapper<>();
                roleQueryWrapper.eq("user_id",info.getUserId()).eq("role_id",roleId)
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
        return CommonResult.success("修改完成");
    }

    /**
     * @param createBy 操作者
     * @param teacherIdList 教师id列比奥
     * @param schoolId 学校id
     * @param relationType 关联类型 1-校管理
     * @Description: 添加校领导
     * @Author: LZH
     * @Date: 2022/1/12 17:34
     */
    @Override
    public CommonResult<Object> addSchoolLeader(Integer createBy, Collection<Integer> teacherIdList, Integer schoolId, Integer relationType) {
        //批量查找教师信息
        List<TeacherInfo> teacherInfos = teacherInfoMapper.selectBatchIds(teacherIdList);
        //判断教师是否存在
        if (teacherInfos == null || teacherInfos.size() <= 0){
            throw new RuntimeException("教职工信息不存在");
        }
        if (teacherIdList.size()!= teacherInfos.size()){
            throw new RuntimeException("有教职工信息不存在");
        }
        //批量添加数组
        Collection<SchoolTeacherRelation> teacherRelations = new ArrayList<>();
        Collection<AuthUserRole> roles = new ArrayList<>();
        //循环添加教职工为领导
        for (TeacherInfo info:teacherInfos) {
            // 查看该教职工是否状态为删除
            if (info.getDeleted()==1) {
                throw new RuntimeException("【" + info.getTeacherName() + "】该教职工信息不存在");
            }
            if (!info.getSchoolId().equals(schoolId)) {
                throw new RuntimeException("【" + info.getTeacherName() + "】非本校教职工");
            }
            //获取老师id
            Integer id = info.getId();
            //查询该教职工已经绑定该学校
            QueryWrapper<SchoolTeacherRelation> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("school_id",schoolId).eq("relation_type",relationType).eq("teacher_id",id).eq("deleted",BaseVo.UNDELETE);
            int count = schoolTeacherRelationService.count(queryWrapper);
            //若无记录则添加
            if (count<=0){
                //添加新领导
                SchoolTeacherRelation relation = new SchoolTeacherRelation();
                //学校id
                relation.setSchoolId(schoolId);
                //教师id
                relation.setTeacherId(id);
                //关联类型 1-学校领导
                relation.setRelationType(1);
                //创建者
                relation.setCreateBy(createBy);
                //添加到批量添加中
                teacherRelations.add(relation);

                /*添加关联角色*/
                boolean hasRole = authUserRoleService.checkUserRole(info.getUserId(), 2);
                if (!hasRole){
                    AuthUserRole authUserRole = new AuthUserRole();
                    authUserRole.setUserId(info.getUserId());
                    //二级学院管理员
                    authUserRole.setRoleId(2);
                    //创建者
                    authUserRole.setCreateBy(createBy);
                    //添加到批量添加数组
                    roles.add(authUserRole);
                }
            }
        }

        //判断是否要添加
        if (teacherRelations.size()>0){
            boolean save = schoolTeacherRelationService.saveBatch(teacherRelations);
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
        return CommonResult.success("添加成功");
    }

    /**
     * @Description: 刪除领导判断权限
     * @Author: LZH
     * @Date: 2022/1/13 8:48
     */
    @Override
    public CommonResult<Object> deleteSchoolRole(Integer teacherId, Integer relationId){
        //查找该教师是否还有管理的学校
        QueryWrapper<SchoolTeacherRelation> schoolInfoQueryWrapper = new QueryWrapper<>();
        schoolInfoQueryWrapper.eq("teacher_id",teacherId).ne("id",relationId).eq("deleted",BaseVo.UNDELETE);
        //查询数量
        int count = schoolTeacherRelationService.count(schoolInfoQueryWrapper);
        //若数量为0，则删除领导权限
        if (count<=0){
            //获取教师User_id
            TeacherInfo teacherInfo = teacherInfoMapper.selectById(teacherId);
            //根据User_id查询
            QueryWrapper<AuthUserRole> authUserRoleQueryWrapper = new QueryWrapper<>();
            authUserRoleQueryWrapper.eq("user_id",teacherInfo.getUserId()).eq("deleted",BaseVo.UNDELETE)
                    .eq("role_id",2);
            AuthUserRole one = authUserRoleService.getOne(authUserRoleQueryWrapper);
            //若存在则删除
            if (one != null){
                one.setDeleted(1);
                boolean b = authUserRoleService.updateById(one);
                if (!b){
                    return CommonResult.error(500,"权限删除异常");
                }
            }
        }
        return CommonResult.success("删除成功",null);
    }

    /**
     * @param userDto
     * @param file
     * @Description: 院校信息导入
     * @Author: LZH
     * @Date: 2022/1/17 10:06
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> importSchoolInfo(UserDto userDto, MultipartFile file) {
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
                //获取学校列表
                QueryWrapper<SchoolInfo> schoolQueryWrapper = new QueryWrapper<>();
                schoolQueryWrapper.eq("deleted",BaseVo.UNDELETE);
                List<SchoolInfo> schoolInfos = this.list(schoolQueryWrapper);
                //二级学院列表
                QueryWrapper<DepartmentInfo> departmentQueryWrapper = new QueryWrapper<>();
                departmentQueryWrapper.eq("deleted",BaseVo.UNDELETE);
                List<DepartmentInfo> departmentInfos = departmentInfoService.list(departmentQueryWrapper);
                //教师列表
                QueryWrapper<TeacherInfo> teacherQueryWrapper = new QueryWrapper<>();
                teacherQueryWrapper.eq("deleted",BaseVo.UNDELETE);
                List<TeacherInfo> teacherInfos = teacherInfoService.list(teacherQueryWrapper);
                //班级列表
                QueryWrapper<ClassInfo> classQueryWrapper = new QueryWrapper<>();
                classQueryWrapper.eq("deleted",BaseVo.UNDELETE);
                List<ClassInfo> classInfos = classInfoService.list(classQueryWrapper);
                //专业列表
                QueryWrapper<MajorInfo> majorInfoQueryWrapper = new QueryWrapper<>();
                majorInfoQueryWrapper.eq("deleted",BaseVo.UNDELETE);
                List<MajorInfo> majorInfos = majorInfoService.list(majorInfoQueryWrapper);
                //年级列表
                QueryWrapper<GradeInfo> gradeInfoQueryWrapper = new QueryWrapper<>();
                gradeInfoQueryWrapper.eq("deleted",BaseVo.UNDELETE);
                List<GradeInfo> gradeInfos = gradeInfoService.list(gradeInfoQueryWrapper);

                //批量插入集合
                //用户权限
                Collection<AuthUserRole> userRoles = new HashSet<>();
                //教职工关联
                Collection<SchoolTeacherRelation> schoolTeacherRelations = new HashSet<>();
                Collection<DepartmentTeacherRelation> departmentTeacherRelations = new HashSet<>();
                Collection<ClassTeacherRelation> classTeacherRelations = new ArrayList<>();
                Collection<TeacherInfo> updateTeacherInfos = new HashSet<>();

                for (Object o:list){
                    SchoolInfoExcelData data = (SchoolInfoExcelData) o;
                    //获取数据-学校
                    String schoolName = data.getSchoolName();
                    String schoolLeader = data.getSchoolLeader();
                    String schoolLeaderNum = data.getSchoolLeaderNum();
                    //二级学院
                    String departmentName = data.getDepartmentName();
                    String departmentLeader = data.getDepartmentLeader();
                    String departmentLeaderNum = data.getDepartmentLeaderNum();
                    //年级
                    String gradeName = data.getGradeName();
                    if (!gradeName.endsWith("级")){
                        gradeName = gradeName + "级";
                    }
                    //专业
                    String majorName = data.getMajorName();
                    //班级
                    String className = data.getClassName();
                    String classLeader = data.getClassLeader();
                    String classLeaderNum = data.getClassLeaderNum();
                    String classTeacher = data.getClassTeacher();
                    //关联参数
                    Integer schoolId = null;
                    Integer departmentId = null;
                    Integer classId = null;
                    Integer majorId = null;
                    Integer gradeId = null;
                    //关联领导参数
                    boolean schoolIsNew = false;
                    TeacherInfo schoolLeaderInfo = null;
                    boolean depIsNew = false;
                    TeacherInfo departmentLeaderInfo = null;
                    boolean classIsNew = false;
                    TeacherInfo classLeaderInfo = null;

                    //判断院校信息是否完全存在
                    if (StringUtils.isBlank(schoolName)){
                        return CommonResult.error(500,"请检查学校信息");
                    }

                    //判断三种教职工是否存在
                    //判断领导
                    if (!StringUtils.isBlank(schoolLeaderNum)||!StringUtils.isBlank(schoolLeader)){
                        if (StringUtils.isBlank(schoolLeader)||StringUtils.isBlank(schoolLeaderNum)){
                            return CommonResult.error(500,"请检查学校领导工号和姓名是否填写");
                        }
                        boolean isExist = false;
                        //循环判断
                        for (TeacherInfo info:teacherInfos) {
                            if (info.getWorkNumber().equals(schoolLeaderNum)){
                                if (info.getTeacherName().equals(schoolLeader)){
                                    //放入关联参数中
                                    schoolLeaderInfo = info;
                                    isExist = true;
                                }
                                break;
                            }
                        }
                        //如果教师不存在，则新增一个教师信息
                        if (!isExist){
                            //添加一个教师
                            TeacherInfo teacherInfo = teacherInfoService.addNewTeacher(userDto, schoolLeader, schoolLeaderNum);
                            if (teacherInfo != null){
                                log.info("添加教师"+schoolLeader+"成功！");
                                schoolIsNew = true;
                                schoolLeaderInfo = teacherInfo;
                                teacherInfos.add(teacherInfo);
                            }else {
                                log.info("添加教师"+schoolLeader+"失败！");
                            }
                        }
                    }
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
                                    if (info.getTeacherName().equals(departmentLeader)) {
                                        //放入关联参数中
                                        departmentLeaderInfo = info;
                                        isExist = true;
                                    }
                                    break;
                                }
                            }
                            //如果教师不存在，则新增一个教师信息
                            if (!isExist){
                                //添加一个教师
                                TeacherInfo teacherInfo = teacherInfoService.addNewTeacher(userDto, departmentLeader, departmentLeaderNum);
                                if (teacherInfo != null){
                                    log.info("添加教师"+schoolLeader+"成功！");
                                    depIsNew = true;
                                    departmentLeaderInfo = teacherInfo;
                                    teacherInfos.add(teacherInfo);
                                }else {
                                    log.info("添加教师"+schoolLeader+"失败！");
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
                                    classIsNew =true;
                                    classLeaderInfo = teacherInfo;
                                    teacherInfos.add(teacherInfo);
                                }else {
                                    log.info("添加教师"+classLeader+"失败！");
                                }
                            }
                        }
                    }
                    /*添加学校*/
                    //循环查询学校是否存在
                    for (SchoolInfo info:schoolInfos) {
                        if (info.getSchoolName().equals(schoolName)){
                            schoolId = info.getId();
                            break;
                        }
                    }
                    //判断学校是否以及存在，如果不存在则添加
                    if(schoolId==null){
                        //新建学校
                        SchoolInfo schoolInfo = new SchoolInfo();
                        //学校名称
                        schoolInfo.setSchoolName(schoolName);
                        //创建人
                        schoolInfo.setCreateBy(createBy);
                        schoolInfo.setStatus(0);
                        //插入数据库
                        boolean save = this.save(schoolInfo);
                        if (save){
                            schoolId=schoolInfo.getId();
                            //添加到数组中
                            schoolInfos.add(schoolInfo);
                        }else {
                            return CommonResult.error(500,"学校"+schoolName+"添加异常");
                        }
                    }
                    /*学校添加成功判断是否添加领导*/
                    if (schoolLeaderInfo != null){

                        QueryWrapper<SchoolTeacherRelation> relationQueryWrapper = new QueryWrapper<>();
                        relationQueryWrapper.eq("school_id",schoolId).eq("deleted",0)
                                .eq("teacher_id",schoolLeaderInfo.getId()).eq("relation_type",1);
                        int count = schoolTeacherRelationService.count(relationQueryWrapper);
                        if (count<=0){
                            //新建关联实体类
                            SchoolTeacherRelation schoolTeacherRelation = new SchoolTeacherRelation();
                            //关联学校id
                            schoolTeacherRelation.setSchoolId(schoolId);
                            //关联教职工id
                            schoolTeacherRelation.setTeacherId(schoolLeaderInfo.getId());
                            schoolTeacherRelation.setRelationType(1);
                            schoolTeacherRelation.setCreateBy(createBy);
                            //放入批量添加数组
                            schoolTeacherRelations.add(schoolTeacherRelation);
                            /*判断是否需要添加权限*/
                            Boolean checkTeacher = checkTeacher(userRoles,schoolLeaderInfo.getUserId(), 2);
                            //返回ture添加权限
                            if (!checkTeacher){
                                //添加权限
                                AuthUserRole role = new AuthUserRole();
                                role.setRoleId(2);
                                role.setUserId(schoolLeaderInfo.getUserId());
                                role.setCreateBy(createBy);
                                userRoles.add(role);
                            }
                            //更新教师管理学校id
                            if (schoolIsNew){
                                schoolLeaderInfo.setSchoolId(schoolId);
                                schoolLeaderInfo.setSchoolName(schoolName);
                                updateTeacherInfos.add(schoolLeaderInfo);
                            }
                        }
                    }

                    /*添加二级学院*/
                    if (!StringUtils.isEmpty(departmentName)){
                        //查看二级学院是否存在
                        for (DepartmentInfo info:departmentInfos) {
                            //判断如果同个学校中有该名称的则关联
                            if (info.getSchoolId().equals(schoolId)){
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
                            departmentInfo.setSchoolId(schoolId);
                            //二级学院名称
                            departmentInfo.setDepartmentName(departmentName);
                            //创建人
                            departmentInfo.setCreateBy(createBy);
                            //插入数据库
                            boolean save = departmentInfoService.save(departmentInfo);
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
                            int count = departmentTeacherRelationService.count(relationQueryWrapper);
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

                                if (depIsNew){
                                    departmentLeaderInfo.setSchoolName(schoolName);
                                    departmentLeaderInfo.setSchoolId(schoolId);
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
                            }
                        }
                    }
                    //判断添加年级
                    if (!StringUtils.isBlank(gradeName)){
                        //查看是否存在
                        for (GradeInfo info:gradeInfos) {
                            //判断如果同个学校中有该名称的则关联
                            if (info.getSchoolId().equals(schoolId)){
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
                        gradeInfo.setSchoolId(schoolId);
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
                            if (info.getSchoolId().equals(schoolId)){
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
                        majorInfo.setSchoolId(schoolId);
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
                            if (info.getSchoolId().equals(schoolId)){
                                //二级学院名称
                                if (info.getDepartmentId().equals(departmentId)){
                                    if (info.getGradeId().equals(gradeId)){
                                        if (info.getMajorId().equals(majorId)){
                                            //班级名称相同
                                            if (info.getClassName().equals(className)){
                                                classId = info.getId();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //不存在则添加
                        if(classId==null){
                            //新建班级
                            ClassInfo classInfo = new ClassInfo();
                            //关联学校
                            classInfo.setSchoolId(schoolId);
                            //关联二级学院
                            classInfo.setDepartmentId(departmentId);
                            //班级名称
                            classInfo.setClassName(className);
                            //专业id
                            classInfo.setMajorId(majorId);
                            //年级id
                            classInfo.setGradeId(gradeId);
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
                                classTeacherRelation.setRelationType(1);
                                classTeacherRelation.setCreateBy(createBy);
                                classTeacherRelation.setTeacherName(classLeaderInfo.getTeacherName());
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
                                if (classIsNew){
                                    classLeaderInfo.setSchoolName(schoolName);
                                    classLeaderInfo.setSchoolId(schoolId);
                                    classLeaderInfo.setDepartmentId(departmentId);
                                    classLeaderInfo.setDepartmentName(departmentName);
                                    updateTeacherInfos.add(classLeaderInfo);
                                }
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
                //循环结束
                //校领导用户权限批量添加
                if (userRoles.size()>0){
                    boolean b = authUserRoleService.saveBatch(userRoles, 1000);
                    if (!b){
                        throw new RuntimeException("用户权限导入异常");
                    }
                }
                if (schoolTeacherRelations.size() > 0){
                    boolean b = schoolTeacherRelationService.saveBatch(schoolTeacherRelations, 1000);
                    if (!b){
                        throw new RuntimeException("校领导导入异常");
                    }
                }
                if (departmentTeacherRelations.size()>0){
                    boolean b = departmentTeacherRelationService.saveBatch(departmentTeacherRelations, 1000);
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
        }catch (RuntimeException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("异常打印-->"+e.getMessage());
            throw new RuntimeException(e.getMessage());
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
