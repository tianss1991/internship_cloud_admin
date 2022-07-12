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
 * 用户账号信息表;记录用户关联的手机，邮箱，微信等账号信息）
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserAccount对象", description="用户账号信息表;记录用户关联的手机，邮箱，微信等账号信息）")
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户编号")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty(value = "用户唯一码")
    @TableField("user_code")
    private String userCode;

    @ApiModelProperty(value = "手机号")
    @TableField("telephone")
    private String telephone;

    @ApiModelProperty(value = "邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty(value = "微信")
    @TableField("wechat")
    private String wechat;

    @ApiModelProperty(value = "学生的学号，老师的工号")
    @TableField("account_number")
    private String accountNumber;

    @ApiModelProperty(value = "其他登录信息，以JSON串的方式进行存储")
    @TableField("other_account")
    private String otherAccount;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private Integer createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_datetime", fill = FieldFill.INSERT)
    private Date createDatetime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private Integer updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_datetime", fill = FieldFill.UPDATE)
    private Date updateDatetime;

    @ApiModelProperty(value = "逻辑删除：0=未删除 1=删除")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

    @ApiModelProperty(value = "性别")
    @TableField("gender")
    private String gender;

    @ApiModelProperty(value = "生日")
    @TableField("birth")
    private String birth;

    @ApiModelProperty(value = "个性签名")
    @TableField("sign")
    private String sign;

    @ApiModelProperty(value = "所在地点 areaId")
    @TableField("address_code")
    private Integer addressCode;

    @ApiModelProperty(value = "所在地点")
    @TableField("address")
    private String address;

}
