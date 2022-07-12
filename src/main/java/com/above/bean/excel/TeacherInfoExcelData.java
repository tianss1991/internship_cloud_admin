package com.above.bean.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @Description: 教职工excel导入
 * @Author: LZH
 * @Date: 2022/1/13 16:46
 */
@Data
public class TeacherInfoExcelData {

    @ExcelProperty(value = "学校名称",index = 0)
    private String schoolName;

    @ExcelProperty(value = "部门名称",index = 1)
    private String department;

    @ExcelProperty(value = "姓名",index = 2)
    private String name;

    @ExcelProperty(value = "性别",index = 3)
    private String gender;

    @ExcelProperty(value = "工号",index = 4)
    private String workNumber;

    @ExcelProperty(value = "移动电话",index = 5)
    private String phone;

    @ExcelProperty(value = "邮箱",index = 6)
    private String mail;

}
