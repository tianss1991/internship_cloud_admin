package com.above.controller;


import com.above.service.SignInfoByStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 签到记录表 前端控制器
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@RestController
@RequestMapping("/signInfoByStudent")
public class SignInfoByStudentController {

    @Autowired
    private SignInfoByStudentService signInfoByStudentService;



}

