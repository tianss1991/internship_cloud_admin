package com.above.service.impl;

import com.above.dao.LeaveApplyInfoMapper;
import com.above.dao.StudentInfoMapper;
import com.above.dto.SignAndApplyDto;
import com.above.dto.StudentInfoDto;
import com.above.dto.UserDto;
import com.above.po.*;
import com.above.dao.SignInfoByStudentMapper;
import com.above.service.*;
import com.above.utils.CommonResult;
import com.above.utils.DateUtil;
import com.above.utils.MyMathUtil;
import com.above.vo.BaseVo;
import com.above.vo.SignInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * <p>
 * 签到记录表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Slf4j
@Service
public class SignInfoByStudentServiceImpl extends ServiceImpl<SignInfoByStudentMapper, SignInfoByStudent> implements SignInfoByStudentService {


    /**
     * 默认打卡距离
     */
    private static final BigDecimal DEFAULT_DISTANT = new BigDecimal("100");

    @Autowired
    private InternshipPlanInfoService planInfoService;
    @Autowired
    private StudentInfoService studentInfoService;
    @Autowired
    private InternshipInfoByStudentService infoByStudentService;
    @Autowired
    private StudentInfoMapper studentInfoMapper;
    @Autowired
    private LeaveApplyInfoMapper leaveApplyInfoMapper;

    /**
     *  自动生成实习记录
     * @throws ParseException  时间转换异常抛出
     */
    @Override
    public void generateSignLogForStudent() throws ParseException {
        //当前时间
        Date now = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(sdf.format(now));
        /*
        查找当前时间，开始的实习计划，对该计划下所有的学生生成打卡记录
        打卡记录一天生成四条，上午打卡，下午打卡各两次
        */
        LambdaQueryWrapper<InternshipPlanInfo> planQueryWrapper = new LambdaQueryWrapper<>();
        planQueryWrapper.eq(InternshipPlanInfo::getDeleted, BaseVo.UNDELETE)
                        .le(InternshipPlanInfo::getStartTime,now).ge(InternshipPlanInfo::getEndTime,now);
        List<InternshipPlanInfo> planInfoList = planInfoService.list(planQueryWrapper);
        //全部学生list
        Collection<StudentInfo> allStudentList = new HashSet<>();


        //若集合为空，则跳过
        if (planInfoList != null && planInfoList.size() > 0){

            //不为空则进入执行
            for (InternshipPlanInfo planInfo : planInfoList) {
                Integer departmentId = planInfo.getDepartmentId();
                Integer majorId = planInfo.getMajorId();
                Integer gradeId = planInfo.getGradeId();
                //查找改实习计划下的所有学生
                LambdaQueryWrapper<StudentInfo> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                studentLambdaQueryWrapper.eq(StudentInfo::getDepartmentId,departmentId).eq(StudentInfo::getGradeId,gradeId)
                        .eq(StudentInfo::getMajorId,majorId).eq(StudentInfo::getDeleted,BaseVo.UNDELETE);
                List<StudentInfo> studentInfoList = studentInfoService.list(studentLambdaQueryWrapper);
                //若学生不为空则添加进数组
                if (studentInfoList != null && studentInfoList.size() > 0){
                    //放入实习计划关联id
                    studentInfoList.forEach( info -> info.setPlanInfo(planInfo));
                    //放入到学生数组中
                    allStudentList.addAll(studentInfoList);
                }
            }
            //若学生为空则不操作
            if (!allStudentList.isEmpty()){

                Collection<SignInfoByStudent> studentSignInfo = getStudentSignInfo(allStudentList, date);

                if (studentSignInfo.size() > 0){
                    /*遍历完成后进行批量插入*/
                    boolean saveBatch = super.saveBatch(studentSignInfo,1000);

                    if (saveBatch){
                        log.info("成功插入["+studentSignInfo.size()+"]条数据");
                    }else {
                        log.error("插入数据库出错");
                    }
                }else {
                    log.info("无数据要添加");
                }

            }else {
                log.info("找到【"+planInfoList.size()+"】个进行中的实习计划，但计划下无学生。");
            }

        }else {
            log.info("无实习计划进行中");
        }
    }

