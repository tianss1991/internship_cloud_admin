package com.above.controller;

import com.alibaba.excel.util.IoUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * @Description: 文件上传接口
 * @Author: LZH
 * @Date: 2021/10/29 10:10
 */
@RestController
@RequestMapping("/excelTemplate")
@Api(tags = "文件接口")
public class ExcelTemplateController {

    /**
     * @Description: 获取教职工导入模板文件
     * @Author: LZH
     * @Date: 2022/2/23 17:35
     */
    @ApiOperation("获取教职工导入模板文件")
    @GetMapping("/downloadTeacherTemplate")
    public void downloadTeacherTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("教职工导入模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        InputStream inputStream = this.getClass().getResourceAsStream("/template/教职工导入模板.xlsx");
        byte[] byteArray = IoUtils.toByteArray(inputStream);
        inputStream.close();
        response.getOutputStream().write(byteArray);
    }
    /**
     * @Description: 获取院校信息导入模板文件
     * @Author: LZH
     * @Date: 2022/2/23 17:35
     */
    @ApiOperation("获取院校信息导入模板文件")
    @GetMapping("/downloadSchoolTemplate")
    public void downloadSchoolTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("院校信息导入模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        InputStream inputStream = this.getClass().getResourceAsStream("/template/院校信息导入模板.xlsx");
        byte[] byteArray = IoUtils.toByteArray(inputStream);
        inputStream.close();
        response.getOutputStream().write(byteArray);
    }

    /**
     * @Description: 获取学生信息导入模板文件
     * @Author: LZH
     * @Date: 2022/4/20 10:35
     */
    @ApiOperation("获取学生信息导入模板文件")
    @GetMapping("/downloadStudentTemplate")
    public void downloadTermTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("学生信息导入模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        InputStream inputStream = this.getClass().getResourceAsStream("/template/学生导入模板.xlsx");
        byte[] byteArray = IoUtils.toByteArray(inputStream);
        inputStream.close();
        response.getOutputStream().write(byteArray);
    }

}
