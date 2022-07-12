package com.above.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 班级列表包含院校信息
 * @Author: LZH
 * @Date: 2022/4/02 10:08
 */
@Data
public class ClassListWithOther {
    @ApiModelProperty(value = "学校编号")
    @TableField("school_id")
    private Integer schoolId;

    @ApiModelProperty(value = "学校名称")
    @TableField("school_name")
    private String schoolName;

    @ApiModelProperty(value = "二级学院编号")
    @TableField("department_id")
    private Integer departmentId;

    @ApiModelProperty(value = "二级学院名称")
    @TableField("department_name")
    private String departmentName;

    @ApiModelProperty(value = "班级编号")
    @TableField("class_id")
    private Integer classId;

    @ApiModelProperty(value = "班级名称")
    @TableField("class_name")
    private String className;
}
