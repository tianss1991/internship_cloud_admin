package com.above.dto;

import com.above.po.ClassInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 审核岗位列表信息
 * @Author: GG
 * @Date: 2022/6/24 16:58
 */
import java.util.Date;
import java.util.List;
@Data
public class InternshipCheckDto {

    private Integer id;

    @ApiModelProperty(value = "学生名称")
    private String studentName;

    @ApiModelProperty(value = "审核状态")
    private Integer status;

    @ApiModelProperty(value = "实习计划")
    private String planTitle;

    @ApiModelProperty(value = "实习单位")
    private String companyName;

    @ApiModelProperty(value = "实习部门")
    private String jobDepartment;

    @ApiModelProperty(value = "岗位名称")
    private String jobName;

    @ApiModelProperty(value = "申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date checkTime;

    @ApiModelProperty(value = "失败原因")
    private String failReason;

    @ApiModelProperty(value = "就业类别")
    private Integer jobCategory;

    @ApiModelProperty(value = "免实习申请原因")
    private String reason;



}
