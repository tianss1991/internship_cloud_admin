package com.above.vo;

import com.above.po.InternshipInfoByStudent;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InternshipApplicationVo{

    @ApiModelProperty(value = "实习id")
    private Integer internshipId;

    /**0-失败 1-成功*/
    @ApiModelProperty(value = "审核状态")
    private Integer internshipStatus;

    /**1-第一次审核 2-岗位审核 3-企业审核*/
    @ApiModelProperty(value = "修改类别")
    private Integer checkStatus;


    /**0-未审核 1-已审核*/
    @ApiModelProperty(value = "是否已审核")
    private Integer isCheck;

    @ApiModelProperty(value = "关联实习计划id")
    @TableField("relation_plan_id")
    private Integer relationPlanId;

    @ApiModelProperty(value = "关联学生id")
    @TableField("relation_student_id")
    private Integer relationStudentId;

    @ApiModelProperty(value = "申请类型 1-普通申请 2-免实习申请 3-就业上报 4-实习总评填写信息")
    @TableField("internship_type")
    private Integer internshipType;

    @ApiModelProperty(value = "申请原因（仅免实习申请有）")
    @TableField("reason")
    private String reason;

    @ApiModelProperty(value = "去向 1-考研 2- （仅免实习申请有）")
    @TableField("dispositon")
    private Integer dispositon;

    @ApiModelProperty(value = "公司名称")
    @TableField("company_name")
    private String companyName;

    @ApiModelProperty(value = "社会信用代码")
    @TableField("social_code")
    private String socialCode;

    @ApiModelProperty(value = "人事负责人")
    @TableField("principal")
    private String principal;

    @ApiModelProperty(value = "企业规模 1-未知分类")
    @TableField("company_scale")
    private Integer companyScale;

    @ApiModelProperty(value = "联系电话")
    @TableField("company_telephone")
    private String companyTelephone;

    @ApiModelProperty(value = "公司邮箱")
    @TableField("company_mail")
    private String companyMail;

    @ApiModelProperty(value = "公司邮编")
    @TableField("company_post")
    private String companyPost;

    @ApiModelProperty(value = "公司性质 1-未知分类")
    @TableField("company_nature")
    private Integer companyNature;

    @ApiModelProperty(value = "所属行业 1-未知分类")
    @TableField("company_industry")
    private Integer companyIndustry;

    @ApiModelProperty(value = "企业所在区域字符串 1-未知分类")
    @TableField("company_area")
    private String companyArea;

    @ApiModelProperty(value = "企业所在区域编码")
    @TableField("company_Code")
    private Integer companyCode;

    @ApiModelProperty(value = "公司地址")
    @TableField("company_address")
    private String companyAddress;

    @ApiModelProperty(value = "岗位名称")
    @TableField("job_name")
    private String jobName;

    @ApiModelProperty(value = "部门名称")
    @TableField("job_department")
    private String jobDepartment;

    @ApiModelProperty(value = "岗位内容")
    @TableField("job_content")
    private String jobContent;

    @ApiModelProperty(value = "就业类别")
    @TableField("job_category")
    private Integer jobCategory;

    @ApiModelProperty(value = "老师id")
    @TableField("teacher_id")
    private Integer teacherId;

    @ApiModelProperty(value = "学生id")
    @TableField("student_id")
    private Integer studentId;

    @ApiModelProperty(value = "企业老师")
    @TableField("company_teacher")
    private String companyTeacher;

    @ApiModelProperty(value = "企业老师电话")
    @TableField("company_teacher_telephone")
    private String companyTeacherTelephone;

    @ApiModelProperty(value = "岗位类别 1-未知分类")
    @TableField("job_type")
    private Integer jobType;

    @ApiModelProperty(value = "岗位简介")
    @TableField("job_brief_info")
    private String jobBriefInfo;

    @ApiModelProperty(value = "岗位所在区域字符串")
    @TableField("job_area")
    private String jobArea;

    @ApiModelProperty(value = "岗位所在区域编码")
    @TableField("job_code")
    private Integer jobCode;

    @ApiModelProperty(value = "岗位地址")
    @TableField("job_address")
    private String jobAddress;

    @ApiModelProperty(value = "实习方式 1-顶岗实习 2-")
    @TableField("approach")
    private Integer approach;

    @ApiModelProperty(value = "专业是否对口 0-否 1-是")
    @TableField("is_aboral")
    private Integer isAboral;

    @ApiModelProperty(value = "实习薪资")
    @TableField("salary")
    private String salary;

    @ApiModelProperty(value = "是否有特殊专业情况 0-否 1-是")
    @TableField("is_special")
    private Integer isSpecial;

    @ApiModelProperty(value = "额外信息 json字符串")
    @TableField("extra")
    private String extra;

    @ApiModelProperty(value = "文件字段(多个文件存JSON格式)")
    @TableField("file_url")
    private String fileUrl;

    @ApiModelProperty(value = "协议书编号（只有就业上报类型才有）")
    @TableField("agreement_code")
    private String agreementCode;

    @ApiModelProperty(value = "失败理由")
    @TableField("fail_reason")
    private String failReason;

    @ApiModelProperty(value = "实习计划id")
    private Integer planId;


    @ApiModelProperty(value = "通用筛选开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "通用筛选结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "页数")
    private Integer page = 1;

    @ApiModelProperty(value = "搜索关键词")
    private String key;

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
