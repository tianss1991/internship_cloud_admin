package com.above.service.impl;

import com.above.dto.AllotInternshipPlanInfoDto;
import com.above.dto.InternshipPlanInfoDto;
import com.above.dto.UserDto;
import com.above.po.*;
import com.above.dao.InternshipPlanInfoMapper;
import com.above.service.*;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.InternshipPlanInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 实习计划表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Service
public class InternshipPlanInfoServiceImpl extends ServiceImpl<InternshipPlanInfoMapper, InternshipPlanInfo> implements InternshipPlanInfoService {

    @Autowired
    private GradeInfoService gradeInfoService;
    @Autowired
    private MajorInfoService majorInfoService;
    @Autowired
    private InternshipWithTeacherService internshipWithTeacherService;
    @Autowired
    private InternshipWithStudentService internshipWithStudentService;

    /**
     *@author: GG
     *@data: 2022/7/12 8:53
     *@function:新增或修改实习计划
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> addandmodifyInternshipPlanInfo(InternshipPlanInfoVo vo, UserDto userDto) {
        Integer schoolId = 0;
        Integer departmentId = 0;
        //获取参数
        if(userDto.getUserRoleDto().getRoleCode().equals("admin")){
            schoolId = vo.getSchoolId();
            departmentId = vo.getDepartmentId();
        }else if(userDto.getUserRoleDto().getRoleCode().equals("schoolAdmin")){
            schoolId = userDto.getTeacherInfo().getSchoolId();
            departmentId = vo.getDepartmentId();
        }else if(userDto.getUserRoleDto().getRoleCode().equals("departmentAdmin")){
            schoolId = userDto.getTeacherInfo().getSchoolId();
            departmentId = userDto.getTeacherInfo().getDepartmentId();
        }
        Integer id = userDto.getId();
        String planTitle = vo.getPlanTitle();
        Integer gradeId = vo.getGradeId();
        Integer majorId = vo.getMajorId();
        String gradation = vo.getGradation();
        Date startTime = vo.getStartTime();
        Date endTime = vo.getEndTime();
        String purpose = vo.getPurpose();
        String required = vo.getRequired();
        String content = vo.getContent();
        Integer signSet = vo.getSignSet();
        Integer dailyCount = vo.getDailyCount();
        Integer dailyWordCount = vo.getDailyWordCount();
        Integer weekCount = vo.getWeekCount();
        Integer weekWordCount = vo.getWeekWordCount();
        Integer monthCount = vo.getMonthCount();
        Integer monthWordCount = vo.getMonthWordCount();
        Integer summarizeCount = vo.getSummarizeCount();
        Integer summarizeWordCount = vo.getSummarizeWordCount();
        Integer signTimes = vo.getSignTimes();

        //判断专业和年级是否存在
        MajorInfo majorInfo = majorInfoService.getById(majorId);
        if(majorInfo == null){
            return CommonResult.error(500,"专业不存在");
        }
        GradeInfo gradeInfo = gradeInfoService.getById(gradeId);
        if(gradeInfo == null){
            return CommonResult.error(500,"年级不存在");
        }

        //新建对象
        InternshipPlanInfo internshipPlanInfo = new InternshipPlanInfo();
        //存入数据
        internshipPlanInfo.setSchoolId(schoolId);
        internshipPlanInfo.setDepartmentId(departmentId);
        internshipPlanInfo.setPlanTitle(planTitle);
        internshipPlanInfo.setGradeId(gradeId);
        internshipPlanInfo.setMajorId(majorId);
        internshipPlanInfo.setGradation(gradation);
        internshipPlanInfo.setStartTime(startTime);
        internshipPlanInfo.setEndTime(endTime);
        internshipPlanInfo.setPurpose(purpose);
        internshipPlanInfo.setRequired(required);
        internshipPlanInfo.setContent(content);
        internshipPlanInfo.setSignSet(signSet);
        internshipPlanInfo.setDailyCount(dailyCount);
        internshipPlanInfo.setDailyWordCount(dailyWordCount);
        internshipPlanInfo.setWeekCount(weekCount);
        internshipPlanInfo.setWeekWordCount(weekWordCount);
        internshipPlanInfo.setMonthCount(monthCount);
        internshipPlanInfo.setMonthWordCount(monthWordCount);
        internshipPlanInfo.setSummarizeCount(summarizeCount);
        internshipPlanInfo.setSummarizeWordCount(summarizeWordCount);
        internshipPlanInfo.setSignTimes(signTimes);
        internshipPlanInfo.setStatus(1);
        //查询是否已经存在实习计划
        QueryWrapper<InternshipPlanInfo> queryWrapper = new QueryWrapper();
        queryWrapper.eq("deleted", BaseVo.UNDELETE)
                .eq("school_id",schoolId)
                .eq("department_id",departmentId)
                .eq("grade_id",gradeId)
                .eq("major_id",majorId)
                .le("start_time",startTime)
                .in("status",1,3)
                .ge("end_time",startTime);
        queryWrapper.or()
                .le("start_time",endTime)
                .ge("end_time",endTime)
                .eq("deleted", BaseVo.UNDELETE)
                .eq("school_id",schoolId)
                .eq("department_id",departmentId)
                .eq("grade_id",gradeId)
                .in("status",1,3)
                .eq("major_id",majorId);
        queryWrapper.or()
                .ge("start_time",startTime)
                .le("end_time",endTime)
                .eq("deleted", BaseVo.UNDELETE)
                . eq("school_id",schoolId)
                .eq("department_id",departmentId)
                .eq("grade_id",gradeId)
                .in("status",1,3)
                .eq("major_id",majorId);
        InternshipPlanInfo planInfo = this.getOne(queryWrapper);
        if(planInfo != null){
            return CommonResult.error(500,"当前时间段已存在实习申请，无法重复申请");
        }
        //修改
        if(vo.getId() != null){
            internshipPlanInfo.setId(vo.getId());
            internshipPlanInfo.setUpdateBy(id);
        }else{
            //新增
            internshipPlanInfo.setId(null);
            internshipPlanInfo.setCreateBy(id);
        }
        //保存或更新数据
        boolean flag = this.saveOrUpdate(internshipPlanInfo);
        if(!flag){
            return CommonResult.error(500,"新增或修改实习计划失败");
        }
        return CommonResult.success("新增或修改实习计划成功");
    }

    /**
     *@author: GG
     *@data: 2022/7/12 8:54
     *@function:根据实习计划id拿到内容
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> getInternshipPlanInfoById(InternshipPlanInfoVo vo, UserDto userDto) {

        InternshipPlanInfoDto internshipPlanInfoDto = this.getBaseMapper().getInternshipInfoById(vo);
        if(internshipPlanInfoDto == null){
            return CommonResult.error(500,"找不到实习计划");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("internshipPlanInfo",internshipPlanInfoDto);
        return CommonResult.success(map);
    }

    /**
     *@author: GG
     *@data: 2022/7/12 8:54
     *@function:审核实习计划
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> checkInternshipPlanInfo(InternshipPlanInfoVo vo, UserDto userDto) {
        Integer planId = vo.getPlanId();
        Integer status = vo.getStatus();
        Integer id = userDto.getId();
        InternshipPlanInfo internshipPlanInfo = this.getById(planId);
        if(internshipPlanInfo == null){
            return CommonResult.error(500,"找不到实习计划");
        }else{
            if(internshipPlanInfo.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该条实习计划已被删除");
            }
        }
        if(internshipPlanInfo.getStatus().equals(1)){
            if(status.equals(0)){
                //审核失败
                internshipPlanInfo.setStatus(2);
                internshipPlanInfo.setFailReason(vo.getFailReason());
            }else if(status.equals(1)){
                //审核通过
                internshipPlanInfo.setStatus(3);
            }
            internshipPlanInfo.setUpdateBy(id);
        }else{
            return CommonResult.error(500,"该条实习计划不能审核");
        }
        boolean save = this.updateById(internshipPlanInfo);
        if(!save){
            return CommonResult.error(500,"审核实习计划失败");
        }
        return CommonResult.success("审核实习计划成功");
    }

    /**
     *@author: GG
     *@data: 2022/7/12 8:58
     *@function:实习计划列表
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> internshipPlanInfoList(InternshipPlanInfoVo vo, UserDto userDto) {
        //数组用来批量查询
        Map<String,Object> map = new HashMap<>();
        if(userDto.getUserRoleDto().getRoleCode().equals("schoolAdmin")){
            vo.setSchoolId(userDto.getTeacherInfo().getSchoolId());
        }else if(userDto.getUserRoleDto().getRoleCode().equals("departmentAdmin")){
            vo.setSchoolId(userDto.getTeacherInfo().getSchoolId());
            vo.setDepartmentId(userDto.getTeacherInfo().getDepartmentId());
        }
        List<InternshipPlanInfoDto> internshipPlanInfos = this.baseMapper.getInternshipInfoList(vo);
        Integer count = this.baseMapper.getInternshipInfoListCount(vo);
        map.put(BaseVo.LIST,internshipPlanInfos);
        map.put(BaseVo.TOTAL,count);
        map.put(BaseVo.PAGE,BaseVo.calculationPages(vo.getSize(),count));
        return CommonResult.success(map);
    }

    /**
     *@author: GG
     *@data: 2022/7/13 14:16
     *@function:撤回实习计划
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> withdrawInternshipPlanInfoList(InternshipPlanInfoVo vo, UserDto userDto) {
        Integer planId = vo.getPlanId();
        Integer id = userDto.getId();
        InternshipPlanInfo internshipPlanInfo = this.getById(planId);
        if(internshipPlanInfo == null){
            return CommonResult.error(500,"找不到实习计划");
        }else{
            if(internshipPlanInfo.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该条实习计划已被删除");
            }
        }
        if(internshipPlanInfo.getStatus().equals(1)){
            //设为草稿
            internshipPlanInfo.setStatus(0);
            internshipPlanInfo.setUpdateBy(id);
        }else{
            return CommonResult.error(500,"审核中的实习计划才可以撤回");
        }

        boolean save = this.updateById(internshipPlanInfo);
        if(!save){
            return CommonResult.error(500,"撤回实习计划失败");
        }
        return CommonResult.success("撤回实习计划成功");
    }

    /**
     *@author: GG
     *@data: 2022/7/13 14:16
     *@function:删除实习计划
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> deleteInternshipPlanInfo(InternshipPlanInfoVo vo, UserDto userDto) {
        Integer planId = vo.getPlanId();
        Integer id = userDto.getId();
        InternshipPlanInfo internshipPlanInfo = this.getById(planId);
        if(internshipPlanInfo == null){
            return CommonResult.error(500,"找不到实习计划");
        }else{
            if(internshipPlanInfo.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该条实习计划已被删除");
            }
        }
        //设为草稿
        internshipPlanInfo.setDeleted(BaseVo.DELETE);
        internshipPlanInfo.setUpdateBy(id);
        boolean save = this.updateById(internshipPlanInfo);
        if(!save){
            return CommonResult.error(500,"删除实习计划失败");
        }
        return CommonResult.success("删除实习计划成功");
    }

    /**
    *@author: GG
    *@data: 2022/7/14 10:35
    *@function:分配实习计划
    */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> allotInternshipPlanInfo(InternshipPlanInfoVo vo, UserDto userDto) {
        //获取参数
        Integer teacherId = vo.getTeacherId();
        List<Integer> studentList = vo.getStudentList();
        Integer id = userDto.getId();
        Integer planId = vo.getPlanId();
        //批量添加数组
        List<InternshipWithStudent> students = new ArrayList<>();

        //查找教师是否已分配该实习计划
        QueryWrapper<InternshipWithTeacher> queryWrapper = new QueryWrapper();
        queryWrapper.eq("deleted",BaseVo.UNDELETE).eq("teacher_id",teacherId).eq("relation_plan_id",planId);
        InternshipWithTeacher internship = internshipWithTeacherService.getOne(queryWrapper);
        if(internship != null){
            return CommonResult.error(500,"该指导老师在该实习计划下已分配");
        }
        //查找学生是否已分配该实习计划
        QueryWrapper<InternshipWithStudent> queryWrapper2 = new QueryWrapper();
        queryWrapper2.eq("deleted",BaseVo.UNDELETE).eq("relation_plan_id",planId)
        .in("relation_student_id",studentList);
        List<InternshipWithStudent> internshipWithStudents =internshipWithStudentService.list(queryWrapper2);
        if(internshipWithStudents.size() > 0){
            return CommonResult.error(500,"该学生集合里有学生在该实习计划下已分配");
        }
        //如果教师id不为空，添加
        InternshipWithTeacher internshipWithTeacher = new InternshipWithTeacher();
        internshipWithTeacher.setCreateBy(id);
        internshipWithTeacher.setTeacherId(teacherId);
        internshipWithTeacher.setRelationPlanId(planId);
        boolean save = internshipWithTeacherService.save(internshipWithTeacher);
        if(!save){
            return CommonResult.error(500,"分配实习计划指导老师失败");
        }
        if(studentList.size()>0){
            for(Integer studentId : studentList){
                InternshipWithStudent internshipWithStudent = new InternshipWithStudent();
                internshipWithStudent.setRelationPlanId(planId);
                internshipWithStudent.setRelationStudentId(studentId);
                internshipWithStudent.setRelationTeacherId(teacherId);
                internshipWithStudent.setCreateBy(id);
                students.add(internshipWithStudent);
            }
        }
        if(students.size() > 0){
            boolean save2 = internshipWithStudentService.saveBatch(students);
            if(!save2){
                return CommonResult.error(500,"分配实习计划学生失败");
            }
        }
        return CommonResult.success("分配实习计划成功");
    }

