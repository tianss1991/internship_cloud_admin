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

    @ApiModelProperty(value = "日报状态 0-待提交 1-未阅 2-驳回 3-已批阅")
    private Integer status;

    @ApiModelProperty(value = "报告类型 1-日报 2-周报 3-月报 4-实习总结")
    private Integer reportType;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "驳回理由")
    private String reason;

    @ApiModelProperty(value = "评分（共10份，2分一颗星）")
    private Integer score;

    @JsonFormat(pattern = "yyy-MM-dd HH:mm")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
