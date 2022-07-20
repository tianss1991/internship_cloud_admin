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
 * 
 * </p>
 *
 * @author mp
 * @since 2022-07-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="InternshipWithStudent对象", description="")
public class InternshipWithStudent implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "关联学生id")
    @TableField("relation_student_id")
    private Integer relationStudentId;

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

    @ApiModelProperty(value = "关联实习计划id")
    @TableField("relation_plan_id")
    private Integer relationPlanId;

    @ApiModelProperty(value = "关联教师id")
    @TableField("relation_teacher_id")
    private Integer relationTeacherId;


}
