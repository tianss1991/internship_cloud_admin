package com.above.service.impl;

import com.above.dao.*;
import com.above.dto.ReportWithStudentDto;
import com.above.dto.UserDto;
import com.above.po.*;
import com.above.service.ReportWithStudentService;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.ReportWithStudentVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生报告表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Service
public class ReportWithStudentServiceImpl extends ServiceImpl<ReportWithStudentMapper, ReportWithStudent> implements ReportWithStudentService {

    @Autowired
    private StudentInfoMapper studentInfoMapper;
    @Autowired
    private InternshipInfoByStudentMapper internshipInfoByStudentMapper;
    @Autowired
    private ReportWithStudentMapper reportWithStudentMapper;
    @Autowired
    private TeacherInfoMapper teacherInfoMapper;
    @Autowired
    private InternshipWithTeacherMapper internshipWithTeacherMapper;

    //新增日报/周报/月报/实习总结
    @Override
    public CommonResult<Object> addDailyPaper(UserDto userDto, ReportWithStudentVo vo) {
        //获取参数
        Integer createBy = userDto.getId();
        //日报类型 1-日报 2-周报 3-月报 4-实习总结
        Integer reportType = vo.getReportType();
        //标题
        String title = vo.getTitle();
        //内容
        String content = vo.getContent();
        //图片
        String imgUrl = vo.getImgUrl();
        //链接
        String url = vo.getUrl();
        //周次
        String week = vo.getWeek();
        //开始时间
        Date startTime = vo.getStartTime();
        //结束时间
        Date endTime = vo.getEndTime();
        //月份
        String month = vo.getMonth();
            //查询学生是否存在
            QueryWrapper<StudentInfo> qw = new QueryWrapper<>();
            qw.eq("user_id", createBy)
                    .eq("deleted", BaseVo.UNDELETE);
            StudentInfo studentInfo = studentInfoMapper.selectOne(qw);
            //获取学生id
            Integer studentId = studentInfo.getId();
            if (studentId == null || studentId==0) {
                return CommonResult.error(500, "缺少学生id");
            }
            vo.setStudentId(studentId);

            //获取关联实习id
            QueryWrapper<InternshipInfoByStudent> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("relation_student_id",studentId)
                    .eq("deleted",BaseVo.UNDELETE);
            InternshipInfoByStudent byStudent = internshipInfoByStudentMapper.selectOne(queryWrapper);
            Integer relationPlanId = byStudent.getRelationPlanId();

            if(reportType==null || reportType==0){
                return CommonResult.error(500,"缺少日报类型");
            }
            if (relationPlanId == null || relationPlanId==0) {
                return CommonResult.error(500, "缺少实习id");
            }
            if (StringUtils.isEmpty(title)) {
                return CommonResult.error(500, "请输入标题");
            }
            if (StringUtils.isEmpty(content)) {
                return CommonResult.error(500, "请输入日报内容");
            }
            if (StringUtils.isEmpty(imgUrl)) {
                return CommonResult.error(500, "图片不能为空");
            }
            if(StringUtils.isEmpty(url)){
                return CommonResult.error(500, "链接不能为空");
            }
             //创建学生报告表实体类
             ReportWithStudent rws = new ReportWithStudent();
             //关联实习id
             rws.setRelationPlanId(relationPlanId);
             //学生id
             rws.setStudentId(studentId);
             //日报状态
             rws.setStatus(1);
             //创建人
             rws.setCreateBy(createBy);
             //标题
             rws.setTitle(title);
             //内容
             rws.setContent(content);
             //图片
             rws.setImgUrl(imgUrl);
             //链接
             rws.setUrl(url);
             //删除状态
             rws.setDeleted(0);

            //日报
            if (reportType==1){
                int today = reportWithStudentMapper.today(vo);
                if (today>0){
                    return CommonResult.error(500,"今日日报已提交");
                }

                //报告类型
                rws.setReportType(1);

            }
        //周报
            if (reportType==2){

                if (StringUtils.isEmpty(week)) {
                    return CommonResult.error(500, "缺少实习周次");
                }
                if (startTime == null) {
                    return CommonResult.error(500, "缺少开始时间");
                }
                if (endTime == null) {
                    return CommonResult.error(500, "缺少结束时间");
                }
                int getweek = reportWithStudentMapper.getweek(vo);
                if (getweek>0){
                    return CommonResult.error(500,"本周周报已提交");
                }
                //报告类型
                rws.setReportType(2);
                //周次
                rws.setWeek(week);
                //开始时间
                rws.setStartTime(startTime);
                //结束时间
                rws.setEndTime(endTime);

            }
            //月报
            if (reportType==3){

                if (StringUtils.isEmpty(month)){
                    return CommonResult.error(500,"请选择月份");
                }
                //查询学生该月是否已提交月报
                QueryWrapper<ReportWithStudent> wrapper = new QueryWrapper<>();
                wrapper.eq("deleted",BaseVo.UNDELETE).eq("student_id",studentId).eq("month",month);
                ReportWithStudent reportWithStudent = reportWithStudentMapper.selectOne(wrapper);
                String month1 = reportWithStudent.getMonth();
                if (month.equals(month1)){
                    return CommonResult.error(500,"该月月报已提交");
                }
                //报告类型
                rws.setReportType(3);
                //月份
                rws.setMonth(month);

            }
            if (reportType==4){

                QueryWrapper<ReportWithStudent> wrapper = new QueryWrapper<>();
                wrapper.eq("student_id",studentId)
                        .eq("report_type",4)
                        .eq("deleted",BaseVo.UNDELETE);
                int count = this.count(wrapper);
                if (count>0){
                    return CommonResult.error(500,"实习总结已提交");
                }
                //报告类型
                rws.setReportType(4);

            }

            boolean save = this.save(rws);

        if (save) {
            Map<Object, Object> map = new HashMap<>();
            map.put("id",rws.getId());
            return CommonResult.success("新增成功", map);
        } else {
            return CommonResult.error(500, "新增失败");
        }
    }

