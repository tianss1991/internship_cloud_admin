package com.above.po;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
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
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 工资单
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Payroll对象", description="工资单")
public class Payroll implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "时间")
    @JsonFormat(pattern = "yyyy-MM")
    @DateTimeFormat(pattern = "yyyy-MM")
    @TableField("date_time")
    private Date dateTime;

    @ApiModelProperty(value = "关联实习id")
    @TableField("internship_id")
    private Integer internshipId;

    @ApiModelProperty(value = "学生id")
    @TableField("student_id")
    private Integer studentId;

    @ApiModelProperty(value = "图片地址（JSON格式）")
    @TableField("img_url")
    private String imgUrl;

    @ApiModelProperty(value = "工资")
    @TableField("salary")
    private BigDecimal salary;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private Integer createBy;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private Integer updateBy;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "逻辑删除 0-未删除 1-已删除")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;


}
