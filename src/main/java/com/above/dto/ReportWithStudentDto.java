package com.above.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ReportWithStudentDto {

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户头像图片地址")
    private String userAvatar;

    @ApiModelProperty(value = "编号")
    private Integer id;

    @ApiModelProperty(value = "学生id")
    private Integer studentId;

    @ApiModelProperty(value = "专业名称")
    private String majorName;

    @ApiModelProperty(value = "年级名称（比如2022级，四位年份数字+级）")
    private String gradeYear;

    @ApiModelProperty(value = "月份（只有月报有）")
    private String month;

    @ApiModelProperty(value = "关联实习计划id")
    private Integer relationPlanId;

    @ApiModelProperty(value = "学号")
    private String studentNumber;

    @ApiModelProperty(value = "日报状态 0-待提交 1-未阅 2-驳回 3-已批阅 4-已阅")
    private Integer status;

    @ApiModelProperty(value = "报告类型 1-日报 2-周报 3-月报 4-实习总结")
    private Integer reportType;

    @ApiModelProperty(value = "日报完成状态 1-未写 2-已写")
    private Integer write;

    @ApiModelProperty(value = "浏览状态 0-无效浏览 1-有效浏览")
    private Integer glanceOver;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "图片链接(JSON格式)")
    private String imgUrl;

    @ApiModelProperty(value = "链接")
    private String url;

    @ApiModelProperty(value = "周次（只有周报有）")
    private String week;

    @ApiModelProperty(value = "驳回理由/评价内容")
    private String reason;

    @ApiModelProperty(value = "评分（共10份，2分一颗星）")
    private Integer score;

    @JsonFormat(pattern = "yyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyy-MM-dd")
    @ApiModelProperty(value = "开始时间（只有周报有）")
    private Date startTime;

    @JsonFormat(pattern = "yyy-MM-dd")
    @ApiModelProperty(value = "结束时间（只有周报有）")
    private Date endTime;

}