    //查询学生日报/周报/月报/实习总结(学生端/教师端)
    @Override
    public CommonResult<Object> getDailyPaperList(UserDto userDto, ReportWithStudentVo vo) {
        String roleCode = userDto.getUserRoleDto().getRoleCode();
        Integer id = userDto.getId();
        String userName = userDto.getUserName();
        String userAvatar = userDto.getUserAvatar();
        Integer reportType = vo.getReportType();

        if (reportType==null || reportType==0){
            return CommonResult.error(500,"缺少该报状态");
        }
        //学生端
        if ("student".equals(roleCode)) {
            //获取学生id
            QueryWrapper<StudentInfo> qw = new QueryWrapper<>();
            qw.eq("user_id", id)
                    .eq("deleted", BaseVo.UNDELETE);
            StudentInfo studentInfo = studentInfoMapper.selectOne(qw);
            Integer studentId = studentInfo.getId();
            if (studentId == null ||studentId==0) {
                return CommonResult.error(500, "缺少学生id");
            }
            vo.setStudentId(studentId);
            HashMap<String, Object> map = new HashMap<>(16);

            List<ReportWithStudentDto> list = reportWithStudentMapper.getDailyPaperList(vo);
            Integer totalCount = reportWithStudentMapper.getDailyPaperListCount(vo);

            //总页数
            map.put(BaseVo.LIST, list);
            //总数
            map.put(BaseVo.TOTAL, totalCount);
            //返回数据
            map.put(BaseVo.PAGE,BaseVo.calculationPages(vo.getSize(),totalCount));
            return CommonResult.success(map);

            }
        //教师端
        if ("adviser".equals(roleCode)) {

            //获取教师id
            QueryWrapper<TeacherInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", id).eq("deleted", BaseVo.UNDELETE);
            TeacherInfo teacherInfo = teacherInfoMapper.selectOne(queryWrapper);
            Integer teacherId = teacherInfo.getId();
            if (teacherId == null || teacherId == 0) {
                return CommonResult.error(500, "缺少教师id");
            }
            vo.setTeacherId(teacherId);
            HashMap<String, Object> map = new HashMap<>(16);

            List<ReportWithStudentDto> list = reportWithStudentMapper.getWeeklyNewspaperTeacherList(vo);
            Integer totalCount = reportWithStudentMapper.getWeeklyNewspaperTeacherListCount(vo);
            //总页数
            map.put(BaseVo.LIST, list);
            //总数
            map.put(BaseVo.TOTAL, totalCount);
            //返回数据
            map.put(BaseVo.PAGE,BaseVo.calculationPages(vo.getSize(),totalCount));
                return CommonResult.success(map);

        }
        return CommonResult.error(500,"缺少用户类型");
    }

