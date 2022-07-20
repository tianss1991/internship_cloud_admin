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
 * 成绩模板表
 * </p>
 *
 * @author mp
 * @since 2022-07-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="GradeTemplate对象", description="成绩模板表")
public class GradeTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "父级id")
    @TableField("pid")
    private Integer pid;

    @ApiModelProperty(value = "关联实习计划id")
    @TableField("plan_id")
    private Integer planId;

    @ApiModelProperty(value = "内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "分数")
    @TableField("score")
    private String score;

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

    @ApiModelProperty(value = "逻辑删除：0=未删除 1=删除")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;


}
