package com.above.po;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AuthRole对象", description="角色表")
public class AuthRole implements Serializable {

    /**
     * 超管
     */
    public final static  String ADMIN = "admin";
    public final static  String SCHOOL_ADMIN = "schoolAdmin";
    public final static  String DEPARTMENT_ADMIN = "departmentAdmin";
    /**
     * 辅导员
     */
    public final static  String INSTRUCTOR = "instructor";
    /**
     * 指导老师
     */
    public final static  String ADVISER = "adviser";
    /**
     * 教师
     */
    public final static  String TEACHER = "teacher";
    /**
     * 学生权限
     */
    public final static  String STUDENT = "student";
    /**
     * 游客权限
     */
    public final static  String VISITOR = "visitor";

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "所属父角色ID")
    @TableField("parent_id")
    private Integer parentId;

    @ApiModelProperty(value = "角色唯一编码")
    @TableField("role_code")
    private String roleCode;

    @ApiModelProperty(value = "角色名称")
    @TableField("role_name")
    private String roleName;

    @ApiModelProperty(value = "角色介绍")
    @TableField("role_introduce")
    private String roleIntroduce;

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

    @ApiModelProperty(value = "是否有权限")
    @TableField(exist = false)
    private Boolean permission;


}
