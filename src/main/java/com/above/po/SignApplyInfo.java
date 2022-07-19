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
 * 签到申请管理表
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SignApplyInfo对象", description="签到申请管理表")
public class SignApplyInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Integer DRAFT = 0;
    public static final Integer AUDITING = 1;
    public static final Integer FAIL = 2;
    public static final Integer SUCCESS = 3;

    @ApiModelProperty(value = "编号")
    @TableField("id")
    private Integer id;

    @ApiModelProperty(value = "学生id")
    @TableField("student_id")
    private Integer studentId;

    @ApiModelProperty(value = "关联实习id")
    @TableField("internship_id")
    private Integer internshipId;

    @ApiModelProperty(value = "关联实习计划id")
    @TableField("internship_plan_id")
    private Integer internshipPlanId;

    @ApiModelProperty(value = "申请类型 1-补卡 2-请假")
    @TableField("apply_type")
    private Integer applyType;

    @ApiModelProperty(value = "申请时间（yyyy-MM-dd）")
    @TableField("apply_time")
    private Date applyTime;

    @ApiModelProperty(value = "申请原因")
    @TableField("reason")
    private String reason;

    @ApiModelProperty(value = "图片链接(JSON字符串)")
    @TableField("img_url")
    private String imgUrl;

    @ApiModelProperty(value = "打卡id")
    @TableField("sign_id")
    private Integer signId;

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

    @ApiModelProperty(value = "申请状态 1-待审核 2-驳回 3-通过（打卡记录改为正常）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除 0-未删除 1-已删除")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

    @ApiModelProperty(value = "驳回原因")
    @TableField("fail_reason")
    private String failReason;


}
