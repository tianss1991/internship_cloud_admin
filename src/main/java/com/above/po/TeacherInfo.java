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
 * 教师信息表
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="TeacherInfo对象", description="教师信息表")
public class TeacherInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户编号")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty(value = "学校编号")
    @TableField("school_id")
    private Integer schoolId;

    @ApiModelProperty(value = "学校名称")
    @TableField("school_name")
    private String schoolName;

    @ApiModelProperty(value = "部门编号")
    @TableField("department_id")
    private Integer departmentId;

    @ApiModelProperty(value = "姓名")
    @TableField("teacher_name")
    private String teacherName;

    @ApiModelProperty(value = "性别;0-未设置  1-男  2-女")
    @TableField("gender")
    private Integer gender;

    @ApiModelProperty(value = "工号")
    @TableField("work_number")
    private String workNumber;

    @ApiModelProperty(value = "部门名称")
    @TableField("department_name")
    private String departmentName;

    @ApiModelProperty(value = "教师编制类别")
    @TableField("work_type")
    private String workType;

    @ApiModelProperty(value = "移动电话")
    @TableField("telephone")
    private String telephone;

    @ApiModelProperty(value = "邮箱")
    @TableField("mail")
    private String mail;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private Integer updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_datetime", fill = FieldFill.UPDATE)
    private Date updateDatetime;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private Integer createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_datetime", fill = FieldFill.INSERT)
    private Date createDatetime;

    @ApiModelProperty(value = "是否在职 0-在职 1-离职")
    @TableField("work_status")
    private Integer workStatus;

    @ApiModelProperty(value = "逻辑删除：0=未删除 1=删除")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;


}
