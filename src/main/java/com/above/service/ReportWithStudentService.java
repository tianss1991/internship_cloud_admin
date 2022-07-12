package com.above.service;

import com.above.dto.UserDto;
import com.above.po.ReportWithStudent;
import com.above.utils.CommonResult;
import com.above.vo.ReportWithStudentVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 学生报告表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface ReportWithStudentService extends IService<ReportWithStudent> {

    //新增日报
    CommonResult<Object> addDailyPaper(UserDto userDto, ReportWithStudentVo vo);
    //查看日报(学生端)
    CommonResult<Object> getDailyPaperStudent(UserDto userDto, ReportWithStudentVo vo);
    //撤回日报
    CommonResult<Object> revocationDailyPaper(UserDto userDto, ReportWithStudentVo vo);
    //修改/编辑日报
    CommonResult<Object> updateDailyPaper(UserDto userDto, ReportWithStudentVo vo);


}
