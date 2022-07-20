package com.above.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AllotInternshipPlanInfoDto {

    @ApiModelProperty(value = "教师id")
    private Integer teacherId;

    @ApiModelProperty(value = "教师名称")
    private String teacherName;

    @ApiModelProperty(value = "学生id字符串")
    private String studentIds;

    @ApiModelProperty(value = "学生名字字符串")
    private String studentNames;



}
