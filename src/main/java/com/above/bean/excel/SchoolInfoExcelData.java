package com.above.bean.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @Description: excel字段
 * @Author: LZH
 * @Date: 2022/1/17 9:59
 */
@Data
public class SchoolInfoExcelData {

    @ExcelProperty(value = "学校名称",index = 0)
    private String schoolName;

    @ExcelProperty(value = "学校领导",index = 1)
    private String schoolLeader;

    @ExcelProperty(value = "领导工号",index = 2)
    private String schoolLeaderNum;

    @ExcelProperty(value = "二级学院名称",index = 3)
    private String departmentName;

    @ExcelProperty(value = "二级学院管理员",index = 4)
    private String departmentLeader;

    @ExcelProperty(value = "二级学院管理员工号",index = 5)
    private String departmentLeaderNum;

    @ExcelProperty(value = "专业名称",index = 6)
    private String majorName;

    @ExcelProperty(value = "年级名称",index = 7)
    private String gradeName;

    @ExcelProperty(value = "班级名称",index = 8)
    private String className;

    @ExcelProperty(value = "辅导员",index = 9)
    private String classLeader;

    @ExcelProperty(value = "辅导员工号",index = 10)
    private String classLeaderNum;

    @ExcelProperty(value = "班主任",index = 11)
    private String classTeacher;




}
