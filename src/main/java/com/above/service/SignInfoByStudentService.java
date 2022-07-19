package com.above.service;

import com.above.dto.UserDto;
import com.above.po.SignInfoByStudent;
import com.above.po.UserAccount;
import com.above.utils.CommonResult;
import com.above.vo.SignInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.text.ParseException;

/**
 * <p>
 * 签到记录表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface SignInfoByStudentService extends IService<SignInfoByStudent> {


    /**
     *  自动生成实习记录
     * @throws ParseException  时间转换异常抛出
     */
    void generateSignLogForStudent() throws ParseException;


    /**
     * 学生获取今天打卡列表
     * @param  userDto 当前用户
     * @param  signInfoVo 前端参数
     * @throws ParseException  时间转换异常抛出
     * @return 通用返回
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    CommonResult<Object> getTodaySignList(UserDto userDto, SignInfoVo signInfoVo) throws ParseException;

    /**
     * 学生获取本月打卡列表
     * @param  userDto 当前用户
     * @param  signInfoVo 前端参数
     * @throws ParseException  时间转换异常抛出
     * @return 通用返回
     * @Author: LZH
     * @Date: 2022/7/11 14:36
     */
    CommonResult<Object> getMonthSignList(UserDto userDto, SignInfoVo signInfoVo) throws ParseException;

    /**
     * 学生签到明细列表
     * @param  userDto 当前用户
     * @param  signInfoVo 前端参数
     * @return 通用返回
     * @Author: LZH
     * @Date: 2022/7/12 13:56
     */
    CommonResult<Object> getStudentSignDetailList(UserDto userDto, SignInfoVo signInfoVo);

    /**
     * 学生签到接口
     * @Author: LZH
     * @Date: 2022/7/12 13:58
     */
    CommonResult<Object> studentSignIn(UserDto userDto, SignInfoVo signInfoVo);

    /**
     * 获取签到和未签到学生列表
     * @Author: LZH
     * @Date: 2022/7/12 13:58
     */
    CommonResult<Object> getUnSignStudentList(UserDto userDto, SignInfoVo signInfoVo) throws ParseException;

    /**
     *  打卡自动异常
     */
    void automaticPunchChangeToException();
}