    /**
     * @param userDto    当前用户
     * @param signInfoVo 前端参数
     * @Description: 学生获取今天打卡列表
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    @Override
    public CommonResult<Object> getTodaySignList(UserDto userDto, SignInfoVo signInfoVo) throws ParseException {
        //获取当前用户
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        if (roleCode.equals(AuthRole.STUDENT)){
            //查询岗位信息
            InternshipPlanInfo internshipPlanInfo = userDto.getInternshipPlanInfo();
            if (internshipPlanInfo == null){
                return CommonResult.error(500,"当前无实习计划");
            }
            //判断实习岗位信息是否存在
            InternshipInfoByStudent internshipInfo = userDto.getInternshipInfo();
            if (internshipInfo == null){
                return CommonResult.error(500,"当前无实习岗位信息");
            }
            signInfoVo.setPlanId(internshipPlanInfo.getId());
            signInfoVo.setStudentId(userDto.getStudentInfo().getId());
        }
        //如果没传时间，默认当天
        if (signInfoVo.getDate() == null){
            //格式化当前时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String format = sdf.format(new Date());
            signInfoVo.setDate(sdf.parse(format));
        }
        //判断参数是否为空
        if (signInfoVo.getStudentId() == null){
            return CommonResult.error(500,"缺少学生id");
        }
        if (signInfoVo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        //查询
        List<SignAndApplyDto> list = super.baseMapper.getSignWithApplyIdListByDate(signInfoVo);
        //1-上班打卡 2-下班打卡
        int flag = 2;
        //循环遍历，查看当前是什么类型打卡
        for (SignAndApplyDto info:list) {
            //获取第一条未打卡记录
            if (info.getIsSign().equals(0)){
                flag = info.getSignType();
                break;
            }
        }

        //封装返回对象
        HashMap<String, Object> returnMap = new HashMap<>(16);
        returnMap.put(BaseVo.LIST,list);
        returnMap.put("flag",flag);

        return CommonResult.success(returnMap);
    }

    /**
     * @param userDto    当前用户
     * @param signInfoVo 前端参数
     * @Description: 学生获取本月打卡列表
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    @Override
    public CommonResult<Object> getMonthSignList(UserDto userDto, SignInfoVo signInfoVo) throws ParseException {
        //获取当前用户的权限
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        //处理月份时间
        Date month = signInfoVo.getMonth();
        Date startTime = DateUtil.getFirstDayOfMonth(month);
        Date endTime = DateUtil.getMaxDayOfMonth(month);
        signInfoVo.setStartTime(startTime);
        signInfoVo.setEndTime(endTime);

        //学生角色执行的操作
        if (roleCode.equals(AuthRole.STUDENT)){
            //查询岗位信息
            InternshipPlanInfo internshipPlanInfo = userDto.getInternshipPlanInfo();
            if (internshipPlanInfo == null){
                return CommonResult.error(500,"当前无实习计划");
            }
            //判断实习岗位信息是否存在
            InternshipInfoByStudent internshipInfo = userDto.getInternshipInfo();
            if (internshipInfo == null){
                return CommonResult.error(500,"当前无实习岗位信息");
            }
            signInfoVo.setPlanId(internshipPlanInfo.getId());
            signInfoVo.setStudentId(userDto.getStudentInfo().getId());
        }
        //判断参数是否为空
        if (signInfoVo.getStudentId() == null){
            return CommonResult.error(500,"缺少学生id");
        }
        if (signInfoVo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        //查询该范围内该学生的数据
        List<SignAndApplyDto> list = super.baseMapper.getSignWithApplyListByStartAndEndTime(signInfoVo);

        //处理数据
        Map<String, Object> objectMap = processDataMap(list, startTime, endTime);

        //获取处理好的数据
        List<String> dateList = (List<String>) objectMap.get("dateList");
        Map<String, Object> infoMap = (HashMap<String, Object>) objectMap.get("returnMap");

        //封装返回对象
        List<Map<String, Object>> returnMap = getReturnMap(dateList, infoMap);

        return CommonResult.success(returnMap);
    }

    /**
     * @param userDto    当前用户
     * @param signInfoVo 前端参数
     * @Description: 学生签到明细列表
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    @Override
    public CommonResult<Object> getStudentSignDetailList(UserDto userDto, SignInfoVo signInfoVo) {
        //获取当前用户的权限
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        //学生角色执行的操作
        if (roleCode.equals(AuthRole.STUDENT)){
            //查询岗位信息
            InternshipPlanInfo internshipPlanInfo = userDto.getInternshipPlanInfo();
            if (internshipPlanInfo == null){
                return CommonResult.error(500,"当前无实习计划");
            }
            //判断实习岗位信息是否存在
            InternshipInfoByStudent internshipInfo = userDto.getInternshipInfo();
            if (internshipInfo == null){
                return CommonResult.error(500,"当前无实习岗位信息");
            }
            signInfoVo.setPlanId(internshipPlanInfo.getId());
            signInfoVo.setStudentId(userDto.getStudentInfo().getId());
        }

        //判断参数是否为空
        if (signInfoVo.getStudentId() == null){
            return CommonResult.error(500,"缺少学生id");
        }
        if (signInfoVo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }
        //获取分页列表
        List<SignAndApplyDto> list = super.baseMapper.getSignListDetail(signInfoVo);
        Integer totalCount = super.baseMapper.getSignListDetailTotalCount(signInfoVo);
        //返回对象
        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap.put(BaseVo.LIST,list);
        returnMap.put(BaseVo.TOTAL,totalCount);
        returnMap.put(BaseVo.PAGE,BaseVo.calculationPages(signInfoVo.getSize(),totalCount));

        return CommonResult.success(returnMap);
    }

    /**
     * 学生签到接口
     *
     * @param userDto 当前用户
     * @param signInfoVo 前端参数
     * @Author: LZH
     * @Date: 2022/7/12 13:58
     */
    @Override
    public CommonResult<Object> studentSignIn(UserDto userDto, SignInfoVo signInfoVo) {
        //获取打卡记录
        SignInfoByStudent signInfoByStudent = super.getById(signInfoVo.getSignId());
        if (signInfoByStudent == null){
            return CommonResult.error(500,"未找到打卡记录");
        }
        //获取岗位地点
        String latitude1 = userDto.getInternshipInfo().getLatitude();
        String longitude1 = userDto.getInternshipInfo().getLongitude();
        //若学生用户信息中的经纬度不存在则跳过验证
        if (!StringUtils.isBlank(latitude1) && !StringUtils.isBlank(longitude1)){
            //判断打卡地点
            String latitude2 = signInfoVo.getLatitude();
            String longitude2 = signInfoVo.getLongitude();
            //判断前端是否有传
            if (StringUtils.isBlank(latitude2) || StringUtils.isBlank(longitude2)){
                return CommonResult.error(500,"缺少经纬度");
            }
            //获取距离
            BigDecimal distance = MyMathUtil.getTwoPointDistance(longitude1, latitude1, longitude2, latitude2);
            log.info("打卡距离为{}米",distance.toString());
            //对比是否在默认范围内 -1
            int flag = distance.compareTo(DEFAULT_DISTANT);
            if (flag > 0){
                return CommonResult.error(500,"您不在打卡范围内");
            }
            signInfoByStudent.setLongitude(longitude2).setLatitude(latitude2);
        }
        //打卡时间
        if (signInfoVo.getSignDateTime() == null){
            signInfoVo.setSignDateTime(new Date());
        }

        signInfoByStudent.setIsSign(1).setSignStatus(SignInfoByStudent.NORMAL).setSignTime(signInfoVo.getSignDateTime()).setSignAddress(signInfoVo.getAddress()).setUpdateBy(userDto.getId());
        //图片
        if (signInfoVo.getImgUrl() != null){
            signInfoByStudent.setSignImg(signInfoVo.getImgUrl());
        }
        //备注
        if (signInfoVo.getRemark() != null){
            signInfoByStudent.setRemark(signInfoVo.getRemark());
        }
        //  保存数据库
        boolean save = super.updateById(signInfoByStudent);
        if (!save){
            return CommonResult.error(500,"打卡失败");
        }

        return CommonResult.success("打卡成功");
    }

