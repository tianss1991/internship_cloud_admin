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
 * 权限表
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AuthPermission对象", description="权限表")
public class AuthPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "权限ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "所属父级权限ID")
    @TableField("parent_id")
    private Integer parentId;

    @ApiModelProperty(value = "权限唯一编码")
    @TableField("permission_code")
    private String permissionCode;

    @ApiModelProperty(value = "权限名称")
    @TableField("permission_name")
    private String permissionName;

    @ApiModelProperty(value = "权限介绍")
    @TableField("permission_introduce")
    private String permissionIntroduce;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private Integer createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_datetime")
    private Date createDatetime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private Integer updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_datetime")
    private Date updateDatetime;

    @ApiModelProperty(value = "逻辑删除：0=未删除，1=删除")
    @TableField(value = "deleted",fill = FieldFill.INSERT)
    private Integer deleted;


}
