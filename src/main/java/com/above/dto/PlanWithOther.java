package com.above.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author BANGO
 */
@Data
@Accessors(chain = true)
public class PlanWithOther {

    @ApiModelProperty(value = "实习计划编号")
    private Integer id;

    @ApiModelProperty(value = "实习计划编号")
    private Integer planTitle;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss",timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss",timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "辅导员寻访次数")
    private Integer instructorSize;

    @ApiModelProperty(value = "指导老师寻访次数")
    private Integer adviserSize;

    @ApiModelProperty(value = "学校名称")
    private String schoolName;

    @ApiModelProperty(value = "年级名称")
    private String gradeYear;

    @ApiModelProperty(value = "二级学院名称")
    private String departmentName;

    @ApiModelProperty(value = "专业名称")
    private String majorName;

    @ApiModelProperty(value = "班级名称")
    private String className;

    @ApiModelProperty(value = "成绩评定")
    private String gradePerformance;

}