    /**
     * 获取签到和未签到学生列表
     *
     * @param userDto 用户
     * @param signInfoVo 前端参数
     * @Author: LZH
     * @Date: 2022/7/12 13:58
     */
    @Override
    public CommonResult<Object> getUnSignStudentList(UserDto userDto, SignInfoVo signInfoVo) throws ParseException {
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        //若没有日期则放入今天日期
        if (signInfoVo.getDate() == null){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date today = format.parse(format.format(new Date()));
            signInfoVo.setDate(today);
        }
        //内部数据提取
        List<StudentInfoDto> list;
        Integer totalCount;
        /*判断权限获取列表*/
        if (roleCode.equals(AuthRole.INSTRUCTOR)){
            /*辅导员获取未签到已签到列表*/
             list = studentInfoMapper.selectStudentSignList(signInfoVo);
             totalCount = studentInfoMapper.selectStudentSignListTotalCount(signInfoVo);
        }else if (roleCode.equals(AuthRole.ADVISER)){
            /*指导教师获取未签到已签到列表*/
             list = studentInfoMapper.selectStudentSignListByTeacher(signInfoVo);
             totalCount = studentInfoMapper.selectStudentSignListByTeacherTotalCount(signInfoVo);
        }else {
            return CommonResult.error(500,"无权限");
        }
        //返回对象
        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap.put(BaseVo.LIST,list);
        returnMap.put(BaseVo.TOTAL,totalCount);
        returnMap.put(BaseVo.PAGE,BaseVo.calculationPages(signInfoVo.getSize(),totalCount));

        return CommonResult.success(returnMap);
    }

