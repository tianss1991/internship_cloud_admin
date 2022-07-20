package com.above.dao;

import com.above.dto.PayrollDto;
import com.above.po.Payroll;
import com.above.vo.PayrollVo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 工资单 Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface PayrollMapper extends BaseMapper<Payroll> {

    //辅导员查看学生工资单
    List<PayrollDto> getInstructorPayrollList(PayrollVo vo);
    //pc端查看工资单
    List<PayrollDto> getPayroll(PayrollVo vo);
    //工资单总数
    Integer countPayroll(PayrollVo vo);
    //学生查看工资单列表
    List<PayrollDto> getStudentPayrollList(PayrollVo vo);
    //辅导员查看学生工资单总数
    Integer getInstructorPayrollListCount(PayrollVo vo);
}
