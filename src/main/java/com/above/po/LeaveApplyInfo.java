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
 * 申请请假表
 * </p>
 *
 * @author mp
 * @since 2022-07-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="LeaveApplyInfo对象", description="申请请假表")
public class LeaveApplyInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "申请状态: 0-草稿 1-待审核 2-驳回 3-通过（修改打卡状态）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "时长")
    @TableField("duration")
    private String duration;

    @ApiModelProperty(value = "开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("end_time")
    private Date endTime;

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

    @ApiModelProperty(value = "关联实习计划id")
    @TableField("plan_id")
    private Integer planId;

    @ApiModelProperty(value = "关联学生id")
    @TableField("student_id")
    private Integer studentId;

    @ApiModelProperty(value = "关联实习信息id")
    @TableField("internship_id")
    private Integer internshipId;


}
