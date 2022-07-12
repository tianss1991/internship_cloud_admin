package com.above.po;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 学校信息
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SchoolInfo对象", description="学校信息")
public class SchoolInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "学校（禁用）状态：0=正常，1=禁用")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "学校名称")
    @TableField("school_name")
    private String schoolName;

    @ApiModelProperty(value = "学校唯一编号")
    @TableField("school_code")
    private String schoolCode;

    @ApiModelProperty(value = "学校logo的url")
    @TableField("school_logo")
    private String schoolLogo;

    @ApiModelProperty(value = "学校描述")
    @TableField("school_desc")
    private String schoolDesc;

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

    @ApiModelProperty(value = "逻辑删除：0=未删除，1=删除")
    @TableField(value = "deleted",fill = FieldFill.INSERT)
    private Integer deleted;


}
