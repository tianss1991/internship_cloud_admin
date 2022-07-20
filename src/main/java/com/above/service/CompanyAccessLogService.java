package com.above.service;

import com.above.dto.UserDto;
import com.above.po.CompanyAccessLog;
import com.above.utils.CommonResult;
import com.above.vo.CompanyAccessVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 企业寻访记录表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-07-15
 */
public interface CompanyAccessLogService extends IService<CompanyAccessLog> {


    /**
     *  添加记录
     * @param userDto 操作用户
     * @param vo 前端参数
     * @return 通过返回
     */
    CommonResult<Object> addCompanyAccessLog(UserDto userDto, CompanyAccessVo vo);

    /**
     *  修改记录
     * @param userDto 操作用户
     * @param vo 前端参数
     * @return 通过返回
     */
    CommonResult<Object> editCompanyAccessLog(UserDto userDto, CompanyAccessVo vo);

    /**
     *  小程序获取自己的寻坊列表
     * @param userDto 操作用户
     * @param vo 前端参数
     * @return 通过返回
     */
    CommonResult<Object> getMyCompanyAccess(UserDto userDto, CompanyAccessVo vo);

    /**
     *  管理端获取全部的寻坊列表
     * @param userDto 操作用户
     * @param vo 前端参数
     * @return 通过返回
     */
    CommonResult<Object> getAllCompanyAccess(UserDto userDto, CompanyAccessVo vo);

    /**
     *  设置寻坊次数
     * @param userDto 操作用户
     * @param vo 前端参数
     * @return 通过返回
     */
    CommonResult<Object> setSize(UserDto userDto, CompanyAccessVo vo);

    /**
     *  获取寻坊次数列表
     * @param userDto 操作用户
     * @param vo 前端参数
     * @return 通过返回
     */
    CommonResult<Object> getSizeWithPlan(UserDto userDto, CompanyAccessVo vo);
}