    /**
     * 打卡自动异常
     *
     * 第二天凌晨查找之前未打卡的记录变为异常
     */
    @Override
    public void automaticPunchChangeToException() {

        //当前时间
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = format.format(now);

        //查找数据库中需要变为异常的签到记录
        LambdaQueryWrapper<SignInfoByStudent> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.lt(SignInfoByStudent::getSignDate,nowDate).eq(SignInfoByStudent::getIsSign,0)
                .isNull(SignInfoByStudent::getSignStatus)
                .eq(SignInfoByStudent::getDeleted,BaseVo.UNDELETE);
        List<SignInfoByStudent> list = super.list(lambdaQueryWrapper);

        //  判断list是否为空
        if (list.size() > 0){
            //状态改为异常
            for (SignInfoByStudent info : list) {
                info.setSignStatus(SignInfoByStudent.EXCEPTION);
            }

            //批量修改
            boolean b = super.updateBatchById(list);
            if (b){
                log.info("更新成功，一共更新{}条数据",list.size());
            }else {
                log.error("更新异常打卡记录失败");
            }
        }else {
            log.info("没有数据要更新");
        }

    }

    /**
     * @Description: 获取月份打卡记录接口-处理返回数据
     * @Author: LZH
     * @Date: 2022/7/12 10:21
     */
    private Map<String, Object> processDataMap(List<SignAndApplyDto> list,Date startTime,Date endTime){

        Map<String, Object> hashMap = new HashMap<>(16);

        //日期格式化
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //日期列表-用于后续取出数据进行操作
        ArrayList<String> dateList = new ArrayList<>();
        //返回对象
        HashMap<String, Object> returnMap = new LinkedHashMap<>();
        int listLong = list.size();
        if (listLong == 0){
            while (startTime.before(endTime) || startTime.equals(endTime)){
                //放入对象
                returnMap.put(format.format(startTime),list);
                //获取下一天时间
                startTime = DateUtil.getNextDay(startTime);
            }
        }else {
            //循环日期时间
            while (startTime.before(endTime) || startTime.equals(endTime)){
                List<SignAndApplyDto> signAndApplyDtos = new LinkedList<>();
                //遍历查出来的对象
                for (int i = 0 ; i < listLong; i++) {
                    SignAndApplyDto signAndApplyDto = list.get(i);
                    //判断日期是否相同，相同则添加到返回数组中
                    if (signAndApplyDto.getSignDate().equals(startTime)){
                        signAndApplyDtos.add(signAndApplyDto);
                        //如果添加过后，则删除该对象，数组长度-1
                        listLong -= 1;
                        i -= 1;
                        list.remove(signAndApplyDto);
                    }
                }
                //日期放入列表中
                dateList.add(format.format(startTime));
                HashMap<String, Object> objectHashMap = new HashMap<>(16);
                objectHashMap.put(BaseVo.LIST,signAndApplyDtos);
                //放入对象
                returnMap.put(format.format(startTime),objectHashMap);
                //获取下一天时间
                startTime = DateUtil.getNextDay(startTime);
            }
        }
        //放入对象中返回
        hashMap.put("returnMap",returnMap);
        hashMap.put("dateList",dateList);

        return hashMap;
    }
    /**
     * @Description: 获取月份打卡记录接口-封装返回对象数据
     * @Author: LZH
     * @Date: 2022/7/12 12:04
     */
    private List<Map<String, Object>> getReturnMap(List<String> dateList,Map<String, Object> infoMap){


        List<Map<String, Object>> maps = new ArrayList<>();

        //拿出每条数据判断
        for (String date:dateList) {
            Map<String, Object> returnMap = new HashMap<>(16);
            //驳回
            boolean fail = false;
            //异常
            boolean exception = false;
            //免打卡
            boolean noPunch = false;
            //未打卡
            boolean noSign = false;

            //取出数据
            Map<String, Object> info = (Map<String, Object>) infoMap.get(date);
            List<SignAndApplyDto> infoList = (List<SignAndApplyDto>) info.get(BaseVo.LIST);
            if (infoList.size() > 0){
                /* 0-未打卡 1-正常 2-异常 3-免打卡 4-驳回*/
                int todayStatus = 1;
                //打卡状态
                for (SignAndApplyDto signInfo:infoList) {
                    //判断签到状态
                    if (signInfo.getSignStatus() == null ){
                        noSign = true;
                    }else if (signInfo.getSignStatus() == 3){
                        //是否免签
                        noPunch = true;
                    }else if (signInfo.getSignStatus() == 2){
                        //是否异常
                        exception = true;
                    }
                    //判断是否有补卡申请被驳回
                    if (signInfo.getApplyStatus() != null && signInfo.getApplyStatus() == 2){
                        //是否失败
                        fail = true;
                    }

                }
                //判断状态
                if (noSign){
                    todayStatus = 0;
                }
                if (exception){
                    todayStatus = 2;
                }
                if (noPunch){
                    todayStatus = 4;
                }
                if (fail){
                    todayStatus = 3;
                }

                returnMap.put("todayStatus",todayStatus);
                returnMap.put(BaseVo.LIST,infoList);
                returnMap.put("date",date);
                maps.add(returnMap);
            }

        }
        return maps;
    }
    /**
     *  对某个学生生成 date日期的 默认2条记录，若times有传则为4
     * @param studentId 学生id
     * @param date 日期 yyyy-MM-dd
     * @return 返回记录list
     */
    private List<SignInfoByStudent> generateSignLog(Integer studentId,Date date, int times){

        List<SignInfoByStudent> signInfoByStudents = new ArrayList<>();

        //上午上班
        SignInfoByStudent signInfoByStudent1 = new SignInfoByStudent();
        signInfoByStudent1.setStudentId(studentId).setSignDate(date).setIsSign(0).setSignType(1).setIsMorning(1);
        signInfoByStudents.add(signInfoByStudent1);

        //下午下班
        SignInfoByStudent signInfoByStudent4 = new SignInfoByStudent();
        signInfoByStudent4.setStudentId(studentId).setSignDate(date).setIsSign(0).setSignType(2).setIsMorning(2);
        signInfoByStudents.add(signInfoByStudent4);

        //对该学生生成四条未打卡记录
        if (times == 4){
            //上午下班
            SignInfoByStudent signInfoByStudent2 = new SignInfoByStudent();
            signInfoByStudent2.setStudentId(studentId).setSignDate(date).setIsSign(0).setSignType(2).setIsMorning(1);
            signInfoByStudents.add(signInfoByStudent2);

            //下午上班
            SignInfoByStudent signInfoByStudent3 = new SignInfoByStudent();
            signInfoByStudent3.setStudentId(studentId).setSignDate(date).setIsSign(0).setSignType(1).setIsMorning(2);
            signInfoByStudents.add(signInfoByStudent3);
        }


        return signInfoByStudents;
    }


