package com.above.service.impl;

import com.above.dto.LeaveApplyInfoDto;
import com.above.dto.UserDto;
import com.above.exception.OptionDateBaseException;
import com.above.po.*;
import com.above.dao.LeaveApplyInfoMapper;
import com.above.service.*;
import com.above.utils.CommonResult;
import com.above.utils.DateUtil;
import com.above.vo.BaseVo;
import com.above.vo.LeaveApplyInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 申请请假表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Service
public class LeaveApplyInfoServiceImpl extends ServiceImpl<LeaveApplyInfoMapper, LeaveApplyInfo> implements LeaveApplyInfoService {

    @Autowired
    private LeaveApplyInfoLogService leaveApplyInfoLogService;
    @Autowired
    private InternshipWithTeacherService internshipWithTeacherService;
    @Autowired
    private StudentInfoService studentInfoService;
    @Autowired
    private ClassTeacherRelationService classTeacherRelationService;
    @Autowired
    private SignInfoByStudentService signInfoByStudentService;


    /**
     * 提交请假申请
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> submitLeaveApplyInfo(LeaveApplyInfoVo vo, UserDto userDto) throws ParseException {

        //创建人
        Integer createBy = userDto.getId();
        //开始时间
        Date startTime = vo.getStartTime();
        Long date= DateUtil.dateToStamp(startTime);
        //结束时间
        Date endTime = vo.getEndTime();
        Long date2= DateUtil.dateToStamp(endTime);
        //时长
        String duration = vo.getDuration();
        //请假类型
        Integer type = vo.getType();
        //请假事由
        String reason = vo.getReason();

        if(date2 < date){
            return CommonResult.error(500,"结束时间不能大于开始时间");
        }
        QueryWrapper<LeaveApplyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",BaseVo.UNDELETE).eq("create_by",createBy).eq("status",1);
        List<LeaveApplyInfo> list = this.list(queryWrapper);
        if(list.size()>0){
            return CommonResult.error(500,"存在审核中请假申请，无法再次申请");
        }
        QueryWrapper<LeaveApplyInfo> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("deleted",BaseVo.UNDELETE).eq("create_by",createBy).eq("status",3);
        queryWrapper2.between("start_time",startTime,endTime).or().between("end_time",startTime,endTime);
        List<LeaveApplyInfo> list1 = this.list(queryWrapper2);
        if(list1.size()>0){
            return CommonResult.error(500,"与生效中申请时间重复，无法再次申请");
        }
        //新建对象
        LeaveApplyInfo leaveApplyInfo = new LeaveApplyInfo();
        leaveApplyInfo.setPlanId(userDto.getInternshipPlanInfo().getId());
        leaveApplyInfo.setStudentId(userDto.getStudentInfo().getId());
        leaveApplyInfo.setInternshipId(userDto.getInternshipInfo().getId());
        leaveApplyInfo.setStartTime(startTime);
        leaveApplyInfo.setEndTime(endTime);
        leaveApplyInfo.setDuration(duration);
        leaveApplyInfo.setType(type);
        leaveApplyInfo.setReason(reason);
        if(vo.getImgUrl()!=null){
            //图片
            String imgUrl = vo.getImgUrl();
            leaveApplyInfo.setImgUrl(imgUrl);
        }
        leaveApplyInfo.setCreateBy(createBy);
        leaveApplyInfo.setStatus(1);
        //保存
        boolean save = this.save(leaveApplyInfo);
        if(!save){
            return CommonResult.error(500,"请假申请提交失败");
        }
        return CommonResult.success("请假申请提交成功");
    }

    /**
     * 编辑请假申请
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> modifyLeaveApplyInfo(LeaveApplyInfoVo vo, UserDto userDto) throws ParseException {
        //更新人
        Integer updateBy = userDto.getId();
        //请假单Id
        Integer leaveApplyId = vo.getLeaveApplyId();
        //开始时间
        Date startTime = vo.getStartTime();
        Long date= DateUtil.dateToStamp(startTime);
        //结束时间
        Date endTime = vo.getEndTime();
        Long date2= DateUtil.dateToStamp(endTime);

        if(date2 < date){
            return CommonResult.error(500,"结束时间不能大于开始时间");
        }
        //改动标志
        boolean flag = false;

        QueryWrapper<LeaveApplyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",BaseVo.UNDELETE).eq("create_by",updateBy).eq("status",1);
        List<LeaveApplyInfo> list = this.list(queryWrapper);
        if(list.size()>0){
            return CommonResult.error(500,"存在审核中请假申请，无法再次申请");
        }

        QueryWrapper<LeaveApplyInfo> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("deleted",BaseVo.UNDELETE).eq("create_by",updateBy).eq("status",3);
        List<LeaveApplyInfo> list2 = this.list(queryWrapper2);
        queryWrapper2.between("start_time",startTime,endTime).or().between("end_time",startTime,endTime).eq("deleted",BaseVo.UNDELETE).eq("create_by",updateBy).eq("status",3);
        List<LeaveApplyInfo> list1 = this.list(queryWrapper2);
        if(list1.size()>0){
            return CommonResult.error(500,"与生效中申请时间重复，无法提交申请");
        }

        LeaveApplyInfo leaveApplyInfo = this.getById(leaveApplyId);
        if(leaveApplyInfo!=null){
            if(leaveApplyInfo.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该申请已被删除，无法修改");
            }
            //0为草稿，2为驳回
            if(leaveApplyInfo.getStatus().equals(0) || leaveApplyInfo.getStatus().equals(2)){
                if(vo.getStartTime()!=null){
                    //开始时间
                    if(!leaveApplyInfo.getStartTime().equals(startTime)){
                        leaveApplyInfo.setStartTime(startTime);
                        flag=true;
                    }
                }
                if(vo.getEndTime()!=null){
                    //结束时间
                    if(!leaveApplyInfo.getEndTime().equals(endTime)){
                        leaveApplyInfo.setEndTime(endTime);
                        flag=true;
                    }
                }
                if(vo.getDuration()!=null){
                    //时长
                    String duration = vo.getDuration();
                    if(!leaveApplyInfo.getDuration().equals(duration)){
                        leaveApplyInfo.setDuration(duration);
                        flag=true;
                    }
                }
                if(vo.getType()!=null){
                    //请假类型
                    Integer type = vo.getType();
                    if(!leaveApplyInfo.getType().equals(type)){
                        leaveApplyInfo.setType(type);
                        flag=true;
                    }
                }
                if(vo.getReason()!=null){
                    //请假事由
                    String reason = vo.getReason();
                    if(!leaveApplyInfo.getReason().equals(reason)){
                        leaveApplyInfo.setReason(reason);
                        flag=true;
                    }
                }
                if(vo.getImgUrl()!=null){
                    String imgUrl = vo.getImgUrl();
                    if(!leaveApplyInfo.getImgUrl().equals(imgUrl)){
                        leaveApplyInfo.setImgUrl(imgUrl);
                        flag=true;
                    }
                }
            }else{
                return CommonResult.error(500,"只有草稿和驳回状态的请假单可以修改");
            }
        }else{
            return CommonResult.error(500,"查无此条");
        }
            leaveApplyInfo.setUpdateBy(updateBy);
            leaveApplyInfo.setStatus(1);
            boolean save = this.updateById(leaveApplyInfo);
            if(!save){
                return CommonResult.error(500,"请假申请修改失败");
            }
            return CommonResult.success("请假申请修改成功");
    }

    /**
     * 撤回请假申请
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> withdrawLeaveApplyInfo(LeaveApplyInfoVo vo, UserDto userDto) {
        //更新人
        Integer updateBy = userDto.getId();
        //请假单Id
        Integer leaveApplyId = vo.getLeaveApplyId();
        LeaveApplyInfo leaveApplyInfo = this.getById(leaveApplyId);
        if(leaveApplyInfo!=null){
            if(leaveApplyInfo.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该申请已被删除，无法撤回");
            }
            //1为待审核,0为草稿
            if(leaveApplyInfo.getStatus().equals(1)) {
                leaveApplyInfo.setStatus(0);
                leaveApplyInfo.setUpdateBy(updateBy);
            }else{
                return CommonResult.error(500,"只有待审核状态的请假单可以撤回");
            }
        }else{
            return CommonResult.error(500,"查无此条");
        }
        boolean save = this.updateById(leaveApplyInfo);
        if(!save){
            return CommonResult.error(500,"请假申请撤回失败");
        }
        return CommonResult.success("请假申请撤回成功");
    }

    /**
     * 教师审核请假申请
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = OptionDateBaseException.class)
    public CommonResult<Object> checkLeaveApplyInfo(LeaveApplyInfoVo vo, UserDto userDto) throws OptionDateBaseException {
        Integer updateBy = userDto.getId();
        //审核状态，0为失败，1为通过
        Integer checkStatus = vo.getCheckStatus();
        //请假单Id
        Integer leaveApplyId = vo.getLeaveApplyId();
        LeaveApplyInfo leaveApplyInfo = this.getById(leaveApplyId);
        if(leaveApplyInfo!=null){
            if(leaveApplyInfo.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该申请已被删除，无法撤回");
            }
            //开始时间
            Date startTime = leaveApplyInfo.getStartTime();
            //结束时间
            Date endTime = leaveApplyInfo.getEndTime();
            //关联id
            Integer applyId = leaveApplyInfo.getId();
            //1为待审核,0为草稿
            if(leaveApplyInfo.getStatus().equals(1)) {
                if(checkStatus.equals(0)){
                    //审核失败
                    leaveApplyInfo.setStatus(2);
                    //失败原因
                    leaveApplyInfo.setFailReason(vo.getFailReason());
                }else if(checkStatus.equals(1)){
                    //审核通过
                    leaveApplyInfo.setStatus(3);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    //请假成功后把期间内所有的签到记录改为免签
                    LambdaQueryWrapper<SignInfoByStudent> signInfoByStudentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    signInfoByStudentLambdaQueryWrapper.eq(SignInfoByStudent::getStudentId,leaveApplyInfo.getStudentId())
                            .eq(SignInfoByStudent::getInternshipPlanId,leaveApplyInfo.getPlanId())
                            .between(SignInfoByStudent::getSignDate,format.format(startTime),format.format(endTime));
                    List<SignInfoByStudent> list = signInfoByStudentService.list(signInfoByStudentLambdaQueryWrapper);

                    //判断如果大于0则修改
                    if (list.size() > 0){
                        //遍历修改需要审核的
                        list.forEach(info -> {
                            Date todaynoon = DateUtil.getTodaynoon(info.getSignDate());
                            /*早上判断开始时间是否12点前 ，若在12点前则早上无需打卡
                              下午则判断结束时间是否在12点后，若在12点后则早上无需打卡*/
                            if (info.getIsMorning().equals(1)){
                                if (startTime.before(todaynoon)){
                                    info.setSignStatus(3);
                                }
                            }
                            if (info.getIsMorning().equals(2)){
                                if (endTime.after(todaynoon)){
                                    info.setSignStatus(3);
                                }
                            }
                        });
                        List<SignInfoByStudent> collect = list.stream().filter(info -> info.getSignStatus().equals(3)).collect(Collectors.toList());
                        if (collect.size() > 0){
                            boolean b = signInfoByStudentService.updateBatchById(collect);
                            if (!b){
                                return CommonResult.error(500,"请假申请审核失败");
                            }
                        }
                    }


                }
                leaveApplyInfo.setUpdateBy(updateBy);
            }else{
                return CommonResult.error(500,"只有待审核状态的请假单可以进行审核操作");
            }
        }
        boolean save = this.updateById(leaveApplyInfo);
        if(!save){
            throw new OptionDateBaseException("请假申请审核失败");
        }

        return CommonResult.success("请假申请审核成功");
    }

    /**
     * 通过学生用户id拿到请假单列表
     * */
    @Override
    public CommonResult<Object> getLeaveApplyByUserId(LeaveApplyInfoVo vo, UserDto userDto) {
        Integer id = userDto.getId();
        Map<String,Object> map = new HashMap<>();
        vo.setCreateBy(userDto.getId());
        List<LeaveApplyInfoDto> list = this.baseMapper.getLeaveApplyListByUserId(vo);
        for(LeaveApplyInfoDto applyInfoDto : list){
            if(applyInfoDto.getStatus().equals(0) || applyInfoDto.getStatus().equals(1)){
                applyInfoDto.setTime(applyInfoDto.getCreateTime());
            }else if(applyInfoDto.getStatus().equals(2) || applyInfoDto.getStatus().equals(3)){
                applyInfoDto.setTime(applyInfoDto.getUpdateTime());
            }
        }
        Integer count = this.baseMapper.getLeaveApplyListByUserIdCount(vo);
        map.put(BaseVo.LIST,list);
        map.put(BaseVo.TOTAL,count);
        map.put(BaseVo.PAGE,BaseVo.calculationPages(vo.getSize(),count));
        return CommonResult.success(map);
    }

    /**
    *@author: GG
    *@data: 2022/7/5 15:58
    *@function:通过请假单id拿到请假单内容
    */
    @Override
    public CommonResult<Object> getLeaveApplyByApplyId(LeaveApplyInfoVo vo, UserDto userDto) {
        Integer applyId = vo.getLeaveApplyId();
        Integer instructorId = 0;
        //通过学生id拿到班级id
        QueryWrapper<StudentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",BaseVo.UNDELETE).eq("user_id",userDto.getId());
        StudentInfo studentInfo = studentInfoService.getOne(queryWrapper);
        if(studentInfo != null){
            //获得班级id
            Integer classId = studentInfo.getClassId();
            //通过班级id拿到辅导员id
            QueryWrapper<ClassTeacherRelation> classTeacherRelationQueryWrapper = new QueryWrapper<>();
            classTeacherRelationQueryWrapper.eq("deleted",BaseVo.UNDELETE).eq("class_id",classId).eq("relation_type",1);
            ClassTeacherRelation classTeacherRelation = classTeacherRelationService.getOne(classTeacherRelationQueryWrapper);
            if(classTeacherRelation != null){
                instructorId = classTeacherRelation.getTeacherId();
            }
        }
        if(instructorId >0){
            vo.setTeacherId(instructorId);
        }
        Map<String,Object> map = new HashMap<>();
        LeaveApplyInfoDto leaveApplyInfoDto = null;
        if(userDto.getUserRoleDto().getRoleCode().equals("student")){
            vo.setStudentId(userDto.getStudentInfo().getId());
        }else if(userDto.getUserRoleDto().getRoleCode().equals("instructor")){
            vo.setTeacherId(userDto.getTeacherInfo().getId());
        }
        if(vo.getLeaveApplyId() != null){
            leaveApplyInfoDto = this.baseMapper.getLeaveApplyByIdUnCheck(vo);
        }
        if(leaveApplyInfoDto == null){
            return CommonResult.error(500,"查询不到请假单信息");
        }
        map.put("leaveApplyInfo",leaveApplyInfoDto);
        return CommonResult.success(map);
    }

    /**
    *@author: GG
    *@data: 2022/7/7 10:16
    *@function:判断学生是否已经有审核中请假单
    */
    @Override
    public CommonResult<Object> judgeLeaveApply(LeaveApplyInfoVo vo, UserDto userDto) {
        Integer createBy = userDto.getId();
        QueryWrapper<LeaveApplyInfo> queryWrapper = new QueryWrapper<>();
        Map<String,Object> map = new HashMap<>();
        boolean flag = false;
        queryWrapper.eq("deleted",BaseVo.UNDELETE).eq("create_by",createBy).eq("status",1);
        List<LeaveApplyInfo> list = this.list(queryWrapper);
        if(list.size()>0){
            return CommonResult.error(500,"存在审核中请假申请，无法再次申请");
        }
        flag = true;
        map.put("flag",flag);
        return CommonResult.success(map);
    }

    /**
     *辅导员端请假申请列表(辅导员)
     * */
    @Override
    public CommonResult<Object> leaveApplyListByInstruct(LeaveApplyInfoVo vo, UserDto userDto) {
        Map<String,Object> returnMap=new HashMap<>(16);
        //拿到实习计划id
//        vo.setPlanId(userDto.getInternshipPlanInfo().getId());
        //拿到教师id
        vo.setTeacherId(userDto.getTeacherInfo().getId());
        //未审核
        List<LeaveApplyInfoDto> list = this.baseMapper.leaveApplyCheckList(vo);
        for(LeaveApplyInfoDto applyInfoDto : list){
            if(applyInfoDto.getStatus().equals(0) || applyInfoDto.getStatus().equals(1)){
                applyInfoDto.setTime(applyInfoDto.getCreateTime());
            }else if(applyInfoDto.getStatus().equals(2) || applyInfoDto.getStatus().equals(3)){
                applyInfoDto.setTime(applyInfoDto.getUpdateTime());
            }
        }
        Integer count = this.baseMapper.countLeaveApplyCheckList(vo);
        returnMap.put(BaseVo.LIST,list);
        returnMap.put(BaseVo.TOTAL,count);
        returnMap.put(BaseVo.PAGE,BaseVo.calculationPages(vo.getSize(),count));
        vo.setIsCheck(0);
        Integer countUnCheck = this.baseMapper.countLeaveApplyCheckList(vo);
        returnMap.put("unCheckCount",countUnCheck);
        return CommonResult.success(returnMap);
    }

}
