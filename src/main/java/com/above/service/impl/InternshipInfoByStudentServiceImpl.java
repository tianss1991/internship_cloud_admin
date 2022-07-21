package com.above.service.impl;

import com.above.dto.InternshipApplyInfoDto;
import com.above.dto.InternshipCheckDto;
import com.above.dto.InternshipInfoFillDto;
import com.above.dto.UserDto;
import com.above.po.*;
import com.above.dao.InternshipInfoByStudentMapper;
import com.above.service.AreaService;
import com.above.service.InternshipInfoByStudentLogService;
import com.above.service.InternshipInfoByStudentService;
import com.above.service.StudentInfoService;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.InternshipApplicationVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.log4j.PatternLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生实习信息表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Service
public class InternshipInfoByStudentServiceImpl extends ServiceImpl<InternshipInfoByStudentMapper, InternshipInfoByStudent> implements InternshipInfoByStudentService {
    @Autowired
    private InternshipInfoByStudentLogService internshipInfoByStudentLogService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private StudentInfoService studentInfoService;

    /*----------------------学生------------------------*/

    /**
     * 提交及修改实习申请
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> internshipApplySubmitOrUpdate(InternshipApplicationVo vo, UserDto userDto) {
        //实习计划id
        Integer relationPlanId = vo.getRelationPlanId();
        //学生id
        Integer studentId = userDto.getStudentInfo().getId();
        //公司名称
        String companyName = vo.getCompanyName();
        //社会信用代码
        String socialCode = vo.getSocialCode();
        //企业规模
        Integer companyScale = vo.getCompanyScale();
        //联系电话
        String companyTelephone = vo.getCompanyTelephone();
        //公司性质
        Integer companyNature = vo.getCompanyNature();
        //所属行业
        Integer companyIndustry = vo.getCompanyIndustry();
        //企业所在区域字符串
        String companyArea = vo.getCompanyArea();
        //企业所在区域编码
        Integer companyCode = vo.getCompanyCode();
        //公司地址
        String companyAddress = vo.getCompanyAddress();
        //岗位名称
        String jobName = vo.getJobName();
        //部门名称
        String jobDepartment = vo.getJobDepartment();
        //企业老师
        String companyTeacher = vo.getCompanyTeacher();
        //企业老师电话
        String companyTeacherTelephone = vo.getCompanyTeacherTelephone();
        //岗位类别
        Integer jobType =vo.getJobType();
        //岗位所在区域字符串
        String jobArea = vo.getJobArea();
        //岗位所在区域编码
        Integer jobCode = vo.getJobCode();
        //岗位地址
        String jobAddress =vo.getJobAddress();
        //开始时间
        Date startTime = vo.getStartTime();
        //结束时间
        Date endTime = vo.getEndTime();
        //实习方式
        Integer approach = vo.getApproach();
        //专业是否对口
        Integer isAboral = vo.getIsAboral();
        //是否有特殊专业情况
        Integer isSpecial = vo.getIsSpecial();
        //经度
        String  longitude = vo.getLongitude();
        //纬度
        String  latitude = vo.getLatitude();
        //创建人
        Integer createBy = userDto.getId();
        //更新人
        Integer updateBy = userDto.getId();

        //新建一个对象
        InternshipInfoByStudent internshipInfoByStudent = new InternshipInfoByStudent();
        internshipInfoByStudent.setCompanyName(companyName);
        internshipInfoByStudent.setSocialCode(socialCode);
        //人事负责人
        if(vo.getPrincipal()!=null){
            String principal = vo.getPrincipal();
            internshipInfoByStudent.setPrincipal(principal);
        }
        internshipInfoByStudent.setCompanyScale(companyScale);
        internshipInfoByStudent.setCompanyTelephone(companyTelephone);
        //公司邮箱
        if(vo.getCompanyMail()!=null){
            String companyMail = vo.getCompanyMail();
            internshipInfoByStudent.setCompanyMail(companyMail);
        }
        //公司邮编
        if(vo.getCompanyPost()!=null){
            String companyPost = vo.getCompanyPost();
            internshipInfoByStudent.setCompanyPost(companyPost);
        }
        internshipInfoByStudent.setCompanyNature(companyNature);
        internshipInfoByStudent.setCompanyIndustry(companyIndustry);
        internshipInfoByStudent.setCompanyArea(companyArea);
        internshipInfoByStudent.setCompanyCode(companyCode);
        internshipInfoByStudent.setCompanyAddress(companyAddress);
        internshipInfoByStudent.setJobName(jobName);
        internshipInfoByStudent.setJobDepartment(jobDepartment);
        //岗位内容
        if(vo.getJobContent()!=null){
            String jobContent = vo.getJobContent();
            internshipInfoByStudent.setJobContent(jobContent);
        }
        internshipInfoByStudent.setCompanyTeacher(companyTeacher);
        internshipInfoByStudent.setCompanyTeacherTelephone(companyTeacherTelephone);
        internshipInfoByStudent.setJobType(jobType);
        //岗位简介
        if(vo.getJobBriefInfo()!=null) {
            String jobBriefInfo = vo.getJobBriefInfo();
            internshipInfoByStudent.setJobBriefInfo(jobBriefInfo);
        }
        internshipInfoByStudent.setJobArea(jobArea);
        internshipInfoByStudent.setJobCode(jobCode);
        internshipInfoByStudent.setJobAddress(jobAddress);
        internshipInfoByStudent.setStartTime(startTime);
        internshipInfoByStudent.setEndTime(endTime);
        internshipInfoByStudent.setApproach(approach);
        internshipInfoByStudent.setIsAboral(isAboral);
        internshipInfoByStudent.setLongitude(longitude);
        internshipInfoByStudent.setLatitude(latitude);
        //实习薪资
        if(vo.getJobBriefInfo()!=null) {
            String salary = vo.getSalary();
            internshipInfoByStudent.setSalary(salary);
        }
        internshipInfoByStudent.setIsSpecial(isSpecial);
        //上传图片
        if(vo.getFileUrl()!=null) {
            String fileUrl = vo.getFileUrl();
            internshipInfoByStudent.setFileUrl(fileUrl);
        }
        //修改
        if(vo.getInternshipId()!=null){
            //判断实习申请状态是不是驳回的状态
            Integer internshipId = vo.getInternshipId();
            InternshipInfoByStudent infoByStudent = this.getById(internshipId);
            if(infoByStudent!=null){
                if(infoByStudent.getDeleted().equals(BaseVo.DELETE)){
                    return CommonResult.error(500,"该条实习申请不存在");
                }
                //1为实习申请
                if(infoByStudent.getInternshipType().equals(1)) {
                    //2为驳回状态，0为草稿状态，这两种都可以修改
                    if (infoByStudent.getStatus().equals(0)||infoByStudent.getStatus().equals(2)) {
                        internshipInfoByStudent.setId(vo.getInternshipId());
                        internshipInfoByStudent.setUpdateBy(updateBy);
                    }else{
                        return CommonResult.error(500,"该条实习申请不是驳回或草稿状态，无法修改");
                    }
                }else{
                    return CommonResult.error(500,"数据异常，这条申请是免实习申请");
                }
            }
        }else{
            internshipInfoByStudent.setId(null);
            internshipInfoByStudent.setCreateBy(createBy);
            //关联学生id
            internshipInfoByStudent.setRelationStudentId(studentId);
            //实习计划id
            internshipInfoByStudent.setRelationPlanId(relationPlanId);
            QueryWrapper<InternshipInfoByStudent> queryWrapper =new QueryWrapper<>();
            queryWrapper.eq("deleted",BaseVo.UNDELETE).eq("relation_plan_id",relationPlanId).eq("relation_student_id",studentId);
            List<InternshipInfoByStudent> list = this.list(queryWrapper);
            for(InternshipInfoByStudent infoByStudent:list){
                //如果是免实习申请，返回
                if(infoByStudent.getInternshipType().equals(2)){
                    if(infoByStudent.getStatus().equals(1)){
                        return CommonResult.error(500,"已经存在审核中免实习申请，无法进行实习申请操作");
                    }else if(infoByStudent.getStatus().equals(3)){
                        //审核通过
                        return CommonResult.error(500,"已经存在已通过免实习申请，无法进行实习申请操作");
                    }
                }else if(infoByStudent.getInternshipType().equals(1)){
                    //审核中
                    if(infoByStudent.getStatus().equals(1)){
                        return CommonResult.error(500,"已经存在审核中实习申请，无法进行实习申请操作");
                    }else if(infoByStudent.getStatus().equals(3)){
                        //审核通过
                        return CommonResult.error(500,"已经存在已通过实习申请，无法进行实习申请操作");
                    }
                }
            }
        }
        //普通申请
        internshipInfoByStudent.setInternshipType(1);
        //正常审核还是变更审核
        internshipInfoByStudent.setCheckStatus(1);
        //状态，提交后就是审核中
        internshipInfoByStudent.setStatus(1);
        boolean flag = this.saveOrUpdate(internshipInfoByStudent);
        if(!flag){
            return CommonResult.error(500,"操作失败");
        }
        return CommonResult.success("操作成功");
    }

    /**
     * 删除实习申请
     * */
//    @Override
//    public CommonResult<Object> internshipApplyDelete(InternshipApplicationVo vo, UserDto userDto) {
//        Integer internshipId = vo.getInternshipId();
//        Integer updateBy = userDto.getId();
//        InternshipInfoByStudent internshipInfoByStudent = this.getById(internshipId);
//        if(internshipInfoByStudent!=null){
//            if(internshipInfoByStudent.getDeleted().equals(BaseVo.DELETE)){
//                return CommonResult.error(500,"该条实习申请不存在");
//            }
//            internshipInfoByStudent.setDeleted(BaseVo.DELETE);
//            internshipInfoByStudent.setUpdateBy(updateBy);
//        }
//        boolean flag = this.updateById(internshipInfoByStudent);
//        if(!flag){
//            return CommonResult.error(500,"删除实习申请失败");
//        }
//        return CommonResult.success("删除实习申请成功");
//    }

