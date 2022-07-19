package com.above.timetask;


import com.above.service.SignInfoByStudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.ParseException;

/**
 * @author BANGO
 */
@Slf4j
@EnableScheduling
@Configuration
public class SignInfoTimeTask {

    @Autowired
    private SignInfoByStudentService signInfoByStudentService;

    /**
     * 自动生成每日打卡记录
     *   【测试自动执行 5s 一次】
     *  【cron = "5/10 * * * * ?"】
     */
//    @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(cron = "5/10 * * * * ?")
    private void generateSignLogForStudent(){
        try {
            signInfoByStudentService.generateSignLogForStudent();
        } catch (ParseException e) {
            log.error("时间转换错误");
        }
    }

    /**
     * @Description: 自动设置未打卡记录为异常 每天凌晨1点执行
     * @Author: LZH
     * @Date: 2022/7/18 10:33
     */
//    @Scheduled(cron = "0 0 1 * * ?")
    @Scheduled(cron = "5/10 * * * * ?")
    private void automaticPunchChangeToException(){
        signInfoByStudentService.automaticPunchChangeToException();
    }
}
