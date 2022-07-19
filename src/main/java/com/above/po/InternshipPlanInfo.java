package com.above.po;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 实习计划表
 * </p>
 *
 * @author mp
 * @since 2022-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="InternshipPlanInfo对象", description="实习计划表")
public class InternshipPlanInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "学校编号;关联学校id")
    @TableField("school_id")
    private Integer schoolId;

    @ApiModelProperty(value = "二级学院编号;关联二级学院id")
    @TableField("department_id")
    private Integer departmentId;

    @ApiModelProperty(value = "年级id")
    @TableField("grade_id")
    private Integer gradeId;

    @ApiModelProperty(value = "专业id")
    @TableField("major_id")
    private Integer majorId;

    @ApiModelProperty(value = "计划名称")
    @TableField("plan_title")
    private String planTitle;

    @ApiModelProperty(value = "层次(存字符串)")
    @TableField("gradation")
    private String gradation;

    @ApiModelProperty(value = "开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "实习目的")
    @TableField("purpose")
    private String purpose;

    @ApiModelProperty(value = "实习要求")
    @TableField("required")
    private String required;

    @ApiModelProperty(value = "实习内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "签到数量设置（若没填则按天数去掉周末生成）")
    @TableField("sign_set")
    private Integer signSet;

    @ApiModelProperty(value = "日报数量要求")
    @TableField("daily_count")
    private Integer dailyCount;

    @ApiModelProperty(value = "日报字数要求")
    @TableField("daily_word_count")
    private Integer dailyWordCount;

    @ApiModelProperty(value = "周报数量要求")
    @TableField("week_count")
    private Integer weekCount;

    @ApiModelProperty(value = "周报字数要求")
    @TableField("week_word_count")
    private Integer weekWordCount;

    @ApiModelProperty(value = "月报数量要求")
    @TableField("month_count")
    private Integer monthCount;

    @ApiModelProperty(value = "月报字数要求")
    @TableField("month_word_count")
    private Integer monthWordCount;

    @ApiModelProperty(value = "总结数量要求")
    @TableField("summarize_count")
    private Integer summarizeCount;

    @ApiModelProperty(value = "总结字数要求")
    @TableField("summarize_word_count")
    private Integer summarizeWordCount;

    @ApiModelProperty(value = "企业评分占比")
    @TableField("company_score_rate")
    private Integer companyScoreRate;

    @ApiModelProperty(value = "教师评分占比")
    @TableField("teacher_score_rate")
    private Integer teacherScoreRate;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private Integer createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private Integer updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "逻辑删除 0-未删除 1-已删除")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

    @ApiModelProperty(value = "打卡次数（2/4）默认2次")
    @TableField("sign_times")
    private Integer signTimes;

    @ApiModelProperty(value = "状态，0-草稿 1-审核中 2-审核失败 3-审核通过")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "失败理由")
    @TableField("fail_reason")
    private String failReason;

    @ApiModelProperty(value = "辅导员寻访次数")
    @TableField("instructor_size")
    private Integer instructorSize;

    @ApiModelProperty(value = "指导老师寻访次数")
    @TableField("adviser_size")
    private Integer adviserSize;


}
