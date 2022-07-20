package com.above.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InternshipPlanInfoVo  {

    @ApiModelProperty(value = "编号")
    @TableField("id")
    private Integer id;

    @ApiModelProperty(value = "教师id")
    private Integer teacherId;

    @ApiModelProperty(value = "学生集合")
    private List<Integer> studentList;

    @ApiModelProperty(value = "旧的教师id")
    private Integer oldTeacherId;

    @ApiModelProperty(value = "旧的学生集合")
    private List<Integer> oldStudentList;

    @ApiModelProperty(value = "状态 0-失败(未分配) 1-通过(已分配)")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "实习计划id")
    private Integer planId;

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

    @ApiModelProperty(value = "层次")
    @TableField("gradation")
    private String gradation;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty(value = "开始时间")
    @TableField("start_time")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
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

    @ApiModelProperty(value = "打卡次数")
    @TableField("sign_times")
    private Integer signTimes;

    @ApiModelProperty(value = "失败理由")
    @TableField("fail_reason")
    private String failReason;

    @ApiModelProperty(value = "搜索词")
    private String key;

    @ApiModelProperty(value = "图片是否必传")
    private Integer isMustImage;

    @ApiModelProperty(value = "页数")
    private Integer page = 1;

    @ApiModelProperty(value = "每页个数")
    private Integer size = 10;

    private Integer start;

    public Integer getStart() {
        if (page != null && size != null && page != 0) {
            start = (this.page - 1) * size;
        }
        return start;
    }


    public void setStart(Integer start) {
        this.start = start;
    }

    public static Integer calculationPages(Integer size, Integer totalCount) {
        return (totalCount + size - 1) / size;
    }

}
