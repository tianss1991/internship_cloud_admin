package com.above.bean.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentInfoExcelData {

    @ExcelProperty(value = "系部",index = 0)
    private String department;

    @ExcelProperty(value = "年级",index = 1)
    private String grade;

    @ExcelProperty(value = "专业",index = 2)
    private String major;

    @ExcelProperty(value = "班级",index = 3)
    private String className;

    @ExcelProperty(value = "学号",index = 4)
    private String studentNumber;

    @ExcelProperty(value = "姓名",index = 5)
    private String studentName;

    @ExcelProperty(value = "性别",index = 6)
    private String gender;

    @ExcelProperty(value = "手机号",index = 7)
    private String telephone;

    @ExcelProperty(value = "邮箱",index = 8)
    private String mail;

}