    /**
     * 显示实习申请
     * */
    @Override
    public CommonResult<Object> internshipApplyDisplaySingle(InternshipApplicationVo vo, UserDto userDto) {
        Map<String,Object> map = new HashMap<>();
        if(userDto.getUserRoleDto().getRoleCode().equals("student")){
            vo.setStudentId(userDto.getId());
        }else if(userDto.getUserRoleDto().getRoleCode().equals("adviser")){
            vo.setTeacherId(userDto.getId());
        }
        InternshipApplyInfoDto applyInfoDto = this.baseMapper.displaySingleInternshipApplyInfo(vo);
        if(applyInfoDto!=null){
            if(applyInfoDto.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该条实习申请不存在");
            }
            map.put("internship",applyInfoDto);
        }else{
            return CommonResult.error(500,"查无此条");
        }
        return CommonResult.success(map);
    }



    /**
     * 显示实习岗位列表
     * */
    @Override
    public CommonResult<Object> internshipApplyDisplayList(InternshipApplicationVo vo, UserDto userDto) {
        Integer relationPlanId = vo.getRelationPlanId();
        Integer relationStudentId = userDto.getStudentInfo().getId();
        vo.setPlanId(relationPlanId);
        vo.setRelationStudentId(relationStudentId);
        //设置分页参数
        Page<InternshipInfoByStudent> page = new Page<>(vo.getPage(), vo.getSize());
        List<InternshipInfoByStudent> list = null;
        Map<String,Object> map = new HashMap<>();
        QueryWrapper<InternshipInfoByStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",BaseVo.UNDELETE).eq("relation_plan_id",relationPlanId).eq("relation_student_id",relationStudentId)
        .in("internship_type",1,2);
        IPage<InternshipInfoByStudent> iPage = super.page(page, queryWrapper);
        list = iPage.getRecords();
        map.put(BaseVo.LIST,list);
        //总页数
        map.put(BaseVo.PAGE,iPage.getPages());
        //总数
        map.put(BaseVo.TOTAL,iPage.getTotal());
        return CommonResult.success(map);
    }

