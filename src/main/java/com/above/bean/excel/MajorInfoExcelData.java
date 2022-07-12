package com.above.bean.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class MajorInfoExcelData {
    @ExcelProperty(value = "学校名称",index = 0)
    private String schoolName;
    @ExcelProperty(value = "课程名称",index = 1)
    private String majorName;
}
