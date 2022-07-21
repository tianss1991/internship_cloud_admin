package com.above.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InternshipPlanInfoDto {

    private Integer id;

    @ApiModelProperty(value = "实习计划名称")
    private String planTitle;

    @ApiModelProperty(value = "年级id")
    private Integer gradeId;

    @ApiModelProperty(value = "层次")
    private String gradation;

    @ApiModelProperty(value = "年级名称")
    private String gradeYear;

    @ApiModelProperty(value = "专业id")
    private Integer majorId;

    @ApiModelProperty(value = "学校id")
    private Integer schoolId;

    @ApiModelProperty(value = "二级学院id")
    private Integer departmentId;

    @ApiModelProperty(value = "专业名称")
    private String majorName;

    @ApiModelProperty(value = "学校名称")
    private String schoolName;

    @ApiModelProperty(value = "系部名称")
    private String departmentName;

    @ApiModelProperty(value = "实习计划开始时间")
    private Date startTime;

    @ApiModelProperty(value = "实习计划结束时间")
    private Date endTime;

    @ApiModelProperty(value = "实习计划指导老师")
    private String teacherNames;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "实习目的")
    private String purpose;

    @ApiModelProperty(value = "实习要求")
    private String required;

    @ApiModelProperty(value = "实习内容")
    private String content;

    @ApiModelProperty(value = "签到数量设置（若没填则按天数去掉周末生成）")
    private Integer signSet;

    @ApiModelProperty(value = "日报数量要求")
    private Integer dailyCount;

    @ApiModelProperty(value = "日报字数要求")
    private Integer dailyWordCount;

    @ApiModelProperty(value = "周报数量要求")
    private Integer weekCount;

    @ApiModelProperty(value = "周报字数要求")
    private Integer weekWordCount;

    @ApiModelProperty(value = "月报数量要求")
    private Integer monthCount;

    @ApiModelProperty(value = "月报字数要求")
    private Integer monthWordCount;

    @ApiModelProperty(value = "总结数量要求")
    private Integer summarizeCount;

    @ApiModelProperty(value = "总结字数要求")
    private Integer summarizeWordCount;

    @ApiModelProperty(value = "打卡次数")
    private Integer signTimes;

    @ApiModelProperty(value = "教师id")
    private Integer teacherId;

    @ApiModelProperty(value = "教师名称")
    private String teacherName;

    @ApiModelProperty(value = "实习计划评分占比")
    private List<InternshipScoreDto> InternshipScore;



}