    /**
     * 提交及修改免实习申请
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> dismissInternshipApplySubmit(InternshipApplicationVo vo, UserDto userDto) {
        String reason = vo.getReason();
        Integer dispositon = vo.getDispositon();
        String  fileUrl = vo.getFileUrl();
        Integer createBy = userDto.getId();
        Integer updateBy = userDto.getId();
        //实习计划id
        Integer relationPlanId = vo.getRelationPlanId();
        //学生id
        Integer studentId = userDto.getStudentInfo().getId();
        //新建对象用来存放数据
        InternshipInfoByStudent internshipInfoByStudent = new InternshipInfoByStudent();
        internshipInfoByStudent.setReason(reason);
        internshipInfoByStudent.setDispositon(dispositon);
        internshipInfoByStudent.setFileUrl(fileUrl);
        //设置免实习状态,免实习为2
        internshipInfoByStudent.setInternshipType(2);
        //提交后是审核中状态
        internshipInfoByStudent.setStatus(1);
        //修改
        if(vo.getInternshipId()!=null){
            //判断免实习申请状态是不是驳回的状态
            Integer internshipId = vo.getInternshipId();
            InternshipInfoByStudent infoByStudent = this.getById(internshipId);
            if(infoByStudent!=null){
                if(infoByStudent.getDeleted().equals(BaseVo.DELETE)){
                    return CommonResult.error(500,"该条免实习申请不存在");
                }
                //2为免实习
                if(infoByStudent.getInternshipType().equals(2)){
                    //1为非通过情况下提交
                        //2为驳回状态，0为草稿状态，这两种都可以修改
                        if (infoByStudent.getStatus().equals(0)||infoByStudent.getStatus().equals(2)) {
                            internshipInfoByStudent.setId(vo.getInternshipId());
                            internshipInfoByStudent.setUpdateBy(updateBy);
                        }else{
                            return CommonResult.error(500,"该条免实习申请不是驳回或草稿状态，无法修改");
                        }

                }else{
                    return CommonResult.error(500,"数据异常，这条数据是实习申请");
                }
            }
        }else{
            internshipInfoByStudent.setId(null);
            internshipInfoByStudent.setCreateBy(createBy);
            //关联学生id
            internshipInfoByStudent.setRelationStudentId(studentId);
            //实习计划id
            internshipInfoByStudent.setRelationPlanId(relationPlanId);
            QueryWrapper<InternshipInfoByStudent> queryWrapper =new QueryWrapper<>();
            queryWrapper.eq("deleted",BaseVo.UNDELETE).eq("relation_plan_id",relationPlanId).eq("relation_student_id",studentId);
            List<InternshipInfoByStudent> list = this.list(queryWrapper);
            for(InternshipInfoByStudent infoByStudent:list){
                //如果是实习申请，返回
                if(infoByStudent.getInternshipType().equals(1)){
                    return CommonResult.error(500,"已经存在实习申请，无法进行免实习申请操作");
                }else if(infoByStudent.getInternshipType().equals(2)){
                    //审核中
                    if(infoByStudent.getStatus().equals(1)){
                        return CommonResult.error(500,"已经存在审核中免实习申请，无法进行免实习申请操作");
                    }else if(infoByStudent.getStatus().equals(3)){
                        //审核通过
                        return CommonResult.error(500,"已经存在已通过免实习申请，无法进行免实习申请操作");
                    }
                }
            }
        }
        //免实习申请
        internshipInfoByStudent.setInternshipType(2);
        //状态，提交后就是审核中
        internshipInfoByStudent.setStatus(1);
        boolean flag = this.saveOrUpdate(internshipInfoByStudent);
        if(!flag){
            return CommonResult.error(500,"操作失败");
        }
        return CommonResult.success("操作成功");
    }

    /**
     * 删除免实习申请
     * */
//    @Override
//    public CommonResult<Object> dismissInternshipApplyDelete(InternshipApplicationVo vo, UserDto userDto) {
//        Integer internshipId = vo.getInternshipId();
//        Integer updateBy = userDto.getId();
//        InternshipInfoByStudent internshipInfoByStudent = this.getById(internshipId);
//        if(internshipInfoByStudent!=null){
//            if(internshipInfoByStudent.getDeleted().equals(BaseVo.DELETE)){
//                return CommonResult.error(500,"该条免实习申请不存在");
//            }
//            internshipInfoByStudent.setDeleted(BaseVo.DELETE);
//            internshipInfoByStudent.setUpdateBy(updateBy);
//        }
//        boolean flag = this.updateById(internshipInfoByStudent);
//        if(!flag){
//            return CommonResult.error(500,"删除免实习申请失败");
//        }
//        return CommonResult.success("删除免实习申请成功");
//    }