        //撤回
        @Override
        public CommonResult<Object> revocationDailyPaper (UserDto userDto, ReportWithStudentVo vo){
            //获取参数
            Integer updateBy = userDto.getId();
            Integer id = vo.getId();

            if (id==null ||id==0){
                return CommonResult.error(500,"缺少该报id");
            }

            QueryWrapper<ReportWithStudent> wrapper = new QueryWrapper<>();
            wrapper.eq("id",id);
            ReportWithStudent reportWithStudent = reportWithStudentMapper.selectById(id);
            reportWithStudent.setStatus(0);
            reportWithStudent.setUpdateBy(updateBy);

            boolean save = this.update(reportWithStudent,wrapper);

            if (save) {
                return CommonResult.success("撤回成功", null);
            } else {
                return CommonResult.error(500, "撤回失败");
            }
        }

        //修改/编辑(学生端)  驳回/批阅日报（教师端）
        @Override
        public CommonResult<Object> updateDailyPaper (UserDto userDto, ReportWithStudentVo vo){
                //报告类型 1-日报 2-周报 3-月报 4-实习总结
                Integer reportType = vo.getReportType();
                if (reportType==null || reportType==0){
                    return CommonResult.error(500,"缺少报告类型");
                }
                //日报状态 0-待提交 1-未阅 2-驳回 3-已批阅 4-已阅
                Integer status = vo.getStatus();
                if (status==null){
                    return CommonResult.error(500,"缺少日报状态");
                }

                Integer id = vo.getId();
                if (id==null ||id==0){
                return CommonResult.error(500,"缺少该报id");
                }
                String roleCode = userDto.getUserRoleDto().getRoleCode();
                Integer updateBy = userDto.getId();
                //标题
                String title = vo.getTitle();
                //内容
                String content = vo.getContent();
                //图片
                String imgUrl = vo.getImgUrl();
                //链接
                String url = vo.getUrl();
                //周次
                String week = vo.getWeek();
                //开始时间
                Date startTime = vo.getStartTime();
                //结束时间
                Date endTime = vo.getEndTime();
                //月份
                String month = vo.getMonth();

            //修改/编辑
            QueryWrapper<ReportWithStudent> qw = new QueryWrapper<>();
            qw.eq("id", id)
                    .eq("deleted", BaseVo.UNDELETE);
            ReportWithStudent rws = reportWithStudentMapper.selectOne(qw);
            //学生id
            Integer studentId = rws.getStudentId();
            //关联实习id
            Integer relationPlanId = rws.getRelationPlanId();
            rws.setUpdateBy(updateBy);


                  //学生端
                if ("student".equals(roleCode)){

                    if (!StringUtils.isEmpty(title)){
                        rws.setTitle(vo.getTitle());
                    }else {
                        return CommonResult.error(500,"标题不能为空");
                    }
                    if (!StringUtils.isEmpty(content)){
                        rws.setContent(vo.getContent());
                    }else {
                        return CommonResult.error(500,"内容不能为空");
                    }
                    if (!StringUtils.isEmpty(imgUrl)){
                        rws.setImgUrl(vo.getImgUrl());
                    }else {
                        return CommonResult.error(500,"图片不能为空");
                    }
                    if (!StringUtils.isEmpty(url)){
                        rws.setUrl(vo.getUrl());
                    }else {
                        return CommonResult.error(500,"链接不能为空");
                    }

                    //创建学生报告表实体类
                    ReportWithStudent rws1 = new ReportWithStudent();
                    //关联实习id
                    rws1.setRelationPlanId(relationPlanId);
                    //学生id
                    rws1.setStudentId(studentId);
                    //日报状态
                    rws1.setStatus(1);
                    //创建人
                    rws1.setCreateBy(updateBy);
                    //标题
                    rws1.setTitle(title);
                    //内容
                    rws1.setContent(content);
                    //图片
                    rws1.setImgUrl(imgUrl);
                    //链接
                    rws1.setUrl(url);
                    //删除状态
                    rws1.setDeleted(0);

                    //日报
                    if (reportType==1){
                        //编辑
                        if (status==0 ){
                            rws.setStatus(1);
                            boolean update = this.update(rws, qw);
                            if (update){
                                return CommonResult.success("编辑成功", null);
                            }else {
                                return CommonResult.success("编辑失败", null);
                            }
                        }

                        //修改
                        if (status==2){

                            //报告类型
                            rws1.setReportType(1);
                            boolean save = this.save(rws1);
                            if (save) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("id",rws1.getId());
                                return CommonResult.success("修改成功", map);
                            } else {
                                return CommonResult.error(500, "修改失败");
                            }
                        }

                    }
                    //周报
                    if (reportType==2){

                        if (!StringUtils.isEmpty(week)){
                            rws.setWeek(week);
                        }else {
                            return CommonResult.error(500,"请选择实习周次");
                        }
                        if (startTime!=null){
                            rws.setStartTime(startTime);
                        }else {
                            return CommonResult.error(500,"请选择开始时间");
                        }
                        if (endTime!=null){
                            rws.setEndTime(endTime);
                        }else {
                            return CommonResult.error(500, "请选择结束时间");
                        }
                        //编辑
                        if (status==0){
                            rws.setStatus(1);
                            boolean update = this.update(rws, qw);
                            if (update){
                                return CommonResult.success("编辑成功", null);
                            }else {
                                return CommonResult.success("编辑失败", null);
                            }
                           }
                        //修改
                        if (status==2){
                            //报告类型
                            rws1.setReportType(2);
                            //周次
                            rws1.setWeek(week);
                            //开始时间
                            rws1.setStartTime(startTime);
                            //结束时间
                            rws1.setEndTime(endTime);
                            boolean save = this.save(rws1);
                            if (save) {
                                return CommonResult.success("修改成功", null);
                            } else {
                                return CommonResult.error(500, "修改失败");
                            }
                        }

                        }
                    //月报
                    if (reportType==3){
                        if (!StringUtils.isEmpty(month)){
                            rws.setMonth(month);
                        }else {
                            return CommonResult.error(500,"请选择月份");
                        }
                        //编辑
                        if (status==0){
                            rws.setStatus(1);
                            boolean update = this.update(rws, qw);
                            if (update){
                                return CommonResult.success("编辑成功", null);
                            }else {
                                return CommonResult.success("编辑失败", null);
                            }
                        }
                        //修改
                        if (status==2){
                            //报告类型
                            rws1.setReportType(3);
                            //月份
                            rws1.setMonth(month);
                            boolean save = this.save(rws1);
                            if (save) {
                                return CommonResult.success("修改成功", null);
                            } else {
                                return CommonResult.error(500, "修改失败");
                            }
                        }
                    }
                         if (reportType==4){
                             //编辑
                             if (status==0 ){
                                 rws.setStatus(1);
                                 boolean update = this.update(rws, qw);
                                 if (update){
                                     return CommonResult.success("编辑成功", null);
                                 }else {
                                     return CommonResult.success("编辑失败", null);
                                 }
                             }

                             //修改
                             if (status==2){

                                 //报告类型
                                 rws1.setReportType(4);
                                 boolean save = this.save(rws1);
                                 if (save) {
                                     return CommonResult.success("修改成功", null);
                                 } else {
                                     return CommonResult.error(500, "修改失败");
                                 }
                             }
                         }
                }
                //教师端
                if ("adviser".equals(roleCode)){
                    //驳回理由/点评内容
                    String reason = vo.getReason();
                    //评分
                    Integer score = vo.getScore();

                    if (studentId == null || studentId == 0) {
                        return CommonResult.error(500, "缺少学生id");
                    }
                    //日报/周报/月报批阅
                    if (reportType==1 || reportType==2 || reportType==3 || reportType==4) {
                        //驳回
                        if (status == 2) {
                            rws.setStatus(2);
                            if (StringUtils.isEmpty(reason)) {
                                return CommonResult.error(500, "请输入驳回理由");
                            }
                            rws.setReason(reason);
                            rws.setUpdateBy(updateBy);
                            int update = reportWithStudentMapper.update(rws, qw);
                            return CommonResult.success(rws);
                        }

                        //已批阅
                        if (status == 3) {
                            rws.setStatus(3);
                            rws.setScore(score);
                            if (StringUtils.isEmpty(reason)) {
                                return CommonResult.error(500, "请输入点评");
                            }
                            rws.setReason(reason);
                            rws.setUpdateBy(updateBy);
                            int update = reportWithStudentMapper.update(rws, qw);
                            return CommonResult.success(rws);
                        }
                    }
                    return CommonResult.success("操作成功");
                }
              return CommonResult.error(500,"用户类型不匹配");
        }

        //日报/周报/月报详情(学生端/教师端)
        @Override
        public CommonResult<Object> getDailyPaperStudent (UserDto userDto, ReportWithStudentVo vo){
            //获取用户类型

            String roleCode = userDto.getUserRoleDto().getRoleCode();
            Integer reportType = vo.getReportType();
            Integer id = vo.getId();
            //获取学生id

            if (id==null || id==0){
                return CommonResult.error(500,"缺少该报id");
            }

            if (reportType==null || reportType==0){
                return CommonResult.error(500,"缺少报告类型");
            }

            //学生端
            if ("student".equals(roleCode)){
                Integer studentId = userDto.getStudentInfo().getId();
                if (studentId==null ||studentId==0){
                    return CommonResult.error(500,"学生id为空");
                }
                vo.setStudentId(studentId);
                //日报
                if (reportType==1){
                    ReportWithStudentDto dailyPaperStudent = reportWithStudentMapper.getDailyPaperStudent(vo);

                    return CommonResult.success(dailyPaperStudent);
                }
                //周报
                if (reportType==2){
                    ReportWithStudentDto weeklyNewspaperStudent = reportWithStudentMapper.getWeeklyNewspaperStudent(vo);
                    return CommonResult.success(weeklyNewspaperStudent);
                }
                //月报
                if (reportType==3){
                    ReportWithStudentDto monthlyMagazineStudent = reportWithStudentMapper.getMonthlyMagazineStudent(vo);
                    return CommonResult.success(monthlyMagazineStudent);
                }
                //实习总结
                if (reportType==4){
                    ReportWithStudentDto summarizeStudent = reportWithStudentMapper.getSummarizeStudent(vo);
                    return CommonResult.success(summarizeStudent);
                }

            }
            //教师端
            if ("adviser".equals(roleCode)){
                //获取教师id
                Integer teacherId = userDto.getTeacherInfo().getId();
                if (teacherId==null || teacherId==0){
                    return CommonResult.error(500,"缺少教师id");
                }
                vo.setTeacherId(teacherId);
                Integer glanceOver = vo.getGlanceOver();

                //日报
                if (reportType==1){
                    ReportWithStudentDto dailyPaperTeacher = reportWithStudentMapper.getDailyPaperTeacher(vo);

                    return  CommonResult.success(dailyPaperTeacher);
                }
                //周报
                if (reportType==2){
                    ReportWithStudentDto weeklyNewspaperTeacher = reportWithStudentMapper.getWeeklyNewspaperTeacher(vo);
                    return CommonResult.success(weeklyNewspaperTeacher);
                }
                //月报
                if (reportType==3){
                    ReportWithStudentDto monthlyMagazineTeacher = reportWithStudentMapper.getMonthlyMagazineTeacher(vo);
                    return CommonResult.success(monthlyMagazineTeacher);
                }
                //实习总结
                if (reportType==4){
                    ReportWithStudentDto summarizeTeacher = reportWithStudentMapper.getSummarizeTeacher(vo);
                    return CommonResult.success(summarizeTeacher);
                }

            }
              return CommonResult.error(500,"用户类型不匹配");
        }


    //统计日报(已写/未写)
        @Override
        public CommonResult<Object> getDailyPaperStatisticsList (UserDto userDto, ReportWithStudentVo vo){
            //获取参数
            Integer id = userDto.getId();
            Integer majorId = vo.getMajorId();
            String userName = vo.getUserName();

            //日报完成状态 1-未写 2-已写
            Integer write = vo.getWrite();
            if (write==null ||write==0){
                return CommonResult.error(500,"缺少日报完成状态");
            }
            //获取教师id
            QueryWrapper<TeacherInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", id).eq("deleted", BaseVo.UNDELETE);
            TeacherInfo teacherInfo = teacherInfoMapper.selectOne(queryWrapper);
            Integer teacherId = teacherInfo.getId();
            if (teacherId == null || teacherId == 0) {
                return CommonResult.error(500, "缺少教师id");
            }
            vo.setTeacherId(teacherId);
            //未写
            if (write==1){
                List<ReportWithStudentDto> dailyPaperStatisticsList = reportWithStudentMapper.getDailyPaperStatisticsList(vo);
                return CommonResult.success(dailyPaperStatisticsList);
            }
            //已写
            if (write==2){
                List<ReportWithStudentDto> statisticsList = reportWithStudentMapper.getStatisticsList(vo);
                return CommonResult.success(statisticsList);
            }
           return CommonResult.error(500,"未知错误");
        }

       //判断是否已阅(教师端)
       @Override
       public CommonResult<Object> getHaveRead(UserDto userDto, ReportWithStudentVo vo) {
           Integer updateBy = userDto.getId();
           Integer id = vo.getId();
           Integer glanceOver = vo.getGlanceOver();
           if (id==null || id==0){
               return CommonResult.error(500,"缺少该报id");
           }
           if (glanceOver==null || glanceOver==0){
               return CommonResult.error(500,"缺少浏览状态");
           }

           QueryWrapper<ReportWithStudent> queryWrapper = new QueryWrapper<>();
           queryWrapper.eq("id",id).eq("deleted",BaseVo.UNDELETE);
           ReportWithStudent reportWithStudent = reportWithStudentMapper.selectById(id);
           Integer status = reportWithStudent.getStatus();

           if (status==1){
               if (glanceOver==2){
               reportWithStudent.setStatus(4);
               boolean update = this.update(reportWithStudent, queryWrapper);
               if (update){
                   return CommonResult.success("有效浏览",null);
               }else {
                   return CommonResult.error(500,"无效浏览");
               }
             }
           }


           return CommonResult.error(500,"未知错误");
    }


}