    /**
     *  生成添加到数据库中的打卡记录
     * @param allStudentList 学生list
     * @param date 日期
     * @return 返回处理后的所有打卡记录
     */
    private Collection<SignInfoByStudent> getStudentSignInfo(Collection<StudentInfo> allStudentList,Date date){

        //签到记录list
        Collection<SignInfoByStudent> signList = new HashSet<>();

        Date todayFirstSecond = DateUtil.getTodayFirstSecond(date);
        long todaynoon = DateUtil.getTodaynoon(date).getTime();
        Date lastSecond = DateUtil.getTodayLastSecond(date);

        //获取集合中的planId
        List<Integer> planId = allStudentList.stream().map(StudentInfo::getPlanInfo).collect(Collectors.toSet())
                                .stream().map(InternshipPlanInfo::getId).collect(Collectors.toList());
        List<Integer> studentIds = allStudentList.stream().map(StudentInfo::getId).collect(Collectors.toList());
        //查询所有的planId下通过的实习计划
        LambdaQueryWrapper<InternshipInfoByStudent> internshipInfoByStudentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        internshipInfoByStudentLambdaQueryWrapper.in(InternshipInfoByStudent::getRelationPlanId,planId)
                .in(InternshipInfoByStudent::getInternshipType,1,2)
                .eq(InternshipInfoByStudent::getStatus,3)
                .eq(InternshipInfoByStudent::getDeleted,BaseVo.UNDELETE);
        List<InternshipInfoByStudent> allInternShipInfo = infoByStudentService.list(internshipInfoByStudentLambdaQueryWrapper);
        //获取该实习计划下所有的请假申请
        LambdaQueryWrapper<LeaveApplyInfo> leaveApplyInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        leaveApplyInfoLambdaQueryWrapper.in(LeaveApplyInfo::getPlanId,planId).eq(LeaveApplyInfo::getStatus,3)
                .eq(LeaveApplyInfo::getDeleted,BaseVo.UNDELETE)
                .between(LeaveApplyInfo::getStartTime,todayFirstSecond,lastSecond)
                .or().between(LeaveApplyInfo::getEndTime,todayFirstSecond,lastSecond)
                .in(LeaveApplyInfo::getPlanId,planId).eq(LeaveApplyInfo::getStatus,3)
                .eq(LeaveApplyInfo::getDeleted,BaseVo.UNDELETE);
        List<LeaveApplyInfo> leaveApplyInfos = leaveApplyInfoMapper.selectList(leaveApplyInfoLambdaQueryWrapper);
        //查找
        LambdaQueryWrapper<LeaveApplyInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(LeaveApplyInfo::getPlanId,planId).eq(LeaveApplyInfo::getStatus,3)
                .eq(LeaveApplyInfo::getDeleted,BaseVo.UNDELETE)
                .le(LeaveApplyInfo::getStartTime,date).ge(LeaveApplyInfo::getEndTime,date);
        leaveApplyInfos.addAll(leaveApplyInfoMapper.selectList(queryWrapper));
        //核对该学生是否生成过
        LambdaQueryWrapper<SignInfoByStudent> signQueryWrapper = new LambdaQueryWrapper<>();
        signQueryWrapper.eq(SignInfoByStudent::getSignDate,date)
                .in(SignInfoByStudent::getStudentId,studentIds)
                .in(SignInfoByStudent::getInternshipPlanId,planId)
                .eq(SignInfoByStudent::getDeleted,BaseVo.UNDELETE);
        List<SignInfoByStudent> signInfoByStudentList = super.list(signQueryWrapper);
        //遍历当前学生
        for (StudentInfo studentInfo : allStudentList) {

            InternshipPlanInfo planInfo = studentInfo.getPlanInfo();
            //打卡次数
            int signTimes = planInfo.getSignTimes() == null ? 0 : planInfo.getSignTimes();

            List<SignInfoByStudent> signInfoByStudents = generateSignLog(studentInfo.getId(), date , signTimes);



            //查找当前学生已通过的实习信息
            List<InternshipInfoByStudent> list = allInternShipInfo.stream().filter(info -> info.getRelationStudentId().equals(studentInfo.getId())).collect(Collectors.toList());

            //判断是否存在记录
            if (list.size() > 0){
                InternshipInfoByStudent internshipInfo = list.get(0);
                //若flag 为 false 则数据库存在数据
                AtomicBoolean flag = new AtomicBoolean(true);
                //若数量为0则生成记录
                signInfoByStudentList.forEach(info -> {
                    if (studentInfo.getId().equals(info.getStudentId())){
                        if (studentInfo.getPlanInfo().getId().equals(info.getInternshipPlanId())){
                            flag.set(false);
                        }
                    }

                });
                //若数据不存在则添加
                if (flag.get()){
                    boolean morning = false;
                    boolean afternoon = false;
                    if (leaveApplyInfos.size() > 0){
                        List<LeaveApplyInfo> collect = leaveApplyInfos.stream().filter(info -> info.getStudentId().equals(studentInfo.getId())).collect(Collectors.toList());
                        for (LeaveApplyInfo info:collect) {
                            long startTime = info.getStartTime().getTime();
                            long endTime = info.getEndTime().getTime();
                            //判断请假的时间
                            if (startTime<todaynoon ){
                                morning =true;
                            }
                            if (todaynoon<endTime){
                                afternoon = true;
                            }
                        }
                    }
                    //放入实习信息和实习计划id
                    for (SignInfoByStudent signInfo:signInfoByStudents) {
                        signInfo.setInternshipId(internshipInfo.getId()).setInternshipPlanId(planInfo.getId());
                        //设置请假免签
                        if (morning){
                            if (signInfo.getIsMorning().equals(1)){
                                signInfo.setSignStatus(SignInfoByStudent.NO_SIGN);
                            }
                        }
                        if (afternoon){
                            if (signInfo.getIsMorning().equals(2)){
                                signInfo.setSignStatus(SignInfoByStudent.NO_SIGN);
                            }
                        }
                    }
                    signList.addAll(signInfoByStudents);
                }
                signQueryWrapper.eq(SignInfoByStudent::getInternshipId,internshipInfo.getId());
            }
        }
        return signList;
    }


}