    /**
     * 显示免实习申请
     * */
    @Override
    public CommonResult<Object> dismissInternshipApplyDisplaySingle(InternshipApplicationVo vo, UserDto userDto) {
        Integer internshipId = vo.getInternshipId();
        Map<String,Object> map = new HashMap<>();
        InternshipInfoByStudent internshipInfoByStudent = this.getById(internshipId);
        if(internshipInfoByStudent!=null){
            if(internshipInfoByStudent.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该条实习申请不存在");
            }
            map.put("internship",internshipInfoByStudent);
        }else{
            return CommonResult.error(500,"查无此条");
        }
        return CommonResult.success(map);
    }



//    /**
//     * 显示免实习岗位列表
//     * */
//    @Override
//    public CommonResult<Object> dismissInternshipApplyDisplayList(InternshipApplicationVo vo, UserDto userDto) {
//        Integer relationPlanId = vo.getRelationPlanId();
//        Integer relationStudentId = vo.getRelationStudentId();
//        Map<String,Object> map = new HashMap<>();
//        QueryWrapper<InternshipInfoByStudent> queryWrapper = new QueryWrapper<>();
//        //internship_type字段中2为免实习申请
//        queryWrapper.eq("deleted",0).eq("relation_plan_id",relationPlanId).eq("relation_student_id",relationStudentId)
//                .eq("internship_type",2);
//        List<InternshipInfoByStudent> list = this.list(queryWrapper);
//        if(list.size()>0){
//            map.put("list",list);
//        }
//        return CommonResult.success(map);
//    }

