package com.above.dto;

import com.above.po.LeaveApplyInfo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 请假单审核列表信息
 * @Author: GG
 * @Date: 2022/6/30 11:28
 */
@Data
public class LeaveApplyInfoDto{

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "学生姓名")
    private String studentName;

    @ApiModelProperty(value = "审核状态")
    private Integer status;

    @ApiModelProperty(value = "请假日志表审核状态")
    private Integer leaveApplyLogStatus;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private String endTime;

    @ApiModelProperty(value = "请假类型")
    private Integer type;

    @ApiModelProperty(value = "请假事由")
    private String reason;

    @ApiModelProperty(value = "失败事由")
    private String failReason;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private String updateTime;

    @ApiModelProperty(value = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private String checkTime;

    @ApiModelProperty(value = "请假时长")
    private String duration;

    @ApiModelProperty(value = "通用用户名称")
    private String name;

    @ApiModelProperty(value = "通用用户头像")
    private String avatar;

    @ApiModelProperty(value = "通用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private String time;



}
