package com.above.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author PC2
 */
@Data
public class ClassReviewDto {

    @ApiModelProperty(value = "课程编号")
    private Integer publicClassId;

    @ApiModelProperty(value = "课程名称")
    private String publicTitle;

    @ApiModelProperty(value = "课程创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date classCreateDatetime;

    @ApiModelProperty(value = "上课老师名称")
    private String userName;

    @ApiModelProperty(value = "教师id")
    private String teacherId;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "上课老师头像")
    private String userAvatar;

    @ApiModelProperty(value = "评价老师名称")
    private String evaluateUserName;

    @ApiModelProperty(value = "评价老师头像")
    private String evaluateUserAvatar;

    @ApiModelProperty(value = "课程类型")
    private Integer classType;

    @ApiModelProperty(value = "任务状态")
    private Integer taskStatus;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value = "create_datetime", fill = FieldFill.INSERT)
    private Date evaluateCreateDatetime;

    @ApiModelProperty(value = "评价id")
    @TableField("task_with_class_evaluate_id")
    private Integer taskWithClassEvaluateId;

    @ApiModelProperty(value = "评价得分")
    @TableField("score")
    private Integer score;

    @ApiModelProperty(value = "学校id")
    @TableField("school_id")
    private Integer schoolId;

    @ApiModelProperty(value = "二级学院id")
    @TableField("department_id")
    private Integer departmentId;

    @ApiModelProperty(value = "学期id")
    @TableField("term_id")
    private Integer termId;

    @ApiModelProperty(value = "评价状态")
    private Integer evaluateStatus ;

    @ApiModelProperty(value = "是否需要审核")
    private Integer verifyStatus;

    @ApiModelProperty(value = "用户类型")
    private Integer userType;
}