    /**
     * 学生撤回申请
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> withdrawApply(InternshipApplicationVo vo, UserDto userDto) {
        Integer internshipId = vo.getInternshipId();
        Integer updateBy = userDto.getId();
        InternshipInfoByStudent internshipInfoByStudent = this.getById(internshipId);
        if(internshipInfoByStudent!=null){
            if(internshipInfoByStudent.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该条实习申请不存在");
            }
            //审核中的才能撤回
            if(internshipInfoByStudent.getStatus().equals(1)){
                //更新人
                internshipInfoByStudent.setUpdateBy(updateBy);
                //撤回后数据会到草稿箱
                internshipInfoByStudent.setStatus(0);
            }else{
                return CommonResult.error(500,"不是审核中状态的申请无法撤回");
            }

        }else{
            return CommonResult.error(500,"查无此条");
        }
        boolean flag = this.updateById(internshipInfoByStudent);
        if(!flag){
            return CommonResult.error(500,"撤回操作失败");
        }
        return CommonResult.success("撤回操作成功");
    }


    /**
     * 实习岗位修改与企业变更申请
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> jobModifyAndCompanyModify(InternshipApplicationVo vo, UserDto userDto) {
        boolean flag = false;
        //先查出具体申请
        Integer internshipId = vo.getInternshipId();
        InternshipInfoByStudent info = this.getById(internshipId);
        if(info==null){
            return CommonResult.error(500,"该条记录不存在");
        }else{
            //已经通过的实习申请才能走这个接口修改
            if(info.getStatus().equals(3)){
                //2-实习岗位修改
                if(vo.getCheckStatus().equals(2)){
                    if(vo.getJobName()!=null){
                        String jobName = vo.getJobName();
                        //岗位名称
                        if(!jobName.equals(info.getJobName())){
                            info.setJobName(jobName);
                            flag=true;
                        }
                    }
                    if(vo.getJobDepartment()!=null){
                        //部门名称
                        String jobDepartment = vo.getJobDepartment();
                        if(!jobDepartment.equals(info.getJobDepartment())){
                            info.setJobDepartment(jobDepartment);
                            flag=true;
                        }
                    }
                    if(vo.getCompanyTeacher()!=null){
                        //企业老师
                        String companyTeacher = vo.getCompanyTeacher();
                        if(!companyTeacher.equals(info.getCompanyTeacher())){
                            info.setCompanyTeacher(companyTeacher);
                            flag=true;
                        }
                    }
                    if(vo.getCompanyTeacherTelephone()!=null){
                        //企业老师电话
                        String companyTeacherTelephone = vo.getCompanyTeacherTelephone();
                        if(!companyTeacherTelephone.equals(info.getCompanyTeacherTelephone())){
                            info.setCompanyTeacherTelephone(companyTeacherTelephone);
                            flag=true;
                        }
                    }
                    if(vo.getJobType()!=null){
                        //岗位类别
                        Integer jobType =vo.getJobType();
                        if(!jobType.equals(info.getJobType())){
                            info.setJobType(jobType);
                            flag=true;
                        }
                    }
                    if(vo.getJobArea() != null && vo.getJobCode() != null){
                        //岗位所在区域
                        String jobArea = vo.getJobArea();
                        Integer jobCode = vo.getJobCode();
                        if(!jobArea.equals(info.getJobArea()) && !jobCode.equals(info.getJobCode())){
                            info.setJobArea(jobArea);
                            info.setJobCode(jobCode);
                            flag=true;
                        }
                    }
                    if(vo.getJobAddress()!=null){
                        //岗位地址
                        String jobAddress =vo.getJobAddress();
                        if(!jobAddress.equals(info.getJobAddress())){
                            info.setJobAddress(jobAddress);
                            flag=true;
                        }
                    }
                    if(vo.getJobBriefInfo()!=null){
                        //工作简介
                        String jobBriefInfo = vo.getJobBriefInfo();
                        if(!jobBriefInfo.equals(info.getJobDepartment())){
                            info.setJobBriefInfo(jobBriefInfo);
                            flag=true;
                        }
                    }
                    if(vo.getJobContent()!=null){
                        //工作内容
                        String jobContent = vo.getJobContent();
                        if(!jobContent.equals(info.getJobDepartment())){
                            info.setJobContent(jobContent);
                            flag=true;
                        }
                    }

                }else if(vo.getCheckStatus().equals(3)){
                    //3-企业变更修改
                    if(vo.getJobName()!=null){
                        String jobName = vo.getJobName();
                        //岗位名称
                        if(!jobName.equals(info.getJobName())){
                            info.setJobName(jobName);
                            flag=true;
                        }
                    }
                    if(vo.getJobDepartment()!=null){
                        //部门名称
                        String jobDepartment = vo.getJobDepartment();
                        if(!jobDepartment.equals(info.getJobDepartment())){
                            info.setJobDepartment(jobDepartment);
                            flag=true;
                        }
                    }
                    if(vo.getCompanyTeacher()!=null){
                        //企业老师
                        String companyTeacher = vo.getCompanyTeacher();
                        if(!companyTeacher.equals(info.getCompanyTeacher())){
                            info.setCompanyTeacher(companyTeacher);
                            flag=true;
                        }
                    }
                    if(vo.getCompanyTeacherTelephone()!=null){
                        //企业老师电话
                        String companyTeacherTelephone = vo.getCompanyTeacherTelephone();
                        if(!companyTeacherTelephone.equals(info.getCompanyTeacherTelephone())){
                            info.setJobName(companyTeacherTelephone);
                            flag=true;
                        }
                    }
                    if(vo.getJobType()!=null){
                        //岗位类别
                        Integer jobType =vo.getJobType();
                        if(!jobType.equals(info.getJobType())){
                            info.setJobType(jobType);
                            flag=true;
                        }
                    }
                    if(vo.getJobArea() != null && vo.getJobCode() != null){
                        //岗位所在区域
                        String jobArea = vo.getJobArea();
                        Integer jobCode = vo.getJobCode();
                        if(!jobArea.equals(info.getJobArea()) && !jobCode.equals(info.getJobCode())){
                            info.setJobArea(jobArea);
                            info.setJobCode(jobCode);
                            flag=true;
                        }
                    }
                    if(vo.getJobAddress()!=null){
                        //岗位地址
                        String jobAddress =vo.getJobAddress();
                        if(!jobAddress.equals(info.getJobAddress())){
                            info.setJobAddress(jobAddress);
                            flag=true;
                        }
                    }
                    if(vo.getJobBriefInfo()!=null){
                        //工作简介
                        String jobBriefInfo = vo.getJobBriefInfo();
                        if(!jobBriefInfo.equals(info.getJobBriefInfo())){
                            info.setJobBriefInfo(jobBriefInfo);
                            flag=true;
                        }
                    }
                    if(vo.getJobContent()!=null){
                        //工作内容
                        String jobContent = vo.getJobContent();
                        if(!jobContent.equals(info.getJobContent())){
                            info.setJobContent(jobContent);
                            flag=true;
                        }
                    }
                    if(vo.getCompanyName()!=null){
                        //公司名称
                        String companyName = vo.getCompanyName();
                        if(!companyName.equals(info.getCompanyName())){
                            info.setCompanyName(companyName);
                            flag=true;
                        }
                    }
                    if(vo.getSocialCode()!=null){
                        //社会信用代码
                        String socialCode = vo.getSocialCode();
                        if(!socialCode.equals(info.getSocialCode())){
                            info.setSocialCode(socialCode);
                            flag=true;
                        }
                    }
                    if(vo.getPrincipal()!=null){
                        //人事负责人
                        String principal = vo.getPrincipal();
                        if(!principal.equals(info.getPrincipal())){
                            info.setPrincipal(principal);
                            flag=true;
                        }
                    }
                    if(vo.getCompanyScale()!=null){
                        //企业规模
                        Integer companyScale = vo.getCompanyScale();
                        if(!companyScale.equals(info.getCompanyScale())){
                            info.setCompanyScale(companyScale);
                            flag=true;
                        }
                    }
                    if(vo.getCompanyTelephone()!=null){
                        //联系电话
                        String companyTelephone = vo.getCompanyTelephone();
                        if(!companyTelephone.equals(info.getJobAddress())){
                            info.setCompanyTelephone(companyTelephone);
                            flag=true;
                        }
                    }
                    if(vo.getCompanyMail()!=null){
                        //公司邮箱
                        String companyMail = vo.getCompanyMail();
                        if(!companyMail.equals(info.getCompanyMail())){
                            info.setCompanyMail(companyMail);
                            flag=true;
                        }
                    }
                    if(vo.getCompanyPost()!=null){
                        //公司邮编
                        String companyPost = vo.getCompanyPost();
                        if(!companyPost.equals(info.getCompanyPost())){
                            info.setCompanyPost(companyPost);
                            flag=true;
                        }
                    }
                    if(vo.getCompanyNature()!=null){
                        //公司性质
                        Integer companyNature = vo.getCompanyNature();
                        if(!companyNature.equals(info.getJobAddress())){
                            info.setCompanyNature(companyNature);
                            flag=true;
                        }
                    }
                    if(vo.getCompanyIndustry()!=null){
                        //所属行业
                        Integer companyIndustry = vo.getCompanyIndustry();
                        if(!companyIndustry.equals(info.getCompanyIndustry())){
                            info.setCompanyIndustry(companyIndustry);
                            flag=true;
                        }
                    }
                    if(vo.getCompanyArea()!=null){
                        //企业所在区域
                        String companyArea = vo.getCompanyArea();
                        Integer companyCode = vo.getCompanyCode();
                        if(!companyArea.equals(info.getCompanyArea()) && !companyArea.equals(info.getCompanyCode())){
                            info.setCompanyCode(companyCode);
                            info.setCompanyArea(companyArea);
                            flag=true;
                        }
                    }
                    if(vo.getCompanyAddress()!=null){
                        //公司地址
                        String companyAddress = vo.getCompanyAddress();
                        if(!companyAddress.equals(info.getCompanyAddress())){
                            info.setCompanyAddress(companyAddress);
                            flag=true;
                        }
                    }
                }
                if(flag){
                    if(vo.getCheckStatus().equals(2)){
                        info.setCheckStatus(2);
                    }else if(vo.getCheckStatus().equals(3)){
                        info.setCheckStatus(3);
                    }
                    info.setStatus(1);
                    boolean update = this.updateById(info);
                    if(!update){
                        return CommonResult.error(500,"修改失败");
                    }
                }else{
                    return CommonResult.success("本次操作没有改动数据");
                }

            }else{
                return CommonResult.error(500,"该条记录不是通过状态,不可进行实习岗位修改操作");
            }
        }
        return CommonResult.success("修改成功");
    }

    /**
     * 提交就业上报
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> employmentReportedSubmitOrUpdate(InternshipApplicationVo vo, UserDto userDto){
        //新建一个实体类用来放数据
        InternshipInfoByStudent internshipInfoByStudent = new InternshipInfoByStudent();
        //学生id
        Integer studentId = userDto.getStudentInfo().getId();
        //创建人
        Integer createBy = userDto.getId();
        internshipInfoByStudent.setRelationStudentId(studentId);
        //实习计划id
        Integer relationPlanId = vo.getRelationPlanId();
        internshipInfoByStudent.setRelationPlanId(relationPlanId);
        if(vo.getJobCategory()!=null){
            //就业类型
            Integer jobCategory = vo.getJobCategory();
            internshipInfoByStudent.setJobCategory(jobCategory);
        }
        if(vo.getCompanyName()!=null){
            //公司名称
            String companyName = vo.getCompanyName();
            internshipInfoByStudent.setCompanyName(companyName);
        }
        if(vo.getSocialCode()!=null){
            //社会信用代码
            String socialCode = vo.getSocialCode();
            internshipInfoByStudent.setSocialCode(socialCode);
        }
        if(vo.getPrincipal()!=null){
            //人事负责人
            String principal = vo.getPrincipal();
            internshipInfoByStudent.setPrincipal(principal);
        }
        if(vo.getCompanyScale()!=null){
            //企业规模
            Integer companyScale = vo.getCompanyScale();
            internshipInfoByStudent.setCompanyScale(companyScale);
        }
        if(vo.getCompanyTelephone()!=null){
            //联系电话
            String companyTelephone = vo.getCompanyTelephone();
            internshipInfoByStudent.setCompanyTelephone(companyTelephone);
        }
        if(vo.getCompanyPost()!=null){
            //单位邮编
            String companyPost = vo.getCompanyPost();
            internshipInfoByStudent.setCompanyPost(companyPost);
        }
        if(vo.getCompanyNature()!=null){
            //公司性质
            Integer companyNature = vo.getCompanyNature();
            internshipInfoByStudent.setCompanyNature(companyNature);
        }
        if(vo.getCompanyIndustry()!=null){
            //所属行业
            Integer companyIndustry = vo.getCompanyIndustry();
            internshipInfoByStudent.setCompanyIndustry(companyIndustry);
        }
        if(vo.getCompanyArea()!=null){
            //企业所在区域
            String companyArea = vo.getCompanyArea();
            internshipInfoByStudent.setCompanyArea(companyArea);
        }
        if(vo.getCompanyCode()!=null){
            //企业所在区域
            Integer companyCode = vo.getCompanyCode();
            internshipInfoByStudent.setCompanyCode(companyCode);
        }
        if(vo.getCompanyAddress()!=null){
            //公司地址
            String companyAddress = vo.getCompanyAddress();
            internshipInfoByStudent.setCompanyAddress(companyAddress);
        }
        if(vo.getJobType()!=null){
            //岗位类别
            Integer jobType =vo.getJobType();
            internshipInfoByStudent.setJobType(jobType);
        }
        if(vo.getJobName()!=null){
            //岗位名称
            String jobName = vo.getJobName();
            internshipInfoByStudent.setJobName(jobName);
        }
        if(vo.getIsAboral()!=null){
            //专业是否对口
            Integer isAboral = vo.getIsAboral();
            internshipInfoByStudent.setIsAboral(isAboral);
        }
        if(vo.getSalary()!=null){
            //薪资
            String salary = vo.getSalary();
            internshipInfoByStudent.setSalary(salary);
        }
        if(vo.getAgreementCode()!=null){
            //协议书编号
            String agreementCode = vo.getAgreementCode();
            internshipInfoByStudent.setAgreementCode(agreementCode);
        }
        if(vo.getFileUrl()!=null){
            //附件
            String fileUrl = vo.getFileUrl();
            internshipInfoByStudent.setFileUrl(fileUrl);

        }
        //提交后就是审核中
        internshipInfoByStudent.setStatus(1);
        //类型为就业上报
        internshipInfoByStudent.setInternshipType(3);
        //创建人
        internshipInfoByStudent.setCreateBy(createBy);

        QueryWrapper<InternshipInfoByStudent> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("deleted",BaseVo.UNDELETE).eq("relation_plan_id",relationPlanId).eq("relation_student_id",studentId).eq("internship_type",3);
        List<InternshipInfoByStudent> list = this.list(queryWrapper);
        for(InternshipInfoByStudent infoByStudent:list){
            if(infoByStudent.getInternshipType().equals(3)){
                if(infoByStudent.getStatus().equals(1) || infoByStudent.getStatus().equals(3)){
                    return CommonResult.error(500,"已经存在审核中或已通过就业上报，无法进行再次上报");
                }
            }
        }
        boolean save = this.save(internshipInfoByStudent);
        if(!save){
            return CommonResult.error(500,"就业上报提交失败");
        }
        return CommonResult.success("就业上报提交成功");
    }


    /**
     * 展示就业上报内容
     * */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    @Override
    public CommonResult<Object> employmentReportedDisplay(InternshipApplicationVo vo, UserDto userDto) {
        Integer internshipId = vo.getInternshipId();
        Map<String,Object> map = new HashMap<>();
        InternshipInfoByStudent internshipInfoByStudent = this.getById(internshipId);
        if(internshipInfoByStudent!=null){
            if(internshipInfoByStudent.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该条就业上报已被删除");
            }
            map.put("internship",internshipInfoByStudent);
        }else{
            return CommonResult.error(500,"查无此条");
        }
        return CommonResult.success(map);
    }