    /**
    *@author: GG
    *@data: 2022/7/14 13:52
    *@function:删除实习计划分配
    */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> deleteAllotInternshipPlanInfo(InternshipPlanInfoVo vo, UserDto userDto) {
        //获取参数
        Integer teacherId = vo.getTeacherId();
        List<Integer> studentList = vo.getStudentList();
        Integer id = userDto.getId();
        Integer planId = vo.getPlanId();
        //批量添加数组
        List<InternshipWithStudent> students = new ArrayList<>();
        //查找教师
        QueryWrapper<InternshipWithTeacher> queryWrapper = new QueryWrapper();
        queryWrapper.eq("deleted",BaseVo.UNDELETE).eq("teacher_id",teacherId).eq("relation_plan_id",planId);
        InternshipWithTeacher internship = internshipWithTeacherService.getOne(queryWrapper);
        if(internship == null){
            throw new RuntimeException("该条实习分配不存在");
        }else{
            internship.setDeleted(BaseVo.DELETE);
            internship.setUpdateBy(id);
            boolean save = internshipWithTeacherService.updateById(internship);
            if(!save){
                throw new RuntimeException("删除实习计划分配教师失败");
            }
        }
        //查找学生
        QueryWrapper<InternshipWithStudent> queryWrapper2 = new QueryWrapper();
        queryWrapper2.eq("deleted",BaseVo.UNDELETE).eq("relation_teacher_id",teacherId).eq("relation_plan_id",planId)
                .in("relation_student_id",studentList);
        List<InternshipWithStudent> internshipWithStudents =internshipWithStudentService.list(queryWrapper2);
        if(internshipWithStudents.size() > 0){
            for(InternshipWithStudent internshipWithStudent : internshipWithStudents){
                internshipWithStudent.setUpdateBy(id);
                internshipWithStudent.setDeleted(BaseVo.DELETE);
                students.add(internshipWithStudent);
            }
        }else{
            throw new RuntimeException("找不到分配学生");
        }
        if(students.size() > 0){
            boolean save2 = internshipWithStudentService.updateBatchById(students);
            if(!save2){
                throw new RuntimeException("删除实习计划分配学生失败");
            }
        }
        return CommonResult.success("删除实习计划分配成功");
    }

