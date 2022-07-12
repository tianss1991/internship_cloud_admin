package com.above.po;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
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

/**
 * <p>
 * 学生实习信息表
 * </p>
 *
 * @author mp
 * @since 2022-06-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="InternshipInfoByStudent对象", description="学生实习信息表")
public class InternshipInfoByStudent implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "关联实习计划id")
    @TableField("relation_plan_id")
    private Integer relationPlanId;

    @ApiModelProperty(value = "关联学生id")
    @TableField("relation_student_id")
    private Integer relationStudentId;

    @ApiModelProperty(value = "申请类型 1-普通申请 2-免实习申请 3-就业上报 4-实习总评填写信息")
    @TableField("internship_type")
    private Integer internshipType;

    @ApiModelProperty(value = "状态，0-草稿 1-审核中 2-审核失败 3-审核通过")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "审核状态 1-第一次审核 2-岗位审核 3-企业审核")
    @TableField("check_status")
    private Integer checkStatus;

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

    @ApiModelProperty(value = "企业所在区域编码 1-未知分类")
    @TableField("company_code")
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

    @ApiModelProperty(value = "开始时间")
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

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

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private Integer createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private Integer updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time",fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "逻辑删除 0-未删除 1-已删除")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;



}
