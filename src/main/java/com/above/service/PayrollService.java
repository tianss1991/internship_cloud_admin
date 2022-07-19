package com.above.service;

import com.above.dto.UserDto;
import com.above.po.Payroll;
import com.above.utils.CommonResult;
import com.above.vo.PayrollVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 工资单 服务类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface PayrollService extends IService<Payroll> {

    /**
     * @Description: 新增工资单
     * @Author: YJH
     * @Date: 2022/7/4 8:30
     */
    CommonResult<Object> addPayroll(UserDto userDto, PayrollVo vo);

    /**
     * @Description: 查询工资单列表-学生端
     * @Author: YJH
     * @Date: 2022/7/4 10:52
     */
    CommonResult<Object> getStudentPayrollList(UserDto userDto, PayrollVo vo);

    /**
     * @Description: 查询工资单列表-辅导员端
     * @Author: YJH
     * @Date: 2022/7/4 16:00
     */
    CommonResult<Object> getInstructorPayrollList(UserDto userDto, PayrollVo vo);

    /**
     * @Description: 查询工资单详情
     * @Author: YJH
     * @Date: 2022/7/6 14:20
     */
    CommonResult<Object> getPayrollDetail(UserDto userDto, PayrollVo vo);

    /**
     * @Description: pc端工资单管理
     * @Author: YJH
     * @Date:
     */
    CommonResult<Object> Payroll(UserDto userDto, PayrollVo vo);

}
