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
 * 实习成绩日志表
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="InternshipGradeLog对象", description="实习成绩日志表")
public class InternshipGradeLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableField("id")
    private Integer id;

    @ApiModelProperty(value = "关联实习成绩id")
    @TableField("internship_grade_id")
    private String internshipGradeId;

    @ApiModelProperty(value = "关联填写信息（只有企业评分选择）")
    @TableField("internship_info_id")
    private String internshipInfoId;

    @ApiModelProperty(value = "评分类型 1-教师评分 2-企业评分")
    @TableField("type")
    private String type;

    @ApiModelProperty(value = "评分详细信息")
    @TableField("detail")
    private String detail;

    @ApiModelProperty(value = "分数")
    @TableField("score")
    private String score;

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


}