    /**
    *@author: GG
    *@data: 2022/7/14 14:51
    *@function:编辑实习计划分配
    */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> modifyAllotInternshipPlanInfo(InternshipPlanInfoVo vo, UserDto userDto) {
        //获取参数
        Integer teacherId = vo.getTeacherId();
        List<Integer> studentList = vo.getStudentList();
        //旧的教师id和学生数组
        Integer oldTeacherId = vo.getOldTeacherId();
        List<Integer> oldStudentList = vo.getOldStudentList();
        Integer id = userDto.getId();
        Integer planId = vo.getPlanId();
        //批量添加数组
        List<InternshipWithStudent> oldStudents = new ArrayList<>();
        List<InternshipWithStudent> students = new ArrayList<>();
        //数组用来存放修改教师后修改学生关联
        List<InternshipWithStudent> updateStudents = new ArrayList<>();
        //首先判断旧的教师id和新的教师id一不一样
        if (!teacherId.equals(oldTeacherId)) {
            //查找旧老师关联信息并删除
            QueryWrapper<InternshipWithTeacher> queryWrapper = new QueryWrapper();
            queryWrapper.eq("deleted", BaseVo.UNDELETE).eq("teacher_id", oldTeacherId).eq("relation_plan_id", planId);
            InternshipWithTeacher internship = internshipWithTeacherService.getOne(queryWrapper);
            if (internship == null) {
                throw new RuntimeException("该条实习分配不存在");
            } else {
                internship.setDeleted(BaseVo.DELETE);
                internship.setUpdateBy(id);
                boolean save = internshipWithTeacherService.updateById(internship);
                if (!save) {
                    throw new RuntimeException("删除实习计划分配教师失败");
                }
            }
            //添加新老师
            //查找教师是否已分配该实习计划
            QueryWrapper<InternshipWithTeacher> queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("deleted",BaseVo.UNDELETE).eq("teacher_id",teacherId).eq("relation_plan_id",planId);
            InternshipWithTeacher internshipWithTeacher = internshipWithTeacherService.getOne(queryWrapper2);
            if(internshipWithTeacher != null){
                throw new RuntimeException("该指导老师在该实习计划下已分配");
            }else{
                //如果教师id不为空，添加
                InternshipWithTeacher internshipWithTeacher1 = new InternshipWithTeacher();
                internshipWithTeacher1.setCreateBy(id);
                internshipWithTeacher1.setTeacherId(teacherId);
                internshipWithTeacher1.setRelationPlanId(planId);
                boolean save = internshipWithTeacherService.save(internshipWithTeacher1);
                if(!save){
                    throw new RuntimeException("分配实习计划指导老师失败");
                }
                //修改学生关联
                QueryWrapper<InternshipWithStudent> queryWrapper4 = new QueryWrapper();
                queryWrapper4.eq("deleted",BaseVo.UNDELETE).eq("relation_teacher_id",oldTeacherId).eq("relation_plan_id",planId)
                        .in("relation_student_id",oldStudentList);
                List<InternshipWithStudent> withStudents =internshipWithStudentService.list(queryWrapper4);
                if(withStudents.size() > 0){
                    for(InternshipWithStudent internshipWithStudent : withStudents){
                        internshipWithStudent.setUpdateBy(id);
                        internshipWithStudent.setRelationTeacherId(teacherId);
                        updateStudents.add(internshipWithStudent);
                    }
                }else{
                    throw new RuntimeException("找不到分配学生");
                }
                if(updateStudents.size() > 0){
                    boolean save4 = internshipWithStudentService.updateBatchById(updateStudents);
                    if(!save4){
                        throw new RuntimeException("修改实习计划分配学生与老师关联失败");
                    }else {
                        //修改完成后替换id用于后续操作
                        oldTeacherId = teacherId;
                    }
                }
            }
        }
        //接下来判断旧的学生集合和新的学生集合一不一样
        if(!studentList.equals(oldStudentList)){
//            Integer studentLen = studentList.size();
//            Integer oldStudentLen = oldStudentList.size();

            Set<Integer> studentIds = new HashSet<>(studentList);
            studentIds.removeAll(oldStudentList);

            Set<Integer> oldStudentIds = new HashSet<>(oldStudentList);
            oldStudentIds.removeAll(studentList);

            studentList = new ArrayList<>(studentIds);
            oldStudentList = new ArrayList<>(oldStudentIds);
            //去重
//            for (int i = studentLen-1; i < studentLen; i--) {
//                for (int j = 0; j < oldStudentLen; j++) {
//                    if(studentList.get(i).equals(oldStudentList.get(j))){
//                        studentList.remove(i);
//                        oldStudentList.remove(j);
//                        --studentLen;
//                        --oldStudentLen;
//                        break;
//                    }
//                }
//            }
            if(oldStudentList.size() > 0){
                //查找并删除学生
                QueryWrapper<InternshipWithStudent> queryWrapper2 = new QueryWrapper();
                queryWrapper2.eq("deleted",BaseVo.UNDELETE).eq("relation_teacher_id",oldTeacherId).eq("relation_plan_id",planId)
                        .in("relation_student_id",oldStudentList);
                List<InternshipWithStudent> internshipWithStudents =internshipWithStudentService.list(queryWrapper2);
                if(internshipWithStudents.size() > 0){
                    for(InternshipWithStudent internshipWithStudent : internshipWithStudents){
                        internshipWithStudent.setUpdateBy(id);
                        internshipWithStudent.setDeleted(BaseVo.DELETE);
                        oldStudents.add(internshipWithStudent);
                    }
                } else{
                    throw new RuntimeException("找不到分配学生");
                }
                if(oldStudents.size() > 0){
                    boolean save2 = internshipWithStudentService.updateBatchById(oldStudents);
                    if(!save2){
                        throw new RuntimeException("删除实习计划分配学生失败");
                    }
                }
            }
            if(studentList.size() > 0){
                //查找并添加分配
                //查找学生是否已分配该实习计划
                QueryWrapper<InternshipWithStudent> queryWrapper3 = new QueryWrapper();
                queryWrapper3.eq("deleted",BaseVo.UNDELETE).eq("relation_plan_id",planId)
                        .in("relation_student_id",studentList);
                List<InternshipWithStudent> internshipWithStudents =internshipWithStudentService.list(queryWrapper3);
//                if(internshipWithStudents.size() > 0){
////                    throw new RuntimeException("该学生集合里有学生在该实习计划下已分配");
//                    //如果学生已分配过，则转到新老师的归属下
//                    internshipWithStudents.forEach(info -> info.setUpdateBy(id).setRelationTeacherId(teacherId));
//                    students.addAll(internshipWithStudents);
//                }else {
                    for(Integer studentId : studentList){
                        InternshipWithStudent internshipWithStudent = null;
                        for (InternshipWithStudent info : internshipWithStudents){
                            internshipWithStudent = info;
                            break;
                        }

                        if (internshipWithStudent != null){
                            internshipWithStudent.setUpdateBy(id).setRelationTeacherId(teacherId);

                            internshipWithStudents.remove(internshipWithStudent);
                        }else {
                            internshipWithStudent = new InternshipWithStudent();
                            internshipWithStudent.setRelationPlanId(planId);
                            internshipWithStudent.setRelationStudentId(studentId);
                            internshipWithStudent.setRelationTeacherId(teacherId);
                            internshipWithStudent.setCreateBy(id);
                        }
                        students.add(internshipWithStudent);
                    }
//                }

                if(students.size() > 0){
                    boolean save3 = internshipWithStudentService.saveOrUpdateBatch(students);
                    if(!save3){
                        throw new RuntimeException("分配实习计划学生失败");
                    }
                }
            }

        }
        return CommonResult.success("编辑实习计划分配成功");
    }

