package com.above.service;

import com.above.po.SignInfoByStudent;
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




}