    /**
     * 就业上报列表（学生端）
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> employmentReportedDisplayList(InternshipApplicationVo vo, UserDto userDto) {
        Integer relationPlanId = vo.getRelationPlanId();
        Integer relationStudentId = userDto.getStudentInfo().getId();
        //设置分页参数
        Page<InternshipInfoByStudent> page = new Page<>(vo.getPage(), vo.getSize());
        Map<String,Object> map = new HashMap<>();
        QueryWrapper<InternshipInfoByStudent> queryWrapper = new QueryWrapper<>();
        //internship_type字段中1为实习申请
        queryWrapper.eq("deleted",0).eq("relation_plan_id",relationPlanId).eq("relation_student_id",relationStudentId)
                .eq("internship_type",3);
        IPage<InternshipInfoByStudent> iPage = super.page(page, queryWrapper);

        List<InternshipInfoByStudent> list = iPage.getRecords();
        map.put(BaseVo.LIST,list);
        //总页数
        map.put(BaseVo.PAGE,iPage.getPages());
        //总数
        map.put(BaseVo.TOTAL,iPage.getTotal());
        return CommonResult.success(map);
    }

    /*----------------------教师------------------------*/
    /**
     * 实习申请、实习岗位修改与企业变更申请、免实习申请、就业上报审核列表（教师）
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> internshipInfoCheckedList(InternshipApplicationVo vo, UserDto userDto) {
//        Integer relationPlanId = userDto.getInternshipPlanInfo().getId();
//        vo.setRelationPlanId(relationPlanId);
        Integer teacherId = userDto.getTeacherInfo().getId();
        List<InternshipCheckDto> internshipCheckList = null;
        Integer count = 0;
        vo.setTeacherId(teacherId);
        if(vo.getIsCheck().equals(0)){
            //未审核
            internshipCheckList = this.baseMapper.internshipInfoUnCheckList(vo);
            count = this.baseMapper.countInternShipInfoUnCheck(vo);

        }else if(vo.getIsCheck().equals(1)){
            //已审核
            internshipCheckList = this.baseMapper.internshipInfoCheckList(vo);
            count = this.getBaseMapper().countInternShipInfoChecked(vo);
        }
        Map<String,Object> map = new HashMap<>();
        map.put(BaseVo.LIST,internshipCheckList);
        map.put(BaseVo.PAGE,BaseVo.calculationPages(vo.getSize(),count));
        map.put(BaseVo.TOTAL,count);
        vo.setIsCheck(0);
        Integer unCheckCount = this.baseMapper.countInternShipInfoUnCheck(vo);
        map.put("unCheckCount",unCheckCount);
        return CommonResult.success(map);
    }

    /**
     * 教师审核
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> internshipInfoCheck(InternshipApplicationVo vo, UserDto userDto) {
        Integer internshipId = vo.getInternshipId();
        Date date =new Date();
        Integer updateBy = userDto.getId();
        //失败原因
        String failReason = vo.getFailReason();
        InternshipInfoByStudent internshipInfoByStudent = this.getById(internshipId);
        //新建一个日志对象
        InternshipInfoByStudentLog infoByStudentLog = new InternshipInfoByStudentLog();
        if(internshipInfoByStudent!=null){
            if(internshipInfoByStudent.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该条实习申请不存在");
            }
            //不是审核中，就不能审核
            if(!internshipInfoByStudent.getStatus().equals(1)){
                return CommonResult.error(500,"该条实习申请不是审核中状态，无法审核");
            }
            if(vo.getCheckStatus().equals(1)){
                infoByStudentLog.setCheckStatus(1);
            }else if(vo.getCheckStatus().equals(2)){
                infoByStudentLog.setCheckStatus(2);
            }else if(vo.getCheckStatus().equals(3)){
                infoByStudentLog.setCheckStatus(3);
            }
            //更新人
            internshipInfoByStudent.setUpdateBy(updateBy);
            //日志创建人
            infoByStudentLog.setCreateBy(updateBy);
            infoByStudentLog.setRelationInternshipInfoByStudentId(internshipId);
            if(vo.getInternshipStatus().equals(0)){
                //审核失败
                internshipInfoByStudent.setFailReason(failReason);
                infoByStudentLog.setFailReason(failReason);
                internshipInfoByStudent.setStatus(2);
                infoByStudentLog.setStatus(2);
            }else if(vo.getInternshipStatus().equals(1)){
                //审核成功
                internshipInfoByStudent.setStatus(3);
                infoByStudentLog.setStatus(3);
            }
            infoByStudentLog.setCheckTime(date);
        }else{
            return CommonResult.error(500,"查无此条");
        }
        //判断是第一次审核的通过审核的实习申请
        if(internshipInfoByStudent.getInternshipType().equals(1)){
            if(vo.getCheckStatus().equals(1)){
                if(vo.getInternshipStatus().equals(1)){
                    Integer studentId = internshipInfoByStudent.getRelationStudentId();
                    StudentInfo studentInfo = studentInfoService.getById(studentId);
                    if(studentInfo != null){
                        studentInfo.setStudyStatus(1);
                }
                    boolean save = studentInfoService.updateById(studentInfo);
                    if(!save){
                        return CommonResult.error(500,"增加学生上岗信息失败");
                    }
                }
            }
        }

        boolean flag = this.updateById(internshipInfoByStudent);
        if(!flag){
            return CommonResult.error(500,"审核操作失败");
        }
        boolean save = internshipInfoByStudentLogService.save(infoByStudentLog);
        if(!save){
            return CommonResult.error(500,"审核日志保存失败");
        }
        return CommonResult.success("审核操作成功");
    }

/**
*@author: GG
*@data: 2022/7/5 11:26
*@function:实习岗位中未审核数量，已审核的通过数量、未通过数量
*/
@Override
public CommonResult<Object> countInternshipInfoCheck(InternshipApplicationVo vo, UserDto userDto) {
    Integer count =0;
//    Integer relationPlanId = userDto.getInternshipPlanInfo().getId();
//    vo.setRelationPlanId(relationPlanId);
    //未审核
    if(vo.getIsCheck().equals(0)){
        count = this.baseMapper.countInternShipInfoUnCheck(vo);
    }else if(vo.getIsCheck().equals(1)){
        //已审核
        count = this.baseMapper.countInternShipInfoChecked(vo);
    }
    Map<String,Object> map = new HashMap<>();
    map.put("count",count);
    return CommonResult.success(map);
}

/**
*@author: GG
*@data: 2022/7/19 10:24
*@function:获取岗位审核中三个列表的未审核数量
*/
@Override
public CommonResult<Object> countCheck(InternshipApplicationVo vo, UserDto userDto) {
    vo.setIsCheck(0);
    //申请类型 1-普通申请 2-免实习申请 3-就业上报 4-实习总评填写信息
    vo.setInternshipType(1);
    vo.setTeacherId(userDto.getTeacherInfo().getId());
    /**1-第一次审核 2-岗位审核 3-企业审核*/
    vo.setCheckStatus(1);
    Integer count1 = this.baseMapper.countInternShipInfoUnCheck(vo);
    vo.setCheckStatus(2);
    Integer count2 = this.baseMapper.countInternShipInfoUnCheck(vo);
    vo.setCheckStatus(3);
    Integer count3 = this.baseMapper.countInternShipInfoUnCheck(vo);
    Map<String,Object> map = new HashMap<>();
    map.put("normalCount",count1);
    map.put("jobCount",count2);
    map.put("companyCount",count3);
    return CommonResult.success(map);
    }

