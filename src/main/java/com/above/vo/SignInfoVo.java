package com.above.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author BANGO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class SignInfoVo extends BaseVo{

    @ApiModelProperty("签到/补卡时间【yyyy-MM-dd HH:mm:ss】")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signDateTime;

    @ApiModelProperty("月份【yyyy-MM】")
    @JsonFormat(pattern = "yyyy-MM",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date month;

    @ApiModelProperty("日期【yyyy-MM-mm】")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @ApiModelProperty("实习计划id")
    private Integer planId;

    @ApiModelProperty("签到记录id")
    private Integer signId;

    @ApiModelProperty("补卡申请id")
    private Integer applyId;

    @ApiModelProperty("图片地址JSON字符串")
    private String urlList;

    /*签到字段*/

    @ApiModelProperty("签到地点")
    private String address;

    @ApiModelProperty("备注")
    private String remark;

    /*补签申请*/

    @ApiModelProperty("补签原因")
    private String reason;

    @ApiModelProperty("补签驳回原因")
    private String failReason;

    @ApiModelProperty("是否草稿 0-不是 1-是")
    private Integer isDraft;

    @ApiModelProperty("审核状态 1-通过 2-驳回")
    private Integer auditType;

    /*获取列表*/

    @ApiModelProperty("审核状态 1-已签到/已审核 2-未签到/未审核")
    private Integer studentListType;

}
