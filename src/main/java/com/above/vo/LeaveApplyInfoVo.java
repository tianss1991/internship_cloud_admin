package com.above.vo;

import com.above.po.LeaveApplyInfo;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class LeaveApplyInfoVo{

    @ApiModelProperty(value = "申请单id")
    private Integer leaveApplyId;

    @ApiModelProperty(value = "申请单日志id")
    private Integer leaveApplyLogId;

    @ApiModelProperty(value = "审核状态，0为失败，1为通过")
    private Integer checkStatus;

    @ApiModelProperty(value = "是否审核状态，0为未审核，1已审核")
    private Integer isCheck;

    @ApiModelProperty(value = "时长")
    @TableField("duration")
    private String duration;

    @ApiModelProperty(value = "原因")
    @TableField("reason")
    private String reason;

    @ApiModelProperty(value = "请假类型 1-病假 2-事假")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "图片链接(JSON字符串)")
    @TableField("img_url")
    private String imgUrl;

    @ApiModelProperty(value = "驳回原因")
    @TableField("fail_reason")
    private String failReason;

    @ApiModelProperty(value = "搜索关键字")
    private String key;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private Integer createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "通用筛选开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "通用筛选结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "实习计划id")
    private Integer planId;

    @ApiModelProperty(value = "教师id")
    private Integer teacherId;

    @ApiModelProperty(value = "学生id")
    private Integer studentId;

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