    /**
    *@author: GG
    *@data: 2022/7/19 10:24
    *@function:获取实习岗位已填报学生列表
    */
    @Override
    public CommonResult<Object> getInternshipApplyInfoFilled(InternshipApplicationVo vo, UserDto userDto) {
//        Integer planId = userDto.getInternshipPlanInfo().getId();
        Integer planId = vo.getPlanId();
        Integer teacherId = userDto.getTeacherInfo().getId();
        vo.setPlanId(planId);
        vo.setTeacherId(teacherId);
        List<InternshipInfoFillDto> list = this.baseMapper.getInternshipApplyInfoFilled(vo);
        Integer filledCount = this.baseMapper.getInternshipApplyInfoFilledCount(vo);
        Integer UnfilledCount = this.baseMapper.getInternshipApplyInfoUnFillCount(vo);
        Map<String,Object> map = new HashMap<>();
        map.put(BaseVo.LIST,list);
        map.put(BaseVo.TOTAL,filledCount);
        map.put("filledCount",filledCount);
        map.put("UnfilledCount",UnfilledCount);
        map.put(BaseVo.PAGE,BaseVo.calculationPages(vo.getSize(),filledCount));
        return CommonResult.success(map);
    }
    
    
    /**
    *@author: GG
    *@data: 2022/7/19 10:24
    *@function:获取实习岗位未填报学生列表
    */
    @Override
    public CommonResult<Object> getInternshipApplyInfoUnFill(InternshipApplicationVo vo, UserDto userDto) {
//        Integer planId = userDto.getInternshipPlanInfo().getId();
        Integer planId = vo.getPlanId();
        Integer teacherId = userDto.getTeacherInfo().getId();
        vo.setPlanId(planId);
        vo.setTeacherId(teacherId);
        List<InternshipInfoFillDto> list = this.baseMapper.getInternshipApplyInfoUnFill(vo);
        Integer filledCount = this.baseMapper.getInternshipApplyInfoFilledCount(vo);
        Integer UnfilledCount = this.baseMapper.getInternshipApplyInfoUnFillCount(vo);
        Map<String,Object> map = new HashMap<>();
        map.put(BaseVo.LIST,list);
        map.put(BaseVo.TOTAL,UnfilledCount);
        map.put("filledCount",filledCount);
        map.put("UnfilledCount",UnfilledCount);
        map.put(BaseVo.PAGE,BaseVo.calculationPages(vo.getSize(),UnfilledCount));
        return CommonResult.success(map);
    }
}
