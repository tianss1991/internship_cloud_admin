package com.above.dto;

import com.above.po.ClassInfo;
import com.above.po.DepartmentInfo;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 二级学院班级列表
 * @author BANGO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentWithClassDto extends DepartmentInfo {

    @ApiModelProperty(value = "班级列表")
    private List<ClassInfo> classInfoList;

}