    /**
    *@author: GG
    *@data: 2022/7/15 10:45
    *@function:获取实习计划分配情况列表
    */
    @Override
    public CommonResult<Object> getAllotInternshipPlanInfoList(InternshipPlanInfoVo vo, UserDto userDto) {
        if(userDto.getUserRoleDto().getRoleCode().equals("schoolAdmin")){
            vo.setSchoolId(userDto.getTeacherInfo().getSchoolId());
        }else if(userDto.getUserRoleDto().getRoleCode().equals("departmentAdmin")){
            vo.setSchoolId(userDto.getTeacherInfo().getSchoolId());
            vo.setDepartmentId(userDto.getTeacherInfo().getDepartmentId());
        }
        List<InternshipPlanInfoDto> list = this.baseMapper.getAllotInternshipPlanInfoList(vo);
        Integer count = this.baseMapper.getAllotInternshipPlanInfoListCount(vo);
        Map<String,Object> map = new HashMap<>();
        map.put(BaseVo.LIST,list);
        map.put(BaseVo.TOTAL,count);
        map.put(BaseVo.PAGE,BaseVo.calculationPages(vo.getSize(),count));
        return CommonResult.success(map);
    }

    /**
     *@author: GG
     *@data: 2022/7/18 10:32
     *@function:获取实习分配列表中已分配教师学生列表
     */
    @Override
    public CommonResult<Object> getAllotInternshipPlanTeacherAndStudentInfoList(InternshipPlanInfoVo vo, UserDto userDto) {
        List<AllotInternshipPlanInfoDto> allotInternshipPlanInfoDto = this.getBaseMapper().getAllotInternshipPlanTeacherAndStudentInfoList(vo);
        Map<String,Object> map = new HashMap<>();
        map.put(BaseVo.LIST,allotInternshipPlanInfoDto);
        return CommonResult.success(map);
    }
}
