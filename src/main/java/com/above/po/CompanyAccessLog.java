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
 * 企业寻访记录表
 * </p>
 *
 * @author mp
 * @since 2022-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CompanyAccessLog对象", description="企业寻访记录表")
public class CompanyAccessLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "关联实习计划id")
    @TableField("plan_id")
    private Integer planId;

    @ApiModelProperty(value = "公司名称")
    @TableField("company_name")
    private String companyName;

    @ApiModelProperty(value = "标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "图片地址（JSON格式）")
    @TableField("img_url")
    private String imgUrl;

    @ApiModelProperty(value = "地址")
    @TableField("address")
    private String address;

    @ApiModelProperty(value = "角色名")
    @TableField("role_code")
    private String roleCode;

    @ApiModelProperty(value = "关联教师id")
    @TableField("teacher_id")
    private Integer teacherId;

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
