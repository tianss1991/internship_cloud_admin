package com.above.service.impl;

import com.above.po.InternshipInfoByStudent;
import com.above.po.InternshipPlanInfo;
import com.above.po.SignInfoByStudent;
import com.above.dao.SignInfoByStudentMapper;
import com.above.po.StudentInfo;
import com.above.service.InternshipInfoByStudentService;
import com.above.service.InternshipPlanInfoService;
import com.above.service.SignInfoByStudentService;
import com.above.service.StudentInfoService;
import com.above.vo.BaseVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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


    @Autowired
    private InternshipPlanInfoService planInfoService;
    @Autowired
    private StudentInfoService studentInfoService;
    @Autowired
    private InternshipInfoByStudentService infoByStudentService;

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
                        .le(InternshipPlanInfo::getStartTime,now).eq(InternshipPlanInfo::getEndTime,now);
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
                    studentInfoList.forEach( info -> info.setPlanId(planInfo.getId()));
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
     *  对某个学生生成 date日期的 4条记录
     * @param studentId 学生id
     * @param date 日期 yyyy-MM-dd
     * @return 返回记录list
     */
    private List<SignInfoByStudent> generateSignLog(Integer studentId,Date date){
        List<SignInfoByStudent> signInfoByStudents = new ArrayList<>();

        //对该学生生成四条未打卡记录
        SignInfoByStudent signInfoByStudent = new SignInfoByStudent();
        signInfoByStudent.setStudentId(studentId).setSignDate(date).setIsSign(0);

        //上午上班
        signInfoByStudent.setSignType(1).setIsMorning(1);
        signInfoByStudents.add(signInfoByStudent);
        //上午下班
        signInfoByStudent.setSignType(2).setIsMorning(1);
        signInfoByStudents.add(signInfoByStudent);
        //下午上班
        signInfoByStudent.setSignType(1).setIsMorning(2);
        signInfoByStudents.add(signInfoByStudent);
        //下午下班
        signInfoByStudent.setSignType(2).setIsMorning(2);
        signInfoByStudents.add(signInfoByStudent);

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

        //遍历当前学生
        for (StudentInfo studentInfo : allStudentList) {

            List<SignInfoByStudent> signInfoByStudents = generateSignLog(studentInfo.getId(), date);

            //核对该学生是否生成过
            LambdaQueryWrapper<SignInfoByStudent> signQueryWrapper = new LambdaQueryWrapper<>();
            signQueryWrapper.eq(SignInfoByStudent::getSignDate,date).eq(SignInfoByStudent::getStudentId,studentInfo.getId());

            //查找当前学生已通过的实习信息
            LambdaQueryWrapper<InternshipInfoByStudent> internshipInfoByStudentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            internshipInfoByStudentLambdaQueryWrapper.eq(InternshipInfoByStudent::getRelationStudentId,studentInfo.getId())
                    .eq(InternshipInfoByStudent::getRelationPlanId,studentInfo.getPlanId())
                    .in(InternshipInfoByStudent::getInternshipType,1,2)
                    .eq(InternshipInfoByStudent::getStatus,3)
                    .eq(InternshipInfoByStudent::getDeleted,BaseVo.UNDELETE);
            List<InternshipInfoByStudent> list = infoByStudentService.list(internshipInfoByStudentLambdaQueryWrapper);

            boolean hadInternship = true;
            //如果为空则生成一条默认记录
            if (list == null || list.size() == 0){
                hadInternship = false;
            }
            //判断是否存在记录
            if (!hadInternship){
                signQueryWrapper.isNull(SignInfoByStudent::getInternshipId);
                //若数量为0则生成记录
                int count = super.count(signQueryWrapper);
                if (count == 0){
                    signList.addAll(signInfoByStudents);
                }

            }else {
                InternshipInfoByStudent internshipInfo = list.get(0);
                //若数量为0则生成记录
                int count = super.count(signQueryWrapper);
                if (count == 0){
                    signInfoByStudents.forEach(info -> info.setInternshipId(internshipInfo.getId()));
                    signList.addAll(signInfoByStudents);
                }
                signQueryWrapper.eq(SignInfoByStudent::getInternshipId,internshipInfo.getId());
            }
        }
        return signList;
    }


}
