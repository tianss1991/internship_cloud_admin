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
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 签到记录表
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SignInfoByStudent对象", description="签到记录表")
public class SignInfoByStudent implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "学生id")
    @TableField("student_id")
    private Integer studentId;

    @ApiModelProperty(value = "关联实习信息id")
    @TableField("internship_id")
    private Integer internshipId;

    @ApiModelProperty(value = "打卡时间 （yyyy-MM-dd HH:mm:ss）")
    @TableField("sign_time")
    private Date signTime;

    @ApiModelProperty(value = "打卡日期 （yyyy-MM-dd）")
    @TableField("sign_date")
    private Date signDate;

    @ApiModelProperty(value = "打卡类型 1-上班打卡 2-下班打卡")
    @TableField("sign_type")
    private Integer signType;

    @ApiModelProperty(value = "是否上午 1-上午 2- 下午")
    @TableField("is_morning")
    private Integer isMorning;

    @ApiModelProperty(value = "打卡状态 1-正常 2-异常（已打卡状态，迟到早退） 3-驳回(申请补卡后被驳回为此状态) 4-补卡 5-缺卡 6-免签 7-请假")
    @TableField("sign_status")
    private Integer signStatus;

    @ApiModelProperty(value = "是否打卡 0-未打卡 1-已打卡")
    @TableField("is_sign")
    private Integer isSign;

    @ApiModelProperty(value = "打卡地址")
    @TableField("sign_address")
    private String signAddress;

    @ApiModelProperty(value = "打卡图片")
    @TableField("sign_img")
    private String signImg;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

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


}
