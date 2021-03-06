package com.above.dao;

import com.above.dto.ReportWithStudentDto;
import com.above.po.ReportWithStudent;
import com.above.vo.ReportWithStudentVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 学生报告表 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface ReportWithStudentMapper extends BaseMapper<ReportWithStudent> {
    //查看日报详情（学生端）
    ReportWithStudentDto getDailyPaperStudent(ReportWithStudentVo vo);
    //统计日报-未写(教师端)
    List<ReportWithStudentDto>  getDailyPaperStatisticsList(ReportWithStudentVo vo);
    //统计日报-已写(教师端)
    List<ReportWithStudentDto> getStatisticsList(ReportWithStudentVo vo);
    //查询日/周/月报/实习总结(教师端)
    List<ReportWithStudentDto> getWeeklyNewspaperTeacherList(ReportWithStudentVo vo);
    //查询日报详情(教师端)
    ReportWithStudentDto getDailyPaperTeacher(ReportWithStudentVo vo);
    //查询周报详情(学生端)
     ReportWithStudentDto getWeeklyNewspaperStudent(ReportWithStudentVo vo);
    //查询月报详情(学生端)
    ReportWithStudentDto getMonthlyMagazineStudent(ReportWithStudentVo vo);
    //查询周报详情(教师端)
    ReportWithStudentDto  getWeeklyNewspaperTeacher(ReportWithStudentVo vo);
    //查询月报详情(教师端)
    ReportWithStudentDto getMonthlyMagazineTeacher(ReportWithStudentVo vo);
    //查询日/周/月报/实习总结(学生端)
    List<ReportWithStudentDto> getDailyPaperList(ReportWithStudentVo vo);
    //查询实习总结详情(学生端)
    ReportWithStudentDto getSummarizeStudent(ReportWithStudentVo vo);
    //查询实习总结详情(教师端)
    ReportWithStudentDto getSummarizeTeacher(ReportWithStudentVo vo);
    //查询当天日报添加的数量
    Integer today(ReportWithStudentVo vo);
    //查询指定时间内的周报数量
    Integer getweek(ReportWithStudentVo vo);
    //查询日/周/月报/实习总结总数(学生端)
    Integer getDailyPaperListCount(ReportWithStudentVo vo);
    //查询日/周/月报/实习总结总数(教师端)
    Integer getWeeklyNewspaperTeacherListCount(ReportWithStudentVo vo);
    //统计日报-未写总数
    Integer getgetDailyPaperStatisticsListCount(ReportWithStudentVo vo);
    //统计日报-已写总数
    Integer getStatisticsListCount(ReportWithStudentVo vo);
}
