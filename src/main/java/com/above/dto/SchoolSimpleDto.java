package com.above.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 返回简单学校信息
 * @Author: LZH
 * @Date: 2022/1/10 19:28
 */
@Data
public class SchoolSimpleDto {

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "学校名称")
    @TableField("school_name")
    private String schoolName;

}
