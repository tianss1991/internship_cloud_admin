package com.above.po;

import com.baomidou.mybatisplus.annotation.IdType;

import com.baomidou.mybatisplus.annotation.*;

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
 * 用户信息表（只存储用户状态与密码）
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "User对象", description = "用户信息表（只存储用户状态与密码）")
public class User implements Serializable {
    /**
     * 逻辑删除：0=未删除，1=删除
     */
    public static final transient Integer UNDELETED = 0;

    public static final transient Integer DELETED = 1;
    /**
     * 用户（禁用）状态：0=正常，1=禁用
     */
    public static final transient Integer NORMAL = 0;

    public static final transient Integer UNNORMAL = 1;

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户（禁用）状态：0=正常，1=禁用")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除：0=未删除，1=删除")
    @TableField(value = "deleted",fill = FieldFill.INSERT)
    private Integer deleted;

    @ApiModelProperty(value = "用户唯一码")
    @TableField("user_code")
    private String userCode;

    @ApiModelProperty(value = "用户类型：0=管理端，1=学生端，2=教师端（关系到具体关联那张表的信息）")
    @TableField("user_type")
    private Integer userType;

    @ApiModelProperty(value = "用户名称")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty(value = "用户头像图片地址")
    @TableField("user_avatar")
    private String userAvatar;

    @ApiModelProperty(value = "密码加盐")
    @TableField("salt")
    private String salt;

    @ApiModelProperty(value = "登录密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private Integer createBy;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value = "create_datetime", fill = FieldFill.INSERT)
    private Date createDatetime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private Integer updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_datetime", fill = FieldFill.UPDATE)
    private Date updateDatetime;

}
