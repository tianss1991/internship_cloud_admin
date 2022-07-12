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
 * 班级
 * </p>
 *
 * @author mp
 * @since 2022-06-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ClassInfo对象", description="班级")
public class ClassInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号;编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "学校编号;关联学校id")
    @TableField("school_id")
    private Integer schoolId;

    @ApiModelProperty(value = "学校唯一编号;学校唯一编号")
    @TableField("school_code")
    private String schoolCode;

    @ApiModelProperty(value = "二级学院编号;关联二级学院id")
    @TableField("department_id")
    private Integer departmentId;

    @ApiModelProperty(value = "二级学院唯一编号;二级学院唯一编号")
    @TableField("department_code")
    private String departmentCode;

    @ApiModelProperty(value = "专业id")
    @TableField("major_id")
    private Integer majorId;

    @ApiModelProperty(value = "年级id")
    @TableField("grade_id")
    private Integer gradeId;

    @ApiModelProperty(value = "班级名称;班级名称")
    @TableField("class_name")
    private String className;

    @ApiModelProperty(value = "班级唯一编号;班级唯一编号")
    @TableField("class_code")
    private String classCode;

    @ApiModelProperty(value = "班级描述;班级描述")
    @TableField("class_desc")
    private String classDesc;

    @ApiModelProperty(value = "入学时间;入学时间")
    @TableField("class_time")
    private String classTime;

    @ApiModelProperty(value = "创建人;创建人")
    @TableField("create_by")
    private Integer createBy;

    @ApiModelProperty(value = "创建时间;创建时间")
    @TableField(value = "create_datetime", fill = FieldFill.INSERT)
    private Date createDatetime;

    @ApiModelProperty(value = "更新人;更新人")
    @TableField("update_by")
    private Integer updateBy;

    @ApiModelProperty(value = "更新时间;更新时间")
    @TableField(value = "update_datetime", fill = FieldFill.UPDATE)
    private Date updateDatetime;

    @ApiModelProperty(value = "逻辑删除：0=未删除 1=删除;逻辑删除：0=未删除 1=删除")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;


}
