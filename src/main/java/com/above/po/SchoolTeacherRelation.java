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
 * 学校教师关系表
 * </p>
 *
 * @author mp
 * @since 2022-01-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SchoolTeacherRelation对象", description="学校教师关系表")
public class SchoolTeacherRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "学校编号")
    @TableField("school_id")
    private Integer schoolId;

    @ApiModelProperty(value = "教师编号")
    @TableField("teacher_id")
    private Integer teacherId;

    @ApiModelProperty(value = "关系类型;1-领导")
    @TableField("relation_type")
    private Integer relationType;

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
