package com.above.po;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 学生报告表
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ReportWithStudent对象", description="学生报告表")
public class ReportWithStudent implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "学生id")
    @TableField("student_id")
    private Integer studentId;

    @ApiModelProperty(value = "关联实习id")
    @TableField("internship_id")
    private Integer internshipId;

    @ApiModelProperty(value = "关联实习计划id")
    @TableField("relation_plan_id")
    private Integer relationPlanId;

    @ApiModelProperty(value = "日报状态 0-待提交 1-未阅 2-驳回 3-已批阅 4-已阅")
    @TableField("`status`")
    private Integer status;

    @ApiModelProperty(value = "报告类型 1-日报 2-周报 3-月报 4-实习总结")
    @TableField("report_type")
    private Integer reportType;

    @ApiModelProperty(value = "周次（只有周报有）")
    @TableField("`week`")
    private String week;

    @ApiModelProperty(value = "月份（只有月报有）")
    @TableField("`month`")
    private String month;

    @DateTimeFormat(pattern = "yyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "开始时间（只有周报有）")
    @TableField("start_time")
    private Date startTime;

    @DateTimeFormat(pattern = "yyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "结束时间（只有周报有）")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "图片链接(JSON格式)")
    @TableField("img_url")
    private String imgUrl;

    @ApiModelProperty(value = "链接")
    @TableField("url")
    private String url;

    @ApiModelProperty(value = "评分（共10份，2分一颗星）")
    @TableField("score")
    private Integer score;

    @ApiModelProperty(value = "驳回理由/评价内容")
    @TableField("reason")
    private String reason;

    @ApiModelProperty(value = "浏览状态 0-无效浏览 1-有效浏览")
    @TableField("glance_over")
    private Integer glanceOver;

    @ApiModelProperty(value = "日报完成状态 1-未写 2-已写")
    @TableField("`write`")
    private Integer write;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private Integer createBy;

    @DateTimeFormat(pattern = "yyy-MM-dd HH:mm")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private Integer updateBy;

    @DateTimeFormat(pattern = "yyy-MM-dd HH:mm")
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "逻辑删除 0-未删除 1-已删除")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

}
